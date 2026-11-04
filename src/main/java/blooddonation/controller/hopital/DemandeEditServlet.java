package blooddonation.controller.hopital;

import blooddonation.controller.BaseServlet;
import blooddonation.service.ServiceException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet("/hopital/demande/modifier")
public class DemandeEditServlet extends BaseServlet {

    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            int id = intParam(req, "id").orElseThrow(() -> new ServiceException("Demande introuvable."));
            show(req, resp, id);
        } catch (ServiceException e) {
            flashError(req, e.getMessage());
            redirect(req, resp, "/hopital/demandes");
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        int hopitalId = currentUser(req).getId();
        int id = intParam(req, "id").orElse(-1);
        try {
            app().hopitaux().modifierDemande(hopitalId, id, DemandesServlet.readForm(req));
            flashSuccess(req, "Demande modifiée.");
            redirect(req, resp, "/hopital/demande?id=" + id);
        } catch (ServiceException e) {
            req.setAttribute("error", e.getMessage());
            try {
                show(req, resp, id);
            } catch (ServiceException notFound) {
                flashError(req, notFound.getMessage());
                redirect(req, resp, "/hopital/demandes");
            }
        }
    }

    private void show(HttpServletRequest req, HttpServletResponse resp, int id) throws ServletException, IOException {
        req.setAttribute("demande", app().hopitaux().getDemande(currentUser(req).getId(), id));
        DemandesServlet.formOptions(req);
        req.setAttribute("activeMenu", "demandes");
        render(req, resp, "hopital/demande-form");
    }
}
