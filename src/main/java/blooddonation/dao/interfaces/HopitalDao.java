package blooddonation.dao.interfaces;

import blooddonation.bean.Hopital;

import java.util.Optional;

public interface HopitalDao {
    Optional<Hopital> findById(int id);
    Hopital update(Hopital hopital);
}
