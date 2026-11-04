package blooddonation.controller.hopital;

import blooddonation.bean.Hopital;
import blooddonation.controller.BaseServlet;
import blooddonation.security.SessionUser;
import blooddonation.service.HopitalService.ProfilRequest;
import blooddonation.service.ServiceException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet("/hopital/profil")
public class HopitalProfilServlet extends BaseServlet {

    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        show(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        ProfilRequest r = new ProfilRequest(
                req.getParameter("nomHopital"),
                req.getParameter("prenom"),
                req.getParameter("nom"),
                req.getParameter("telephone"),
                req.getParameter("ville"),
                req.getParameter("adresse"));
        try {
            Hopital h = app().hopitaux().updateProfil(currentUser(req).getId(), r);
            req.getSession().setAttribute(SessionUser.SESSION_KEY, SessionUser.from(h));
            flashSuccess(req, "Profil mis à jour.");
            redirect(req, resp, "/hopital/profil");
        } catch (ServiceException e) {
            req.setAttribute("error", e.getMessage());
            show(req, resp);
        }
    }

    private void show(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setAttribute("hopital", app().hopitaux().getHopital(currentUser(req).getId()));
        req.setAttribute("activeMenu", "profil");
        render(req, resp, "hopital/profil");
    }
}
