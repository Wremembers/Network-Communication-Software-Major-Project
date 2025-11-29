package com.example.admin.controller; // 请改成你的实际包名

import com.example.admin.entity.CallRecord;
import com.example.admin.service.CallRecordService;
import com.example.admin.service.UserService;
import com.example.admin.dto.ApiResponse;
import com.example.admin.util.JwtUtil; // 用于从 Token 中获取用户信息
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController 
@RequestMapping("/api/calls") // 通话记录的统一前缀 /api/calls
@CrossOrigin(origins = "*")
public class CallRecordController {

    // 自动注入 Service 层
    @Autowired
    private CallRecordService callRecordService;
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private JwtUtil jwtUtil; // 用于解析 Token

    // 辅助方法：从 Authorization Header 中提取 Token
    private String extractToken(String authHeader) {
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        return null;
    }

    // 格式化时间字符串
    private static final DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;


    /**
     * 存储新的通话记录 (呼叫结束或未接时调用)
     * POST /api/calls/record
     */
    @PostMapping("/record")
    public ApiResponse<CallRecord> saveRecord(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody CallRecordRequest request) {
        try {
            String token = extractToken(authHeader);
            if (token == null) {
                return ApiResponse.error("无效的 Authorization 头");
            }
            
            // 1. 获取用户 ID
            Long callerId = userService.findBySipUri(request.getCallerSipUri()).getId();
            Long calleeId = userService.findBySipUri(request.getCalleeSipUri()).getId();
            
            if (callerId == null || calleeId == null) {
                 return ApiResponse.error("呼叫方或被呼叫方不存在");
            }

            // 2. 转换时间字符串为 LocalDateTime
            LocalDateTime startTime = LocalDateTime.parse(request.getStartTime(), formatter);
            LocalDateTime endTime = request.getEndTime() != null ? LocalDateTime.parse(request.getEndTime(), formatter) : null;

            // 3. 调用 Service 存储记录
            CallRecord newRecord = callRecordService.saveCallRecord(
                callerId, 
                calleeId, 
                startTime, 
                endTime, 
                request.getResult(), 
                request.getMediaType()
            );
            
            return ApiResponse.success(newRecord, "通话记录保存成功");
            
        } catch (Exception e) {
            // 捕获异常（比如用户ID不存在或时间格式错误）
            return ApiResponse.error("保存通话记录失败: " + e.getMessage());
        }
    }

    /**
     * 获取用户通话历史记录
     * GET /api/calls/history
     */
    @GetMapping("/history")
    public ApiResponse<List<CallRecordResponse>> getHistory(@RequestHeader("Authorization") String authHeader) {
        try {
            String token = extractToken(authHeader);
            if (token == null) {
                return ApiResponse.error("无效的 Authorization 头");
            }
            
            // 1. 获取当前登录用户的 SipUri，然后查找 ID
            String currentSipUri = jwtUtil.getUserIdFromToken(token);
            Long userId = userService.findBySipUri(currentSipUri).getId();
            
            // 2. 从数据库获取通话历史
            List<CallRecord> dbRecords = callRecordService.getCallHistory(userId);
            
            // 3. 将数据库实体转换为 API 响应所需的 DTO 结构
            List<CallRecordResponse> responseList = dbRecords.stream().map(dbRecord -> {
                CallRecordResponse response = new CallRecordResponse();
                response.setRecordId(dbRecord.getId());
                response.setCallerSipUri(dbRecord.getCaller().getSipUri());
                response.setCalleeSipUri(dbRecord.getCallee().getSipUri());
                response.setStartTime(dbRecord.getStartTime().format(formatter));
                response.setEndTime(dbRecord.getEndTime() != null ? dbRecord.getEndTime().format(formatter) : null);
                response.setDurationSeconds(dbRecord.getDurationSeconds());
                response.setResult(dbRecord.getResult());
                response.setMediaType(dbRecord.getMediaType());
                return response;
            }).collect(Collectors.toList());

            return ApiResponse.success(responseList, "获取通话历史成功");

        } catch (Exception e) {
            return ApiResponse.error("获取通话历史失败: " + e.getMessage());
        }
    }

    // --- DTO 类 (用于接收请求和发送响应) ---

    public static class CallRecordRequest {
        private String callerSipUri;
        private String calleeSipUri;
        private String startTime; // ISO_LOCAL_DATE_TIME 格式字符串
        private String endTime;   // ISO_LOCAL_DATE_TIME 格式字符串 (可选)
        private String result;    // ANSWERED, MISSED, BUSY
        private String mediaType; // AUDIO, VIDEO
        
        // Getters and Setters
        public String getCallerSipUri() { return callerSipUri; }
        public void setCallerSipUri(String callerSipUri) { this.callerSipUri = callerSipUri; }
        public String getCalleeSipUri() { return calleeSipUri; }
        public void setCalleeSipUri(String calleeSipUri) { this.calleeSipUri = calleeSipUri; }
        public String getStartTime() { return startTime; }
        public void setStartTime(String startTime) { this.startTime = startTime; }
        public String getEndTime() { return endTime; }
        public void setEndTime(String endTime) { this.endTime = endTime; }
        public String getResult() { return result; }
        public void setResult(String result) { this.result = result; }
        public String getMediaType() { return mediaType; }
        public void setMediaType(String mediaType) { this.mediaType = mediaType; }
    }
    
    public static class CallRecordResponse {
        private Long recordId;
        private String callerSipUri;
        private String calleeSipUri;
        private String startTime;
        private String endTime;
        private Integer durationSeconds;
        private String result;
        private String mediaType;

        // Getters and Setters
        public Long getRecordId() { return recordId; }
        public void setRecordId(Long recordId) { this.recordId = recordId; }
        public String getCallerSipUri() { return callerSipUri; }
        public void setCallerSipUri(String callerSipUri) { this.callerSipUri = callerSipUri; }
        public String getCalleeSipUri() { return calleeSipUri; }
        public void setCalleeSipUri(String calleeSipUri) { thiseeSipUri = calleeSipUri; }
        public String getStartTime() { return startTime; }
        public void setStartTime(String startTime) { this.startTime = startTime; }
        public String getEndTime() { return endTime; }
        public void setEndTime(String endTime) { this.endTime = endTime; }
        public Integer getDurationSeconds() { return durationSeconds; }
        public void setDurationSeconds(Integer durationSeconds) { this.durationSeconds = durationSeconds; }
        public String getResult() { return result; }
        public void setResult(String result) { this.result = result; }
        public String getMediaType() { return mediaType; }
        public void setMediaType(String mediaType) { this.mediaType = mediaType; }
    }
}
