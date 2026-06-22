package com.dormitory.service;

import com.dormitory.dto.CreateTeacherRequest;
import com.dormitory.dto.TeacherVO;
import com.dormitory.mapper.ManagerScopeMapper;
import com.dormitory.mapper.BuildingMapper;
import com.dormitory.mapper.StudentMapper;
import com.dormitory.mapper.TeacherMapper;
import com.dormitory.mapper.UserMapper;
import com.dormitory.model.ManagerScope;
import com.dormitory.model.Teacher;
import com.dormitory.model.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

@Service
public class TeacherService {

    private static final Pattern EMPLOYEE_NO_PATTERN = Pattern.compile("^\\d{6}$");

    private final UserMapper userMapper;
    private final TeacherMapper teacherMapper;
    private final ManagerScopeMapper managerScopeMapper;
    private final BuildingMapper buildingMapper;
    private final StudentMapper studentMapper;
    private final PasswordEncoder passwordEncoder;

    public TeacherService(UserMapper userMapper,
                          TeacherMapper teacherMapper,
                          ManagerScopeMapper managerScopeMapper,
                          BuildingMapper buildingMapper,
                          StudentMapper studentMapper,
                          PasswordEncoder passwordEncoder) {
        this.userMapper = userMapper;
        this.teacherMapper = teacherMapper;
        this.managerScopeMapper = managerScopeMapper;
        this.buildingMapper = buildingMapper;
        this.studentMapper = studentMapper;
        this.passwordEncoder = passwordEncoder;
    }

    public List<TeacherVO> listTeachers() {
        List<TeacherVO> result = new ArrayList<>();
        for (Teacher teacher : teacherMapper.findAll()) {
            result.add(toTeacherVO(teacher));
        }
        return result;
    }

    public TeacherVO findById(Long userId) {
        Teacher teacher = teacherMapper.findByUserId(userId);
        if (teacher == null) {
            User user = userMapper.findById(userId);
            if (user == null || !"MANAGER".equalsIgnoreCase(user.getRole())) {
                throw new RuntimeException("教师不存在");
            }
            teacher = new Teacher();
            teacher.setUserId(userId);
            teacher.setEmployeeNo(user.getUsername());
            teacher.setName(user.getNickname());
            teacher.setPhone(user.getPhone());
            teacher.setEmail(user.getEmail());
            teacher.setStatus(user.getStatus());
            teacher.setUsername(user.getUsername());
            teacher.setNickname(user.getNickname());
        }
        return toTeacherVO(teacher);
    }

    @Transactional
    public TeacherVO createTeacher(CreateTeacherRequest request) {
        String employeeNo = normalizeEmployeeNo(request.getEmployeeNo());
        validateEmployeeNo(employeeNo);

        if (request.getName() == null || request.getName().isBlank()) {
            throw new RuntimeException("请输入姓名");
        }
        if (teacherMapper.findByEmployeeNo(employeeNo) != null) {
            throw new RuntimeException("该工号已存在");
        }
        if (userMapper.countByUsername(employeeNo) > 0) {
            throw new RuntimeException("该工号已注册账号");
        }

        String name = request.getName().trim();

        User user = new User();
        user.setUsername(employeeNo);
        user.setPassword(passwordEncoder.encode(employeeNo));
        user.setNickname(name);
        user.setPhone(request.getPhone());
        user.setEmail(request.getEmail());
        user.setRole("MANAGER");
        user.setStatus(1);
        userMapper.insert(user);

        Teacher teacher = new Teacher();
        teacher.setEmployeeNo(employeeNo);
        teacher.setName(name);
        teacher.setPhone(request.getPhone());
        teacher.setEmail(request.getEmail());
        teacher.setUserId(user.getId());
        teacher.setStatus(1);
        teacherMapper.insert(teacher);

        if (request.getScopes() != null) {
            for (ManagerScope scope : request.getScopes()) {
                saveScope(user.getId(), scope);
            }
        }

        return findById(user.getId());
    }

    @Transactional
    public TeacherVO updateTeacher(Long userId, User update) {
        Teacher teacher = teacherMapper.findByUserId(userId);
        User user = userMapper.findById(userId);
        if (user == null || !"MANAGER".equalsIgnoreCase(user.getRole())) {
            throw new RuntimeException("教师不存在");
        }

        if (update.getNickname() != null) {
            user.setNickname(update.getNickname());
            if (teacher != null) {
                teacher.setName(update.getNickname());
            }
        }
        if (update.getPhone() != null) {
            user.setPhone(update.getPhone());
            if (teacher != null) {
                teacher.setPhone(update.getPhone());
            }
        }
        if (update.getEmail() != null) {
            user.setEmail(update.getEmail());
            if (teacher != null) {
                teacher.setEmail(update.getEmail());
            }
        }
        if (update.getStatus() != null) {
            user.setStatus(update.getStatus());
            if (teacher != null) {
                teacher.setStatus(update.getStatus());
            }
        }
        if (update.getPassword() != null && !update.getPassword().isBlank()) {
            if (update.getPassword().length() < 6) {
                throw new RuntimeException("密码长度至少6位");
            }
            userMapper.updatePassword(userId, passwordEncoder.encode(update.getPassword()));
        }

        userMapper.update(user);
        if (teacher != null) {
            teacherMapper.update(teacher);
        }
        return findById(userId);
    }

