package com.dormitory.dto;

import com.dormitory.model.ManagerScope;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class CreateTeacherRequest {
    /** 6位工号 */
    private String employeeNo;
    /** 姓名 */
    private String name;
    private String phone;
    private String email;
    private List<ManagerScope> scopes = new ArrayList<>();
}
