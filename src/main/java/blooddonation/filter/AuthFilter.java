package blooddonation.filter;

import blooddonation.enums.Role;
import blooddonation.security.SessionUser;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

/**
 * Contrôle d'accès basé sur les rôles (RBAC), appliqué à /donneur/* et /hopital/* (voir web.xml).
 * <ul>
 *   <li>pas connecté : redirection vers la page de connexion ;</li>
 *   <li>connecté avec le mauvais rôle : erreur 403.</li>
 * </ul>
 * Les servlets n'ont donc plus besoin de vérifier la session elles-mêmes.
 */
public class AuthFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;

        HttpSession session = req.getSession(false);
        SessionUser user = session == null ? null : (SessionUser) session.getAttribute(SessionUser.SESSION_KEY);
        if (user == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        String path = req.getRequestURI().substring(req.getContextPath().length());
        Role requis = requiredRole(path);
        if (requis != null && user.getRole() != requis) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Cette page n'est pas accessible avec votre type de compte.");
            return;
        }

        // Pages privées : le navigateur ne doit pas les garder en cache (bouton retour après déconnexion)
        resp.setHeader("Cache-Control", "no-store");
        chain.doFilter(request, response);
    }

    static Role requiredRole(String path) {
        if (path.startsWith("/donneur/")) {
            return Role.DONNEUR;
        }
        if (path.startsWith("/hopital/")) {
            return Role.HOPITAL;
        }
        return null;
    }
}
