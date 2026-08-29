package com.dormitory.utils;

import com.dormitory.model.RoommateGroup;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

class RoommateGroupMembersTest {

    @Test
    void emptyGroupAllowsAddingMembers() {
        RoommateGroup group = new RoommateGroup();
        group.setMemberIdList(List.of());
        List<Long> members = group.getMemberIdList();
        assertDoesNotThrow(() -> members.add(99L));
        group.setMemberIdList(members);
        assertEquals(List.of(99L), group.getMemberIdList());
    }
}
