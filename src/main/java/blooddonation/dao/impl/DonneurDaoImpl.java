package blooddonation.dao.impl;

import blooddonation.bean.Donneur;
import blooddonation.dao.interfaces.DonneurDao;

import java.util.Optional;

public class DonneurDaoImpl implements DonneurDao {

    @Override
    public Optional<Donneur> findById(int id) {
        return HibernateTx.read(s -> Optional.ofNullable(s.get(Donneur.class, id)));
    }

    @Override
    public Donneur update(Donneur donneur) {
        return HibernateTx.write(s -> s.merge(donneur));
    }
}
