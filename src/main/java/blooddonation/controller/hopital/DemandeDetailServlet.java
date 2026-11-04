package blooddonation.controller.hopital;

import blooddonation.controller.BaseServlet;
import blooddonation.service.ServiceException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.time.LocalDate;

/** Détail d'une demande : donneurs qui ont répondu, confirmation, enregistrement des dons. */
@WebServlet("/hopital/demande")
public class DemandeDetailServlet extends BaseServlet {

    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            int id = intParam(req, "id").orElseThrow(() -> new ServiceException("Demande introuvable."));
            req.setAttribute("detail", app().hopitaux().detail(currentUser(req).getId(), id));
            req.setAttribute("today", LocalDate.now().toString());
            req.setAttribute("activeMenu", "demandes");
            render(req, resp, "hopital/demande-detail");
        } catch (ServiceException e) {
            flashError(req, e.getMessage());
            redirect(req, resp, "/hopital/demandes");
        }
    }
}
