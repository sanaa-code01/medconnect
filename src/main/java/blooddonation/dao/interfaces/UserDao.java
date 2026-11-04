package blooddonation.dao.interfaces;

import blooddonation.bean.User;

import java.util.Optional;

public interface UserDao {
    Optional<User> findByEmail(String email);
    Optional<User> findById(int id);
    void save(User user);
}
