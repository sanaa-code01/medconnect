package blooddonation.filter;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * Protection CSRF (Cross-Site Request Forgery) par jeton de synchronisation.
 * <p>
 * Un jeton aléatoire est stocké en session et ajouté comme champ caché
 * {@code _csrf} dans chaque formulaire. Toute requête POST sans le bon jeton
 * est rejetée : un site malveillant ne peut donc pas faire publier, confirmer
 * ou supprimer quelque chose à la place d'un utilisateur connecté.
 */
public class CsrfFilter implements Filter {

    public static final String SESSION_ATTR = "csrfToken";
    public static final String PARAM = "_csrf";

    private static final SecureRandom RANDOM = new SecureRandom();

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;

        String path = req.getRequestURI().substring(req.getContextPath().length());
        if (path.startsWith("/assets/")) {
            chain.doFilter(request, response);
            return;
        }

        HttpSession session = req.getSession(true);
        String expected = (String) session.getAttribute(SESSION_ATTR);
        if (expected == null) {
            expected = newToken();
            session.setAttribute(SESSION_ATTR, expected);
        }

        if ("POST".equalsIgnoreCase(req.getMethod()) && !matches(expected, req.getParameter(PARAM))) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Jeton de sécurité invalide. Rechargez la page et réessayez.");
            return;
        }
        chain.doFilter(request, response);
    }

    private static boolean matches(String expected, String actual) {
        if (actual == null) {
            return false;
        }
        return MessageDigest.isEqual(expected.getBytes(StandardCharsets.UTF_8), actual.getBytes(StandardCharsets.UTF_8));
    }

    private static String newToken() {
        byte[] bytes = new byte[32];
        RANDOM.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }
}
