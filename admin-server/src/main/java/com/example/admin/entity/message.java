package com.yourproject.entity; // 请确保你的包名正确

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "messages")
@Data
@NoArgsConstructor
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 关联发送者：一个用户可以发送多条消息
    // name = "sender_id" 是数据库中的外键列名
    @ManyToOne 
    @JoinColumn(name = "sender_id", nullable = false)
    private User sender; 

    // 关联接收者：一个用户可以接收多条消息
    @ManyToOne
    @JoinColumn(name = "receiver_id", nullable = false)
    private User receiver;

    // 消息内容，使用 TEXT 类型以支持较长的消息
    @Column(columnDefinition = "TEXT")
    private String content;

    // 消息发送时间
    @Column(nullable = false)
    private LocalDateTime timestamp;

    // 消息类型：TEXT, IMAGE, FILE
    private String type; 
    
    // 是否已读
    private boolean isRead = false;
    
    // 可选：用于群聊或更复杂的会话标识
    private String conversationId;
}
