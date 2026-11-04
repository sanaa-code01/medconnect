package blooddonation.controller.donneur;

import blooddonation.bean.Donneur;
import blooddonation.controller.BaseServlet;
import blooddonation.utils.Dates;
import blooddonation.service.ServiceException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/**
 * GET  : demandes compatibles avec le groupe du donneur (filtre « ma ville » en option).
 * POST : bouton « Je veux donner ».
 */
@WebServlet("/donneur/annonces")
public class DonneurAnnoncesServlet extends BaseServlet {

    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        int donneurId = currentUser(req).getId();
        boolean maVille = "1".equals(req.getParameter("ville"));
        req.setAttribute("annonces", app().donneurs().annonces(donneurId, maVille));
        Donneur donneur = app().donneurs().getDonneur(donneurId);
        req.setAttribute("donneur", donneur);
        req.setAttribute("eligible", app().donneurs().estEligible(donneur));
        req.setAttribute("prochaineDate", Dates.fr(app().donneurs().prochaineDatePossible(donneur)));
        req.setAttribute("groupesCompatibles", app().donneurs().groupesCompatibles(donneur));
        req.setAttribute("maVille", maVille);
        req.setAttribute("activeMenu", "annonces");
        render(req, resp, "donneur/annonces");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            int demandeId = intParam(req, "demandeId").orElseThrow(() -> new ServiceException("Demande invalide."));
            app().donneurs().repondre(currentUser(req).getId(), demandeId);
            flashSuccess(req, "Merci ! L'hôpital a reçu votre proposition de don et va vous contacter.");
        } catch (ServiceException e) {
            flashError(req, e.getMessage());
        }
        redirect(req, resp, "/donneur/annonces");
    }
}
