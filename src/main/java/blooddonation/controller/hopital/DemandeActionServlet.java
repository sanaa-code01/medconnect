package blooddonation.controller.hopital;

import blooddonation.controller.BaseServlet;
import blooddonation.service.HopitalService;
import blooddonation.service.ServiceException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/** Actions sur une demande : action=annuler ou action=supprimer. */
@WebServlet("/hopital/demande/action")
public class DemandeActionServlet extends BaseServlet {

    private static final long serialVersionUID = 1L;

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        int hopitalId = currentUser(req).getId();
        int id = intParam(req, "id").orElse(-1);
        HopitalService service = app().hopitaux();
        String action = String.valueOf(req.getParameter("action"));
        try {
            switch (action) {
                case "annuler" -> {
                    service.annulerDemande(hopitalId, id);
                    flashSuccess(req, "Demande annulée. Les donneurs qui avaient répondu en sont informés.");
                    redirect(req, resp, "/hopital/demande?id=" + id);
                }
                case "supprimer" -> {
                    service.supprimerDemande(hopitalId, id);
                    flashSuccess(req, "Demande supprimée.");
                    redirect(req, resp, "/hopital/demandes");
                }
                default -> throw new ServiceException("Action inconnue.");
            }
        } catch (ServiceException e) {
            flashError(req, e.getMessage());
            redirect(req, resp, "/hopital/demandes");
        }
    }
}
