package com.dormitory.dto;

import com.dormitory.model.ManagerScope;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class TeacherVO {
    /** users 表 ID，用于范围绑定等操作 */
    private Long id;
    /** managers 表 ID */
    private Long teacherId;
    /** 工号（与 username 相同） */
    private String employeeNo;
    private String username;
    private String nickname;
    private String phone;
    private String email;
    private Integer status;
    private List<ManagerScope> scopes = new ArrayList<>();
}
