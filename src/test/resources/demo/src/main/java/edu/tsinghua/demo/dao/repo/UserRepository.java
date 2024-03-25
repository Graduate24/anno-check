package edu.tsinghua.demo.dao.repo;

import edu.tsinghua.demo.dao.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    List<User> findByName(String name);

    @Query(value = "SELECT * FROM user WHERE name LIKE ?", nativeQuery = true)
    List<User> searchUser(String name);
}
