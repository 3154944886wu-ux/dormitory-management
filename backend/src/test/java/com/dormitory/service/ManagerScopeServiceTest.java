package com.dormitory.service;

import com.dormitory.mapper.ManagerScopeMapper;
import com.dormitory.mapper.RoomMapper;
import com.dormitory.mapper.StudentMapper;
import com.dormitory.model.ManagerScope;
import com.dormitory.model.Room;
import com.dormitory.model.Student;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ManagerScopeServiceTest {

    @Mock
    private ManagerScopeMapper managerScopeMapper;
    @Mock
    private StudentMapper studentMapper;
    @Mock
    private RoomMapper roomMapper;

    private ManagerScopeService service;

    @BeforeEach
    void setUp() {
        service = new ManagerScopeService(managerScopeMapper, studentMapper, roomMapper);
    }

    @Test
    void buildingOnlyMatchesThatBuilding() {
        assertTrue(service.matches(List.of(scope(1L, null)), 1L, "计科1班"));
        assertFalse(service.matches(List.of(scope(1L, null)), 2L, "计科1班"));
    }

    @Test
    void classOnlyMatchesThatClass() {
        assertTrue(service.matches(List.of(scope(null, "计科1班")), 9L, "计科1班"));
        assertFalse(service.matches(List.of(scope(null, "计科1班")), 9L, "计科2班"));
    }

    @Test
    void bothConstraintsMustMatchInSameRow() {
        assertTrue(service.matches(List.of(scope(1L, "计科1班")), 1L, "计科1班"));
        assertFalse(service.matches(List.of(scope(1L, "计科1班")), 1L, "计科2班"));
        assertFalse(service.matches(List.of(scope(1L, "计科1班")), 2L, "计科1班"));
    }

    @Test
    void multipleRowsAreOr() {
        List<ManagerScope> scopes = List.of(scope(1L, null), scope(null, "计科2班"));
        assertTrue(service.matches(scopes, 1L, "其他班"));
        assertTrue(service.matches(scopes, 9L, "计科2班"));
        assertFalse(service.matches(scopes, 9L, "其他班"));
    }

    @Test
    void emptyScopeRejects() {
        assertFalse(service.matches(List.of(), 1L, "计科1班"));
        assertFalse(service.matches(null, 1L, "计科1班"));
    }

    @Test
    void teacherACannotMatchTeacherBStudent() {
        Student studentB = student(20L, 200L, "计科2班");
        Room roomB = room(200L, 2L);
        when(studentMapper.findById(20L)).thenReturn(studentB);
        when(roomMapper.findById(200L)).thenReturn(roomB);
        when(managerScopeMapper.findActiveByUserId(1L)).thenReturn(List.of(scope(1L, null)));

        assertThrows(AccessDeniedException.class,
                () -> service.assertStudentInScope("MANAGER", 1L, 20L));
    }

    @Test
    void teacherCanAccessOwnScopedStudent() {
        Student studentA = student(10L, 100L, "计科1班");
        Room roomA = room(100L, 1L);
        when(studentMapper.findById(10L)).thenReturn(studentA);
        when(roomMapper.findById(100L)).thenReturn(roomA);
        when(managerScopeMapper.findActiveByUserId(1L)).thenReturn(List.of(scope(1L, null)));

        service.assertStudentInScope("MANAGER", 1L, 10L);
    }

    @Test
    void adminBypassesScope() {
        service.assertStudentInScope("ADMIN", 99L, 20L);
    }

    private ManagerScope scope(Long buildingId, String className) {
        ManagerScope scope = new ManagerScope();
        scope.setBuildingId(buildingId);
        scope.setClassName(className);
        scope.setStatus(1);
        return scope;
    }

    private Student student(Long id, Long roomId, String className) {
        Student student = new Student();
        student.setId(id);
        student.setRoomId(roomId);
        student.setClassName(className);
        return student;
    }

    private Room room(Long id, Long buildingId) {
        Room room = new Room();
        room.setId(id);
        room.setBuildingId(buildingId);
        return room;
    }
}
