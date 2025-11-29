package com.yourproject.entity; // 请确保你的包名正确

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "call_records")
@Data
@NoArgsConstructor
public class CallRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 呼叫方
    @ManyToOne 
    @JoinColumn(name = "caller_id", nullable = false)
    private User caller;

    // 被呼叫方
    @ManyToOne
    @JoinColumn(name = "callee_id", nullable = false)
    private User callee;

    @Column(nullable = false)
    private LocalDateTime startTime;

    private LocalDateTime endTime;
    
    // 通话时长，秒
    private Integer durationSeconds;
    
    // 结果：ANSWERED, MISSED, BUSY
    private String result; 
    
    // 媒体类型：AUDIO, VIDEO
    private String mediaType;
}