    @Transactional
    public TeacherVO ensureTeacher(String employeeNo, String name) {
        String normalized = normalizeEmployeeNo(employeeNo);
        validateEmployeeNo(normalized);

        Teacher existing = teacherMapper.findByEmployeeNo(normalized);
        if (existing != null) {
            return toTeacherVO(existing);
        }

        CreateTeacherRequest request = new CreateTeacherRequest();
        request.setEmployeeNo(normalized);
        request.setName(name);
        return createTeacher(request);
    }

    @Transactional
    public ManagerScope addScope(Long userId, ManagerScope scope) {
        User user = userMapper.findById(userId);
        if (user == null || !"MANAGER".equalsIgnoreCase(user.getRole())) {
            throw new RuntimeException("教师不存在");
        }
        return saveScope(userId, scope);
    }

    @Transactional
    public ManagerScope updateScope(Long scopeId, ManagerScope scope) {
        ManagerScope existing = managerScopeMapper.findById(scopeId);
        if (existing == null || existing.getStatus() == null || existing.getStatus() != 1) {
            throw new RuntimeException("绑定记录不存在");
        }
        validateScope(scope);
        existing.setBuildingId(scope.getBuildingId());
        existing.setClassName(normalizeClassName(scope.getClassName()));
        managerScopeMapper.update(existing);
        return managerScopeMapper.findById(scopeId);
    }

    @Transactional
    public void deleteTeacher(Long userId) {
        User user = userMapper.findById(userId);
        if (user == null || !"MANAGER".equalsIgnoreCase(user.getRole())) {
            throw new RuntimeException("教师不存在");
        }
        managerScopeMapper.disableByUserId(userId);
        teacherMapper.deleteByUserId(userId);
        userMapper.deleteById(userId);
    }

    @Transactional
    public void removeScope(Long scopeId) {
        ManagerScope existing = managerScopeMapper.findById(scopeId);
        if (existing == null) {
            throw new RuntimeException("绑定记录不存在");
        }
        managerScopeMapper.disable(scopeId);
    }

    public List<String> listClassNames() {
        return studentMapper.findDistinctClassNames();
    }

    private ManagerScope saveScope(Long userId, ManagerScope scope) {
        validateScope(scope);
        ManagerScope toSave = new ManagerScope();
        toSave.setUserId(userId);
        toSave.setBuildingId(scope.getBuildingId());
        toSave.setClassName(normalizeClassName(scope.getClassName()));
        toSave.setStatus(1);
        managerScopeMapper.insert(toSave);
        return managerScopeMapper.findById(toSave.getId());
    }

    private void validateScope(ManagerScope scope) {
        boolean hasBuilding = scope.getBuildingId() != null;
        boolean hasClass = scope.getClassName() != null && !scope.getClassName().isBlank();
        if (!hasBuilding && !hasClass) {
            throw new RuntimeException("请至少选择楼栋或班级之一");
        }
        if (hasBuilding && buildingMapper.findById(scope.getBuildingId()) == null) {
            throw new RuntimeException("所选楼栋不存在");
        }
        if (hasClass) {
            String className = scope.getClassName().trim();
            if (studentMapper.countByClassName(className) == 0) {
                throw new RuntimeException("所选班级不存在");
            }
        }
    }

    private String normalizeEmployeeNo(String employeeNo) {
        if (employeeNo == null) {
            return null;
        }
        return employeeNo.trim();
    }

    private void validateEmployeeNo(String employeeNo) {
        if (employeeNo == null || employeeNo.isBlank()) {
            throw new RuntimeException("请输入工号");
        }
        if (!EMPLOYEE_NO_PATTERN.matcher(employeeNo).matches()) {
            throw new RuntimeException("工号必须为6位数字");
        }
    }

    private String normalizeClassName(String className) {
        if (className == null || className.isBlank()) {
            return null;
        }
        return className.trim();
    }

    private TeacherVO toTeacherVO(Teacher teacher) {
        TeacherVO vo = new TeacherVO();
        vo.setTeacherId(teacher.getId());
        vo.setId(teacher.getUserId());
        vo.setEmployeeNo(teacher.getEmployeeNo());
        vo.setUsername(teacher.getEmployeeNo());
        vo.setNickname(teacher.getName());
        vo.setPhone(teacher.getPhone());
        vo.setEmail(teacher.getEmail());
        vo.setStatus(teacher.getStatus());
        if (teacher.getUserId() != null) {
            vo.setScopes(managerScopeMapper.findActiveByUserId(teacher.getUserId()));
        }
        return vo;
    }
}
