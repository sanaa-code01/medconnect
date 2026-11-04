package blooddonation.controller;

import blooddonation.security.SessionUser;
import blooddonation.utils.AppContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.Optional;

/**
 * Classe mère des servlets : affichage des vues, redirections, messages flash.
 * <p>
 * Les JSP sont rangées dans /WEB-INF/views : elles ne sont pas accessibles
 * directement par URL, on passe toujours par une servlet (pattern MVC).
 */
public abstract class BaseServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    protected AppContext app() {
        return AppContext.get();
    }

    protected void render(HttpServletRequest req, HttpServletResponse resp, String view)
            throws ServletException, IOException {
        req.getRequestDispatcher("/WEB-INF/views/" + view + ".jsp").forward(req, resp);
    }

    protected void redirect(HttpServletRequest req, HttpServletResponse resp, String path) throws IOException {
        resp.sendRedirect(req.getContextPath() + path);
    }

    /** Utilisateur connecté (garanti non nul sur /donneur/* et /hopital/* grâce à AuthFilter). */
    protected SessionUser currentUser(HttpServletRequest req) {
        HttpSession session = req.getSession(false);
        return session == null ? null : (SessionUser) session.getAttribute(SessionUser.SESSION_KEY);
    }

    protected void flashSuccess(HttpServletRequest req, String message) {
        req.getSession().setAttribute("flashSuccess", message);
    }

    protected void flashError(HttpServletRequest req, String message) {
        req.getSession().setAttribute("flashError", message);
    }

    protected Optional<Integer> intParam(HttpServletRequest req, String name) {
        String value = req.getParameter(name);
        if (value == null) {
            return Optional.empty();
        }
        try {
            return Optional.of(Integer.parseInt(value.trim()));
        } catch (NumberFormatException e) {
            return Optional.empty();
        }
    }
}
