package blooddonation.utils;

import blooddonation.dao.impl.CorrespondanceDaoImpl;
import blooddonation.dao.impl.DemandeDaoImpl;
import blooddonation.dao.impl.DonDaoImpl;
import blooddonation.dao.impl.DonneurDaoImpl;
import blooddonation.dao.impl.HopitalDaoImpl;
import blooddonation.dao.impl.UserDaoImpl;
import blooddonation.dao.interfaces.CorrespondanceDao;
import blooddonation.dao.interfaces.DemandeDao;
import blooddonation.dao.interfaces.DonDao;
import blooddonation.dao.interfaces.DonneurDao;
import blooddonation.dao.interfaces.HopitalDao;
import blooddonation.dao.interfaces.UserDao;
import blooddonation.security.PasswordHasher;
import blooddonation.service.AuthService;
import blooddonation.service.CompatibiliteService;
import blooddonation.service.DonneurService;
import blooddonation.service.EligibiliteService;
import blooddonation.service.HopitalService;

import java.time.Clock;

/**
 * Assemble les DAO et les services une seule fois pour toute l'application
 * (injection de dépendances "à la main", sans framework).
 * Les servlets récupèrent leurs services ici ; les tests, eux, construisent
 * les services directement avec de faux DAO en mémoire.
 */
public final class AppContext {

    private static volatile AppContext instance;

    private final AuthService authService;
    private final DonneurService donneurService;
    private final HopitalService hopitalService;
    private final UserDao userDao;

    private AppContext() {
        Clock clock = Clock.systemDefaultZone();
        userDao = new UserDaoImpl();
        DonneurDao donneurDao = new DonneurDaoImpl();
        HopitalDao hopitalDao = new HopitalDaoImpl();
        DemandeDao demandeDao = new DemandeDaoImpl();
        CorrespondanceDao correspondanceDao = new CorrespondanceDaoImpl();
        DonDao donDao = new DonDaoImpl();

        CompatibiliteService compatibilite = new CompatibiliteService();
        EligibiliteService eligibilite = new EligibiliteService(clock);

        authService = new AuthService(userDao, new PasswordHasher(), clock);
        donneurService = new DonneurService(donneurDao, demandeDao, correspondanceDao, donDao,
                compatibilite, eligibilite, clock);
        hopitalService = new HopitalService(hopitalDao, demandeDao, correspondanceDao, donDao, donneurDao, clock);
    }

    public static AppContext get() {
        if (instance == null) {
            synchronized (AppContext.class) {
                if (instance == null) {
                    instance = new AppContext();
                }
            }
        }
        return instance;
    }

    public AuthService auth() { return authService; }
    public DonneurService donneurs() { return donneurService; }
    public HopitalService hopitaux() { return hopitalService; }
    public UserDao userDao() { return userDao; }
}
