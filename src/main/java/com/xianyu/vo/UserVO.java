package com.xianyu.vo;

public class UserVO {
    private Long id;
    private String username;
    private String password;   // 新增
    private String email;
    private String avatarUrl;
    private String studentId;
    private String college;
    private String realName;
    private String phone;
    private String major;
    private String grade;
    private Integer status;
    private Integer role;

    // getter/setter
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }   // 新增
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getAvatarUrl() { return avatarUrl; }
    public void setAvatarUrl(String avatarUrl) { this.avatarUrl = avatarUrl; }
    public String getStudentId() { return studentId; }
    public void setStudentId(String studentId) { this.studentId = studentId; }
    public String getCollege() { return college; }
    public void setCollege(String college) { this.college = college; }
    public String getRealName() { return realName; }
    public void setRealName(String realName) { this.realName = realName; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getMajor() { return major; }
    public void setMajor(String major) { this.major = major; }
    public String getGrade() { return grade; }
    public void setGrade(String grade) { this.grade = grade; }
    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }
    public Integer getRole() { return role; }
    public void setRole(Integer role) { this.role = role; }

    @Override
    public String toString() {
        return "UserVO{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", avatarUrl='" + avatarUrl + '\'' +
                ", studentId='" + studentId + '\'' +
                ", college='" + college + '\'' +
                ", realName='" + realName + '\'' +
                ", phone='" + phone + '\'' +
                ", major='" + major + '\'' +
                ", grade='" + grade + '\'' +
                ", status=" + status +
                ", role=" + role +
                '}';
    }
}