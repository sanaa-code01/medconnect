package blooddonation.dao.impl;

import blooddonation.bean.User;
import blooddonation.dao.interfaces.UserDao;

import java.util.Optional;

public class UserDaoImpl implements UserDao {

    @Override
    public Optional<User> findByEmail(String email) {
        return HibernateTx.read(s -> s.createQuery("FROM User u WHERE u.email = :email", User.class)
                .setParameter("email", email)
                .uniqueResultOptional());
    }

    @Override
    public Optional<User> findById(int id) {
        return HibernateTx.read(s -> Optional.ofNullable(s.get(User.class, id)));
    }

    @Override
    public void save(User user) {
        HibernateTx.writeVoid(s -> s.persist(user));
    }
}
