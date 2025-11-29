package com.example.admin.repository;

import com.example.admin.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

// 这是一个接口，Spring Data JPA 会自动帮我们实现里面的方法。
// 它可以对 User 实体进行基本的数据库操作（增删改查）。
public interface UserRepository extends JpaRepository<User, Long> {
    
    /**
     * 根据用户的 SIP URI 查找用户。
     * Spring Data JPA 强大的地方在于：只需要写对方法名，它就能自动生成 SQL 查询。
     * @param sipUri 用户的 SIP 地址
     * @return 查找到的用户对象
     */
    User findBySipUri(String sipUri);
}
