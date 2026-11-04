package blooddonation.controller.donneur;

import blooddonation.controller.BaseServlet;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet("/donneur/dashboard")
public class DonneurDashboardServlet extends BaseServlet {

    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setAttribute("dash", app().donneurs().dashboard(currentUser(req).getId()));
        req.setAttribute("activeMenu", "dashboard");
        render(req, resp, "donneur/dashboard");
    }
}
