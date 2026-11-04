package blooddonation.controller.hopital;

import blooddonation.controller.BaseServlet;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet("/hopital/dons")
public class HopitalDonsServlet extends BaseServlet {

    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setAttribute("dons", app().hopitaux().dons(currentUser(req).getId()));
        req.setAttribute("activeMenu", "dons");
        render(req, resp, "hopital/dons");
    }
}
