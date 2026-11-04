package blooddonation.utils;

import blooddonation.bean.Demande;
import blooddonation.bean.User;
import blooddonation.service.AuthService;
import blooddonation.service.AuthService.RegistrationRequest;
import blooddonation.service.DonneurService;
import blooddonation.service.HopitalService;
import blooddonation.service.HopitalService.DemandeRequest;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Démarrage / arrêt de l'application.
 * <ul>
 *   <li>initialise Hibernate au démarrage (une erreur de connexion se voit tout de suite) ;</li>
 *   <li>si la variable DEMO_DATA=true, crée des comptes et des demandes de démonstration ;</li>
 *   <li>ferme proprement la SessionFactory à l'arrêt.</li>
 * </ul>
 */
@WebListener
public class AppLifecycleListener implements ServletContextListener {

    private static final Logger LOG = Logger.getLogger(AppLifecycleListener.class.getName());

    static final String DEMO_PASSWORD = "Demo1234!";

    @Override
    public void contextInitialized(ServletContextEvent event) {
        AppContext ctx = AppContext.get();
        HibernateUtil.getSessionFactory();
        if (Env.isTrue("DEMO_DATA")) {
            try {
                seedDemoData(ctx);
            } catch (RuntimeException e) {
                LOG.log(Level.WARNING, "Impossible de créer les données de démonstration", e);
            }
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent event) {
        HibernateUtil.shutdown();
    }

    private static void seedDemoData(AppContext ctx) {
        if (ctx.userDao().findByEmail("hopital@demo.ma").isPresent()) {
            return; // déjà créées
        }
        AuthService auth = ctx.auth();
        User hopital = auth.register(new RegistrationRequest("HOPITAL", "Amina", "Benali", "hopital@demo.ma",
                "0528000000", "Agadir", "Avenue Hassan II", null, "CHR Hassan II Agadir", DEMO_PASSWORD, DEMO_PASSWORD));
        auth.register(new RegistrationRequest("HOPITAL", "Youssef", "Alaoui", "clinique@demo.ma",
                "0522000000", "Casablanca", "Boulevard Zerktouni", null, "Clinique Atlas Casablanca",
                DEMO_PASSWORD, DEMO_PASSWORD));
        User sara = auth.register(new RegistrationRequest("DONNEUR", "Sara", "Idrissi", "donneur@demo.ma",
                "0612345678", "Agadir", null, "O_MOINS", null, DEMO_PASSWORD, DEMO_PASSWORD));
        User karim = auth.register(new RegistrationRequest("DONNEUR", "Karim", "Tazi", "donneur2@demo.ma",
                "0698765432", "Agadir", null, "A_PLUS", null, DEMO_PASSWORD, DEMO_PASSWORD));

        HopitalService hopitaux = ctx.hopitaux();
        int id = hopital.getId();
        Demande critique = hopitaux.creerDemande(id, new DemandeRequest("O_MOINS", "3", "CRITIQUE",
                "Accident de la route, besoin immédiat pour le service des urgences."));
        Demande urgente = hopitaux.creerDemande(id, new DemandeRequest("A_PLUS", "2", "URGENT",
                "Intervention chirurgicale programmée demain matin."));
        hopitaux.creerDemande(id, new DemandeRequest("AB_PLUS", "1", "NORMAL",
                "Reconstitution du stock du service d'hématologie."));

        // Quelques réponses pour que les deux espaces ne soient pas vides au premier lancement
        DonneurService donneurs = ctx.donneurs();
        donneurs.repondre(sara.getId(), critique.getId());
        donneurs.repondre(karim.getId(), urgente.getId());

        LOG.info("Données de démonstration créées (mot de passe : " + DEMO_PASSWORD + ")");
    }
}
