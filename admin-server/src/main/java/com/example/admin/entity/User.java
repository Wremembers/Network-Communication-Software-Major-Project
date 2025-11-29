package com.yourproject.entity; // 请确保你的包名正确

import jakarta.persistence.*;
import lombok.Data; // 使用 Lombok 简化代码
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Data // 自动生成 Getter, Setter, toString 等
@NoArgsConstructor // 自动生成无参构造函数
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // SIP 地址，必须唯一且不能为空，作为登录账号
    @Column(unique = true, nullable = false, length = 100)
    private String sipUri;

    // 存储密码的哈希值（已加密的密码）
    @Column(nullable = false, length = 255)
    private String passwordHash;

    private String nickname;
    
    // 用户当前状态：例如: ONLINE, OFFLINE, BUSY
    private String status; 
    
    // 上次登录时间
    private LocalDateTime lastLogin;
    
    // 头像链接
    private String avatarUrl;

    // 方便测试用的构造函数
    public User(String sipUri, String passwordHash, String nickname) {
        this.sipUri = sipUri;
        this.passwordHash = passwordHash;
        this.nickname = nickname;
        this.status = "OFFLINE";
        this.lastLogin = LocalDateTime.now();
    }
}
