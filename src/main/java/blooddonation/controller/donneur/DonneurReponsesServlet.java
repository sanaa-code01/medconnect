package blooddonation.controller.donneur;

import blooddonation.controller.BaseServlet;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet("/donneur/reponses")
public class DonneurReponsesServlet extends BaseServlet {

    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setAttribute("reponses", app().donneurs().mesReponses(currentUser(req).getId()));
        req.setAttribute("activeMenu", "reponses");
        render(req, resp, "donneur/reponses");
    }
}
