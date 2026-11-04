package blooddonation.dao.impl;

import blooddonation.utils.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Petit utilitaire qui évite de répéter dans chaque DAO l'ouverture de session,
 * la transaction, le commit / rollback et la fermeture.
 */
final class HibernateTx {

    private HibernateTx() { }

    /** Lecture seule. */
    static <T> T read(Function<Session, T> work) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return work.apply(session);
        }
    }

    /** Écriture dans une transaction : commit si tout va bien, rollback sinon. */
    static <T> T write(Function<Session, T> work) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();
            try {
                T result = work.apply(session);
                tx.commit();
                return result;
            } catch (RuntimeException e) {
                if (tx.isActive()) {
                    tx.rollback();
                }
                throw e;
            }
        }
    }

    static void writeVoid(Consumer<Session> work) {
        write(session -> {
            work.accept(session);
            return null;
        });
    }
}
