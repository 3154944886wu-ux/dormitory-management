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
    /** 可选；为空则系统生成随机初始密码 */
    private String password;
    private List<ManagerScope> scopes = new ArrayList<>();
}
