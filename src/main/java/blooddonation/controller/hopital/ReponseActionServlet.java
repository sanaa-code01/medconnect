package blooddonation.controller.hopital;

import blooddonation.controller.BaseServlet;
import blooddonation.service.HopitalService;
import blooddonation.service.ServiceException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/** Actions sur la réponse d'un donneur : action=confirmer, refuser ou don. */
@WebServlet("/hopital/reponse/action")
public class ReponseActionServlet extends BaseServlet {

    private static final long serialVersionUID = 1L;

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        int hopitalId = currentUser(req).getId();
        int reponseId = intParam(req, "id").orElse(-1);
        int demandeId = intParam(req, "demandeId").orElse(-1);
        HopitalService service = app().hopitaux();
        String action = String.valueOf(req.getParameter("action"));
        try {
            switch (action) {
                case "confirmer" -> {
                    service.confirmerReponse(hopitalId, reponseId);
                    flashSuccess(req, "Donneur confirmé. Contactez-le pour fixer le rendez-vous.");
                }
                case "refuser" -> {
                    service.refuserReponse(hopitalId, reponseId);
                    flashSuccess(req, "Réponse refusée.");
                }
                case "don" -> {
                    service.enregistrerDon(hopitalId, reponseId, req.getParameter("dateDon"), req.getParameter("lieu"));
                    flashSuccess(req, "Don enregistré. Merci au donneur !");
                }
                default -> throw new ServiceException("Action inconnue.");
            }
        } catch (ServiceException e) {
            flashError(req, e.getMessage());
        }
        redirect(req, resp, demandeId > 0 ? "/hopital/demande?id=" + demandeId : "/hopital/demandes");
    }
}
