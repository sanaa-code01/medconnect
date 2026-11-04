package blooddonation.controller.donneur;

import blooddonation.controller.BaseServlet;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet("/donneur/dons")
public class DonneurDonsServlet extends BaseServlet {

    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setAttribute("dons", app().donneurs().historique(currentUser(req).getId()));
        req.setAttribute("activeMenu", "dons");
        render(req, resp, "donneur/dons");
    }
}
