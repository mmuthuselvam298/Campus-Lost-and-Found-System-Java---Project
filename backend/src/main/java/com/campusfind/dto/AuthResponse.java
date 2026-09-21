package com.campusfind.dto;

import com.campusfind.entity.Role;

public class AuthResponse {

    private String token;
    private String tokenType = "Bearer";
    private Long id;
    private String email;
    private String fullName;
    private Role role;
    private String studentStaffId;
    private String department;

    public AuthResponse() {}

    public AuthResponse(String token, Long id, String email, String fullName, Role role, String studentStaffId, String department) {
        this.token = token;
        this.id = id;
        this.email = email;
        this.fullName = fullName;
        this.role = role;
        this.studentStaffId = studentStaffId;
        this.department = department;
    }

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }
    public String getTokenType() { return tokenType; }
    public void setTokenType(String tokenType) { this.tokenType = tokenType; }
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }
    public String getStudentStaffId() { return studentStaffId; }
    public void setStudentStaffId(String studentStaffId) { this.studentStaffId = studentStaffId; }
    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }
}
