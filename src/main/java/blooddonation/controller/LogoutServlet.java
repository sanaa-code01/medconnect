package blooddonation.controller;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

/** Déconnexion en POST uniquement (protégée par le jeton CSRF). */
@WebServlet("/logout")
public class LogoutServlet extends BaseServlet {

    private static final long serialVersionUID = 1L;

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HttpSession session = req.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        flashSuccess(req, "Vous êtes déconnecté.");
        redirect(req, resp, "/login");
    }
}
