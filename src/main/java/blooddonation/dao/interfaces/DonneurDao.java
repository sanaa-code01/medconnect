package blooddonation.dao.interfaces;

import blooddonation.bean.Donneur;

import java.util.Optional;

public interface DonneurDao {
    Optional<Donneur> findById(int id);
    Donneur update(Donneur donneur);
}
