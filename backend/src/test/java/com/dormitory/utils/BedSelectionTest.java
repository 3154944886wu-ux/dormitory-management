package com.dormitory.utils;

import com.dormitory.model.Bed;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class BedSelectionTest {

    private Bed bed(long id, String type) {
        Bed b = new Bed();
        b.setId(id);
        b.setBedType(type);
        return b;
    }

    @Test
    void prefersBedMatchingPreference() {
        List<Bed> available = List.of(bed(1, "window"), bed(2, "corridor"));
        Bed picked = BedSelection.pick(available, Set.of(), "corridor");
        assertEquals(2L, picked.getId());
    }

    @Test
    void fallsBackToFirstWhenPreferenceNotAvailable() {
        List<Bed> available = List.of(bed(1, "window"), bed(2, "window"));
        Bed picked = BedSelection.pick(available, Set.of(), "corridor");
        assertEquals(1L, picked.getId());
    }

    @Test
    void returnsFirstWhenNoPreference() {
        List<Bed> available = List.of(bed(1, "window"), bed(2, "corridor"));
        assertEquals(1L, BedSelection.pick(available, Set.of(), null).getId());
    }

    @Test
    void excludesReservedBeds() {
        // A(window) 已被本批次其他人推荐 → 应选未被占用的 B(corridor)，即便偏好是 window
        List<Bed> available = List.of(bed(1, "window"), bed(2, "corridor"));
        Bed picked = BedSelection.pick(available, Set.of(1L), "window");
        assertEquals(2L, picked.getId());
    }

    @Test
    void returnsNullWhenAllReserved() {
        List<Bed> available = List.of(bed(1, "window"), bed(2, "corridor"));
        assertNull(BedSelection.pick(available, Set.of(1L, 2L), "window"));
    }

    @Test
    void returnsNullWhenNoBeds() {
        assertNull(BedSelection.pick(List.of(), Set.of(), "window"));
        assertNull(BedSelection.pick(null, null, null));
    }
}
