package blooddonation.filter;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/**
 * Premier filtre de la chaîne (voir web.xml) :
 * encodage UTF-8 des formulaires (accents) et en-têtes HTTP de sécurité.
 */
public class SecurityHeadersFilter implements Filter {

    /** Scripts et styles uniquement depuis l'application : bloque l'injection de scripts externes (XSS). */
    private static final String CSP = "default-src 'self'; "
            + "img-src 'self' data:; "
            + "style-src 'self' 'unsafe-inline'; "
            + "script-src 'self'; "
            + "font-src 'self' data:; "
            + "frame-ancestors 'none'; "
            + "form-action 'self'";

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        request.setCharacterEncoding("UTF-8");
        HttpServletResponse resp = (HttpServletResponse) response;
        resp.setHeader("X-Content-Type-Options", "nosniff");
        resp.setHeader("X-Frame-Options", "DENY");
        resp.setHeader("Referrer-Policy", "same-origin");
        resp.setHeader("Content-Security-Policy", CSP);
        chain.doFilter(request, response);
    }
}
