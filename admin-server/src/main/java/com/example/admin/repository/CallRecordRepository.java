package com.example.admin.repository;

import com.example.admin.entity.CallRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CallRecordRepository extends JpaRepository<CallRecord, Long> {
    
    /**
     * 查找某个用户所有的通话记录（无论是主叫方还是被叫方），按开始时间降序（最新的在前面）。
     * @param userId1 用户ID (作为主叫方)
     * @param userId2 用户ID (作为被叫方)
     * @return 通话记录列表
     */
    List<CallRecord> findByCallerIdOrCalleeIdOrderByStartTimeDesc(Long userId1, Long userId2);
}
