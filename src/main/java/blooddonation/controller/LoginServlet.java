package blooddonation.controller;

import blooddonation.bean.User;
import blooddonation.security.SessionUser;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.Optional;

@WebServlet("/login")
public class LoginServlet extends BaseServlet {

    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        SessionUser user = currentUser(req);
        if (user != null) {
            redirect(req, resp, user.getRole().getDashboardPath());
            return;
        }
        render(req, resp, "auth/login");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Optional<User> user = app().auth().authenticate(req.getParameter("email"), req.getParameter("password"));
        if (user.isEmpty()) {
            // Message volontairement vague : on ne dit pas si c'est l'e-mail ou le mot de passe qui est faux
            req.setAttribute("error", "E-mail ou mot de passe incorrect.");
            render(req, resp, "auth/login");
            return;
        }

        // Nouvelle session après connexion : empêche la fixation de session
        HttpSession old = req.getSession(false);
        if (old != null) {
            old.invalidate();
        }
        HttpSession session = req.getSession(true);
        SessionUser sessionUser = SessionUser.from(user.get());
        session.setAttribute(SessionUser.SESSION_KEY, sessionUser);
        flashSuccess(req, "Bienvenue, " + sessionUser.getDisplayName() + " !");
        redirect(req, resp, sessionUser.getRole().getDashboardPath());
    }
}
