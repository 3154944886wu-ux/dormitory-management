package com.dormitory.utils;

import com.dormitory.model.Bed;

import java.util.List;
import java.util.Set;

/**
 * 从可用床位中挑选一张分配给学生（纯逻辑，便于单元测试）。
 *
 * 规则：
 * - 排除 reservedBedIds 中已被本批次其他人推荐/占用的床位。
 * - 在剩余候选里优先选择与 preference（window/corridor）一致的床位。
 * - 无匹配偏好则取第一张候选；没有任何候选时返回 null。
 */
public final class BedSelection {

    private BedSelection() {
    }

    public static Bed pick(List<Bed> available, Set<Long> reservedBedIds, String preference) {
        if (available == null || available.isEmpty()) {
            return null;
        }
        Set<Long> reserved = reservedBedIds == null ? Set.of() : reservedBedIds;

        Bed firstCandidate = null;
        for (Bed bed : available) {
            if (bed.getId() != null && reserved.contains(bed.getId())) {
                continue;
            }
            if (firstCandidate == null) {
                firstCandidate = bed;
            }
            if (preference != null && preference.equals(bed.getBedType())) {
                return bed;
            }
        }
        return firstCandidate;
    }
}
