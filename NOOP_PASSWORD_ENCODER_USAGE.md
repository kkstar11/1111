# NoOpPasswordEncoder 配置说明

## 概述

本项目已配置使用 `NoOpPasswordEncoder` 以支持明文密码测试。此配置**仅用于开发测试环境，严禁在生产环境使用**。

## 配置位置

文件：`src/main/java/com/xianyu/config/SecurityConfig.java`

```java
@Bean
@SuppressWarnings("deprecation")
public PasswordEncoder passwordEncoder() {
    return NoOpPasswordEncoder.getInstance();
}
```

## 使用说明

### 1. 数据库配置

在数据库 `student_xianyu` 的 `student_user` 表中，直接在 `password` 列填入明文密码即可：

```sql
-- 示例：创建一个使用明文密码的用户
INSERT INTO student_user (username, password, ...) 
VALUES ('admin', '123456', ...);
```

### 2. 登录测试

用户可以直接使用明文密码登录：
- 用户名：admin
- 密码：123456

系统将直接比对明文密码，无需加密。

## 安全警告

⚠️ **重要安全提示**：

1. **NoOpPasswordEncoder** 不对密码进行任何加密处理
2. 密码以明文形式存储在数据库中
3. 这种配置**严重不安全**，仅适用于：
   - 本地开发环境
   - 功能测试环境
   - 演示环境

4. **生产环境必须使用加密的密码编码器**，例如：
   ```java
   @Bean
   public PasswordEncoder passwordEncoder() {
       return new BCryptPasswordEncoder();
   }
   ```

## 切换到生产配置

在部署到生产环境前，必须：

1. 修改 `SecurityConfig.java` 中的 `passwordEncoder()` 方法，使用 `BCryptPasswordEncoder`
2. 将数据库中所有用户的密码使用 BCrypt 加密后更新
3. 可以使用以下代码生成 BCrypt 密码：
   ```java
   BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
   String hashedPassword = encoder.encode("123456");
   // $2a$10$...（加密后的密码）
   ```

## 其他安全配置

此更改**不影响**以下配置：
- URL 权限控制
- 用户角色管理
- Session 管理
- CSRF 保护设置
- 其他 Spring Security 配置

## 技术说明

- `NoOpPasswordEncoder` 已被 Spring Security 标记为 `@Deprecated`
- 代码中使用 `@SuppressWarnings("deprecation")` 抑制编译警告
- 这是 Spring Security 官方提供的测试用密码编码器
