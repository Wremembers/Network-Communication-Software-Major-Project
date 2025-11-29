package com.example.admin.service; 

import com.example.admin.entity.User;
import com.example.admin.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;

// 这是 Service 层注解，告诉 Spring 这是一个业务逻辑处理类
@Service 
public class UserService {

    // 自动注入你之前创建的 UserRepository
    @Autowired
    private UserRepository userRepository;

    /**
     * 注册新用户
     * @param sipUri 用户的 SIP 地址
     * @param passwordHash 密码的哈希值
     * @param nickname 用户的昵称
     * @return 成功注册的用户对象
     */
    public User registerUser(String sipUri, String passwordHash, String nickname) {
        // 检查用户是否已存在
        if (userRepository.findBySipUri(sipUri) != null) {
            // 如果用户已存在，抛出异常
            throw new RuntimeException("User with SIP URI " + sipUri + " already exists.");
        }

        // 创建新的 User Entity
        User newUser = new User();
        newUser.setSipUri(sipUri);
        newUser.setPasswordHash(passwordHash);
        newUser.setNickname(nickname);
        newUser.setStatus("OFFLINE"); // 默认离线
        newUser.setLastLogin(LocalDateTime.now());
        
        // 保存到数据库
        return userRepository.save(newUser);
    }

    /**
     * 用户登录认证（简化版）
     * @param sipUri SIP 地址
     * @param providedHash 客户端提供的密码哈希值
     * @return 登录成功的用户对象
     */
    public User authenticate(String sipUri, String providedHash) {
        User user = userRepository.findBySipUri(sipUri);
        
        // 1. 检查用户是否存在
        if (user == null) {
            return null; // 用户不存在
        }
        
        // 2. 检查密码是否匹配 (真实项目中需要使用 BCryptPasswordEncoder 进行比对)
        if (user.getPasswordHash().equals(providedHash)) {
            // 登录成功，更新用户状态为 ONLINE
            user.setStatus("ONLINE");
            user.setLastLogin(LocalDateTime.now());
            userRepository.save(user); // 保存更新后的状态
            return user;
        } else {
            return null; // 密码不匹配
        }
    }
    
    /**
     * 根据 SIP URI 查找用户
     */
    public User findBySipUri(String sipUri) {
        return userRepository.findBySipUri(sipUri);
    }
    
    // 你可以在这里添加更多方法，比如更新状态、查找所有联系人等
}
