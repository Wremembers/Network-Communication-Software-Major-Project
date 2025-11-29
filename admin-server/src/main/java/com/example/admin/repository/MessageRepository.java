package com.example.admin.repository;

import com.example.admin.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long> {
    
    /**
     * 查找两个用户之间的历史消息，按时间升序排序。
     * (senderId=userId1 AND receiverId=userId2) OR (senderId=userId2 AND receiverId=userId1)
     * 这样可以找到两个用户相互发送的所有消息。
     */
    List<Message> findBySenderIdAndReceiverIdOrReceiverIdAndSenderIdOrderByTimestampAsc(
            Long userId1, Long userId2, Long userId3, Long userId4);
            
    /**
     * 查找某个用户收到的所有未读消息。
     * @param receiverId 接收者ID
     * @return 未读消息列表
     */
    List<Message> findByReceiverIdAndIsReadFalse(Long receiverId);
}
