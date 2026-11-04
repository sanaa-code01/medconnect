package blooddonation.controller.hopital;

import blooddonation.controller.BaseServlet;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet("/hopital/dashboard")
public class HopitalDashboardServlet extends BaseServlet {

    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setAttribute("dash", app().hopitaux().dashboard(currentUser(req).getId()));
        req.setAttribute("activeMenu", "dashboard");
        render(req, resp, "hopital/dashboard");
    }
}
