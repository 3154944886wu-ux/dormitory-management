package com.dormitory.utils;

import com.dormitory.model.Student;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RoommateNamesTest {

    private Student student(long id, String name, String studentNo, String bed) {
        Student s = new Student();
        s.setId(id);
        s.setName(name);
        s.setStudentNo(studentNo);
        s.setBedNumber(bed);
        s.setPhone("1380000000" + id);
        s.setClassName("计科2301");
        return s;
    }

    @Test
    void excludesSelfAndKeepsOthers() {
        List<Map<String, Object>> list = RoommateNames.summaries(
                List.of(student(1, "张三", "20230001", "A"), student(2, "李四", "20230002", "B")),
                1L);
        assertEquals(1, list.size());
        assertEquals("李四", list.get(0).get("name"));
        assertEquals("20230002", list.get(0).get("studentNo"));
        assertEquals("B", list.get(0).get("bedNumber"));
    }

    @Test
    void displayJoinsNames() {
        List<Map<String, Object>> list = RoommateNames.summaries(
                List.of(student(1, "张三", "20230001", "A"), student(2, "李四", "20230002", "B")),
                1L);
        assertEquals("李四", RoommateNames.display(list));
    }

    @Test
    void emptyWhenAloneOrNoRoom() {
        assertTrue(RoommateNames.summaries(List.of(student(1, "张三", "20230001", "A")), 1L).isEmpty());
        assertTrue(RoommateNames.summaries(null, 1L).isEmpty());
        assertEquals("", RoommateNames.display(List.of()));
    }
}
