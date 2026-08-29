package com.dormitory.model;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Data
public class RoommateGroup {
    private Long id;
    private Long batchId;
    private Long roomId;
    private String memberIds;
    private LocalDateTime createTime;

    public List<Long> getMemberIdList() {
        if (memberIds == null || memberIds.isBlank()) {
            return new ArrayList<>();
        }
        return Arrays.stream(memberIds.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .map(Long::valueOf)
                .collect(Collectors.toList());
    }

    public void setMemberIdList(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            this.memberIds = null;
        } else {
            this.memberIds = ids.stream()
                    .map(String::valueOf)
                    .collect(Collectors.joining(","));
        }
    }
}
