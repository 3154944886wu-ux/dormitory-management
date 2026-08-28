package com.dormitory.api;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.nio.charset.StandardCharsets;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ApiContractTest {

    static {
        ApiSchemaBootstrap.ensurePrepared();
    }

    @DynamicPropertySource
    static void isolateTestDatabase(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", () -> ApiSchemaBootstrap.JDBC_DB);
        registry.add("spring.datasource.username", () -> "root");
        registry.add("spring.datasource.password", () -> "");
        registry.add("app.init-admin.enabled", () -> "false");
        registry.add("app.seed-demo.enabled", () -> "false");
        registry.add("spring.task.scheduling.enabled", () -> "false");
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private String adminToken;
    private String studentToken;
    private String managerToken;
    private String otherStudentToken;

    @BeforeAll
    void loginAll() throws Exception {
        adminToken = login("admin", "admin123");
        studentToken = login("20230001", "123456");
        managerToken = login("mgr_b1", "mgrpass1");
        otherStudentToken = login("20230003", "123456");
    }

    @Test
    void unauthenticatedDashboardIs401() throws Exception {
        mockMvc.perform(get("/api/dashboard/overview"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value(401));
    }

    @Test
    void unauthenticatedUploadsIs401() throws Exception {
        mockMvc.perform(get("/uploads/leave/secret.png"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value(401));
    }

    @Test
    void studentCannotEnumerateCampusBuildings() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/buildings").header("Authorization", bearer(studentToken)))
                .andExpect(status().isOk())
                .andReturn();
        JsonNode list = objectMapper.readTree(result.getResponse().getContentAsString(StandardCharsets.UTF_8)).get("data");
        assertTrue(list.isArray());
        assertTrue(list.size() <= 1);
    }

    @Test
    void disabledUserTokenIsRejected() throws Exception {
        jdbcTemplate.update("UPDATE users SET status = 0 WHERE username = '20230003'");
        try {
            mockMvc.perform(get("/api/auth/me").header("Authorization", bearer(otherStudentToken)))
                    .andExpect(status().isUnauthorized());
        } finally {
            jdbcTemplate.update("UPDATE users SET status = 1 WHERE username = '20230003'");
        }
    }

    @Test
    void studentCannotEnumerateRoomsOrBeds() throws Exception {
        mockMvc.perform(get("/api/rooms").header("Authorization", bearer(studentToken)))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value(403));
        mockMvc.perform(get("/api/beds/available/1").header("Authorization", bearer(studentToken)))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value(403));
    }

    @Test
    void studentCannotQueryAnotherStudent() throws Exception {
        mockMvc.perform(get("/api/students/no/20230003").header("Authorization", bearer(studentToken)))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value(403));
    }

    @Test
    void studentCanViewOwnRoomInspectionButNotOtherRoom() throws Exception {
        mockMvc.perform(get("/api/inspection/records/room/1").header("Authorization", bearer(studentToken)))
                .andExpect(status().isOk());
        mockMvc.perform(get("/api/inspection/records/room/2").header("Authorization", bearer(studentToken)))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value(403));
    }

    @Test
    void occupancyUsesLiveCountEvenWhenCurrentCountIsZero() throws Exception {
        Integer drifted = jdbcTemplate.queryForObject(
                "SELECT current_count FROM rooms WHERE id = 1", Integer.class);
        assertEquals(0, drifted);

        MvcResult result = mockMvc.perform(get("/api/students/no/20230001")
                        .header("Authorization", bearer(studentToken)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.occupancy").value(2))
                .andReturn();
        JsonNode data = objectMapper.readTree(result.getResponse().getContentAsString(StandardCharsets.UTF_8)).get("data");
        assertEquals(2, data.get("occupancy").asInt());

        MvcResult rooms = mockMvc.perform(get("/api/rooms").header("Authorization", bearer(adminToken)))
                .andExpect(status().isOk())
                .andReturn();
        JsonNode list = objectMapper.readTree(rooms.getResponse().getContentAsString(StandardCharsets.UTF_8))
                .at("/data/list");
        JsonNode room1 = null;
        for (JsonNode node : list) {
            if (node.path("id").asLong() == 1L) {
                room1 = node;
                break;
            }
        }
        assertTrue(room1 != null, "管理员房间列表应包含 id=1");
        assertEquals(2, room1.get("occupancy").asInt());
        assertEquals(0, room1.path("currentCount").asInt());
    }

    @Test
    void managerDashboardAndListsAreScopedToBuilding() throws Exception {
        JsonNode adminOverview = readData(getJson("/api/dashboard/overview", adminToken));
        JsonNode managerOverview = readData(getJson("/api/dashboard/overview", managerToken));
        assertEquals(2, adminOverview.get("buildingCount").asInt());
        assertEquals(1, managerOverview.get("buildingCount").asInt());
        assertTrue(adminOverview.get("studentCount").asInt() > managerOverview.get("studentCount").asInt());

        JsonNode adminLeaves = objectMapper.readTree(getJson("/api/leave-requests/pending", adminToken));
        JsonNode managerLeaves = objectMapper.readTree(getJson("/api/leave-requests/pending", managerToken));
        assertEquals(2, adminLeaves.get("total").asInt());
        assertEquals(1, managerLeaves.get("total").asInt());

        JsonNode adminFees = objectMapper.readTree(getJson("/api/utility-fees", adminToken)).get("data");
        JsonNode managerFees = objectMapper.readTree(getJson("/api/utility-fees", managerToken)).get("data");
        assertEquals(2, adminFees.size());
        assertEquals(1, managerFees.size());
        assertEquals(1L, managerFees.get(0).path("buildingId").asLong());
    }

    @Test
    void putUsersMeIgnoresPassword() throws Exception {
        String before = jdbcTemplate.queryForObject(
                "SELECT password FROM users WHERE username = '20230001'", String.class);
        mockMvc.perform(put("/api/users/me")
                        .header("Authorization", bearer(studentToken))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "nickname", "张伟",
                                "password", "hacked-password"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
        String after = jdbcTemplate.queryForObject(
                "SELECT password FROM users WHERE username = '20230001'", String.class);
        assertEquals(before, after);
        login("20230001", "123456");
    }

    @Test
    void emptyBatchTriggerMatchingReturnsHttp400WithoutDoublePrefix() throws Exception {
        MvcResult result = mockMvc.perform(put("/api/batches/1/trigger-matching")
                        .header("Authorization", bearer(adminToken)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400))
                .andReturn();
        String json = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
        String message = objectMapper.readTree(json).path("message").asText();
        assertTrue(message.contains("匹配失败"), message);
        assertFalse(message.contains("匹配失败: 匹配失败"), message);
        assertEquals("running", jdbcTemplate.queryForObject(
                "SELECT match_status FROM dorm_batch WHERE id = 1", String.class));
    }

    @Test
    void studentDashboardIsForbidden() throws Exception {
        mockMvc.perform(get("/api/dashboard/overview").header("Authorization", bearer(otherStudentToken)))
                .andExpect(status().isForbidden());
    }

    private String login(String username, String password) throws Exception {
        MvcResult result = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "username", username,
                                "password", password))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andReturn();
        return objectMapper.readTree(result.getResponse().getContentAsString(StandardCharsets.UTF_8))
                .at("/data/token").asText();
    }

    private String getJson(String path, String token) throws Exception {
        return mockMvc.perform(get(path).header("Authorization", bearer(token)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);
    }

    private JsonNode readData(String json) throws Exception {
        return objectMapper.readTree(json).get("data");
    }

    private static String bearer(String token) {
        return "Bearer " + token;
    }
}
