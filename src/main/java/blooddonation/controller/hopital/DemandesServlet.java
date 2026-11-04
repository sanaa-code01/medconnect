package blooddonation.controller.hopital;

import blooddonation.controller.BaseServlet;
import blooddonation.enums.GroupeSang;
import blooddonation.enums.Urgence;
import blooddonation.service.HopitalService;
import blooddonation.service.HopitalService.DemandeRequest;
import blooddonation.service.ServiceException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/** GET : liste des demandes + formulaire de création. POST : publier une demande. */
@WebServlet("/hopital/demandes")
public class DemandesServlet extends BaseServlet {

    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        show(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            app().hopitaux().creerDemande(currentUser(req).getId(), readForm(req));
            flashSuccess(req, "Demande publiée. Les donneurs compatibles peuvent maintenant la voir.");
            redirect(req, resp, "/hopital/demandes");
        } catch (ServiceException e) {
            req.setAttribute("error", e.getMessage());
            req.setAttribute("formOuvert", true);
            show(req, resp);
        }
    }

    static DemandeRequest readForm(HttpServletRequest req) {
        return new DemandeRequest(req.getParameter("groupeSanguin"), req.getParameter("quantite"),
                req.getParameter("urgence"), req.getParameter("description"));
    }

    static void formOptions(HttpServletRequest req) {
        req.setAttribute("groupes", GroupeSang.values());
        req.setAttribute("urgences", Urgence.values());
        req.setAttribute("quantiteMax", HopitalService.QUANTITE_MAX);
    }

    private void show(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setAttribute("demandes", app().hopitaux().demandes(currentUser(req).getId()));
        formOptions(req);
        req.setAttribute("activeMenu", "demandes");
        render(req, resp, "hopital/demandes");
    }
}
