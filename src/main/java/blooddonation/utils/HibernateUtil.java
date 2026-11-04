package blooddonation.utils;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

/**
 * Fabrique unique de SessionFactory.
 * Les paramètres de connexion de hibernate.cfg.xml peuvent être remplacés par
 * des variables d'environnement (utile pour Docker) :
 * DB_URL, DB_USER, DB_PASSWORD, HBM2DDL.
 */
public final class HibernateUtil {

    private static volatile SessionFactory sessionFactory;

    private HibernateUtil() { }

    public static SessionFactory getSessionFactory() {
        if (sessionFactory == null) {
            synchronized (HibernateUtil.class) {
                if (sessionFactory == null) {
                    sessionFactory = build();
                }
            }
        }
        return sessionFactory;
    }

    private static SessionFactory build() {
        Configuration cfg = new Configuration().configure("hibernate.cfg.xml");
        override(cfg, "DB_URL", "hibernate.connection.url");
        override(cfg, "DB_USER", "hibernate.connection.username");
        override(cfg, "DB_PASSWORD", "hibernate.connection.password");
        override(cfg, "HBM2DDL", "hibernate.hbm2ddl.auto");
        return cfg.buildSessionFactory();
    }

    private static void override(Configuration cfg, String envName, String property) {
        String value = Env.get(envName);
        if (value != null) {
            cfg.setProperty(property, value);
        }
    }

    public static void shutdown() {
        if (sessionFactory != null) {
            sessionFactory.close();
            sessionFactory = null;
        }
    }
}
