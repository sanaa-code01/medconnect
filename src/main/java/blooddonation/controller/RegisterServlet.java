package blooddonation.controller;

import blooddonation.enums.GroupeSang;
import blooddonation.security.SessionUser;
import blooddonation.service.AuthService.RegistrationRequest;
import blooddonation.service.ServiceException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet("/register")
public class RegisterServlet extends BaseServlet {

    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        SessionUser user = currentUser(req);
        if (user != null) {
            redirect(req, resp, user.getRole().getDashboardPath());
            return;
        }
        showForm(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        RegistrationRequest r = new RegistrationRequest(
                req.getParameter("role"),
                req.getParameter("prenom"),
                req.getParameter("nom"),
                req.getParameter("email"),
                req.getParameter("telephone"),
                req.getParameter("ville"),
                req.getParameter("adresse"),
                req.getParameter("groupeSanguin"),
                req.getParameter("nomHopital"),
                req.getParameter("password"),
                req.getParameter("passwordConfirm"));
        try {
            app().auth().register(r);
            flashSuccess(req, "Compte créé avec succès. Vous pouvez vous connecter.");
            redirect(req, resp, "/login");
        } catch (ServiceException e) {
            req.setAttribute("error", e.getMessage());
            showForm(req, resp);
        }
    }

    private void showForm(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setAttribute("groupes", GroupeSang.values());
        render(req, resp, "auth/register");
    }
}
