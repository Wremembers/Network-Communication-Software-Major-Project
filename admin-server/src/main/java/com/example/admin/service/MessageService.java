package com.example.admin.service; // 请改成你的实际包名

import com.example.admin.entity.Message;
import com.example.admin.entity.User;
import com.example.admin.repository.MessageRepository;
import com.example.admin.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

@Service 
public class MessageService {

    @Autowired
    private MessageRepository messageRepository;
    
    // 我们需要 UserRepository 来确保 senderId 和 receiverId 存在
    @Autowired 
    private UserRepository userRepository;

    /**
     * 存储一条新消息
     * @param senderId 发送者ID
     * @param receiverId 接收者ID
     * @param content 消息内容
     * @param type 消息类型 (TEXT, IMAGE, FILE)
     * @return 存储后的消息对象
     */
    public Message saveMessage(Long senderId, Long receiverId, String content, String type) {
        
        // 1. 查找发送者和接收者的 Entity 对象
        User sender = userRepository.findById(senderId)
            .orElseThrow(() -> new RuntimeException("Sender not found with ID: " + senderId));
        User receiver = userRepository.findById(receiverId)
            .orElseThrow(() -> new RuntimeException("Receiver not found with ID: " + receiverId));

        // 2. 创建 Message Entity
        Message newMessage = new Message();
        newMessage.setSender(sender);
        newMessage.setReceiver(receiver);
        newMessage.setContent(content);
        newMessage.setType(type);
        newMessage.setTimestamp(LocalDateTime.now());
        newMessage.setRead(false); 
        
        // 3. 保存到数据库
        return messageRepository.save(newMessage);
    }

    /**
     * 查询两个用户之间的历史消息
     * @param userId1 用户A的ID
     * @param userId2 用户B的ID
     * @return 消息列表，按时间排序
     */
    public List<Message> getConversationHistory(Long userId1, Long userId2) {
        // 使用 Repository 中定义的复杂查询方法
        return messageRepository.findBySenderIdAndReceiverIdOrReceiverIdAndSenderIdOrderByTimestampAsc(
            userId1, userId2, userId2, userId1);
    }
    
    /**
     * 将某个用户收到的消息标记为已读
     * @param receiverId 接收者ID
     */
    public void markMessagesAsRead(Long receiverId, Long senderId) {
        // 这是一个简化的实现，你可以根据需要优化查询效率
        List<Message> unreadMessages = messageRepository.findByReceiverIdAndIsReadFalse(receiverId);
        for (Message msg : unreadMessages) {
            // 仅标记来自特定发送者的消息
            if (msg.getSender().getId().equals(senderId)) {
                 msg.setRead(true);
                 messageRepository.save(msg);
            }
        }
    }
}
