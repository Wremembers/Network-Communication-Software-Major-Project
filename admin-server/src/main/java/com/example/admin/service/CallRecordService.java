package com.example.admin.service; // 请改成你的实际包名

import com.example.admin.entity.CallRecord;
import com.example.admin.entity.User;
import com.example.admin.repository.CallRecordRepository;
import com.example.admin.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

@Service 
public class CallRecordService {

    @Autowired
    private CallRecordRepository callRecordRepository;
    
    // 我们需要 UserRepository 来查找用户 Entity
    @Autowired 
    private UserRepository userRepository;

    /**
     * 存储一条新的通话记录
     * @param callerId 呼叫方ID
     * @param calleeId 被呼叫方ID
     * @param startTime 通话开始时间
     * @param endTime 通话结束时间 (可选)
     * @param result 通话结果 (ANSWERED, MISSED, BUSY)
     * @param mediaType 媒体类型 (AUDIO, VIDEO)
     * @return 存储后的通话记录对象
     */
    public CallRecord saveCallRecord(
            Long callerId, 
            Long calleeId, 
            LocalDateTime startTime, 
            LocalDateTime endTime, 
            String result, 
            String mediaType) {
        
        // 1. 查找呼叫方和被呼叫方的 Entity 对象
        User caller = userRepository.findById(callerId)
            .orElseThrow(() -> new RuntimeException("Caller not found with ID: " + callerId));
        User callee = userRepository.findById(calleeId)
            .orElseThrow(() -> new RuntimeException("Callee not found with ID: " + calleeId));

        // 2. 计算通话时长 (如果结束时间存在)
        Integer durationSeconds = null;
        if (startTime != null && endTime != null) {
            durationSeconds = (int) (java.time.Duration.between(startTime, endTime).toSeconds());
        }

        // 3. 创建 CallRecord Entity
        CallRecord newRecord = new CallRecord();
        newRecord.setCaller(caller);
        newRecord.setCallee(callee);
        newRecord.setStartTime(startTime);
        newRecord.setEndTime(endTime);
        newRecord.setDurationSeconds(durationSeconds);
        newRecord.setResult(result);
        newRecord.setMediaType(mediaType);
        
        // 4. 保存到数据库
        return callRecordRepository.save(newRecord);
    }

    /**
     * 查询某个用户所有的通话记录
     * @param userId 用户的ID
     * @return 通话记录列表，按时间降序排序
     */
    public List<CallRecord> getCallHistory(Long userId) {
        // 使用 Repository 中定义的查询方法
        return callRecordRepository.findByCallerIdOrCalleeIdOrderByStartTimeDesc(userId, userId);
    }
}
