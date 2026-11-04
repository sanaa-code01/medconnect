package blooddonation.controller.donneur;

import blooddonation.bean.Donneur;
import blooddonation.controller.BaseServlet;
import blooddonation.enums.Disponibilite;
import blooddonation.enums.GroupeSang;
import blooddonation.security.SessionUser;
import blooddonation.service.DonneurService.ProfilRequest;
import blooddonation.service.ServiceException;
import blooddonation.utils.Dates;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet("/donneur/profil")
public class DonneurProfilServlet extends BaseServlet {

    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        showForm(req, resp, app().donneurs().getDonneur(currentUser(req).getId()));
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        int id = currentUser(req).getId();
        ProfilRequest r = new ProfilRequest(
                req.getParameter("prenom"),
                req.getParameter("nom"),
                req.getParameter("telephone"),
                req.getParameter("ville"),
                req.getParameter("adresse"),
                req.getParameter("groupeSanguin"),
                req.getParameter("disponibilite"));
        try {
            Donneur d = app().donneurs().updateProfil(id, r);
            // Le nom affiché en haut de page peut avoir changé
            req.getSession().setAttribute(SessionUser.SESSION_KEY, SessionUser.from(d));
            flashSuccess(req, "Profil mis à jour.");
            redirect(req, resp, "/donneur/profil");
        } catch (ServiceException e) {
            req.setAttribute("error", e.getMessage());
            showForm(req, resp, app().donneurs().getDonneur(id));
        }
    }

    private void showForm(HttpServletRequest req, HttpServletResponse resp, Donneur d)
            throws ServletException, IOException {
        req.setAttribute("donneur", d);
        req.setAttribute("prochaineDate", Dates.fr(app().donneurs().prochaineDatePossible(d)));
        req.setAttribute("groupes", GroupeSang.values());
        req.setAttribute("disponibilites", Disponibilite.values());
        req.setAttribute("activeMenu", "profil");
        render(req, resp, "donneur/profil");
    }
}
