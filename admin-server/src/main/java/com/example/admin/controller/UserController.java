package com.example.admin.controller; // 请确保你的包名正确

import com.example.admin.entity.User;
import com.example.admin.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.Map;

// 告诉 Spring 这是一个 REST API Controller
@RestController 
// 设置所有接口的统一前缀：/api/users
@RequestMapping("/api/users") 
public class UserController {

    // 自动注入你刚才替换好的 UserService
    @Autowired
    private UserService userService;

    // ----- 注册接口 -----
    // 客户端访问地址：POST http://localhost:8080/api/users/register
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Map<String, String> registrationRequest) {
        try {
            // 1. 从客户端请求中取出数据
            String sipUri = registrationRequest.get("sipUri");
            String passwordHash = registrationRequest.get("passwordHash");
            String nickname = registrationRequest.getOrDefault("nickname", "New User");

            // 2. 调用 Service 层的注册逻辑
            User newUser = userService.registerUser(sipUri, passwordHash, nickname);
            
            // 3. 返回成功的响应
            // 我们可以只返回部分安全的信息
            Map<String, Object> response = new HashMap<>();
            response.put("id", newUser.getId());
            response.put("sipUri", newUser.getSipUri());
            response.put("nickname", newUser.getNickname());
            
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            // 4. 如果 Service 抛出异常（比如用户已存在），返回 400 错误
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

    // ----- 登录接口 -----
    // 客户端访问地址：POST http://localhost:8080/api/users/login
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> loginRequest) {
        // 1. 从客户端请求中取出数据
        String sipUri = loginRequest.get("sipUri");
        String passwordHash = loginRequest.get("passwordHash"); 

        // 2. 调用 Service 层的认证逻辑
        User user = userService.authenticate(sipUri, passwordHash);

        if (user != null) {
            // 3. 登录成功，返回用户信息
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Login successful");
            response.put("user_id", user.getId());
            response.put("sip_uri", user.getSipUri());
            response.put("nickname", user.getNickname());
            response.put("status", user.getStatus());
            // TODO: 真实项目中，你需要在这里生成并返回 JWT Token 用于后续的请求认证
            // response.put("token", "YOUR_GENERATED_JWT_TOKEN"); 

            return ResponseEntity.ok(response);
        } else {
            // 4. 登录失败，返回 401 错误
            Map<String, String> error = new HashMap<>();
            error.put("error", "Invalid SIP URI or password.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
        }
    }
    
    // 你可以在这里继续添加用户状态更新、获取联系人列表等接口
}
