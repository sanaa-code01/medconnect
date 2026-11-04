package blooddonation.dao.impl;

import blooddonation.bean.Hopital;
import blooddonation.dao.interfaces.HopitalDao;

import java.util.Optional;

public class HopitalDaoImpl implements HopitalDao {

    @Override
    public Optional<Hopital> findById(int id) {
        return HibernateTx.read(s -> Optional.ofNullable(s.get(Hopital.class, id)));
    }

    @Override
    public Hopital update(Hopital hopital) {
        return HibernateTx.write(s -> s.merge(hopital));
    }
}
