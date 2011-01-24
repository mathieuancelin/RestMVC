package cx.ath.mancel01.restmvc.utils;

import java.io.File;
import java.io.IOException;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(name="UtilServlet", urlPatterns={"/framework/util/uselessservlet"}, loadOnStartup=1)
public class UtilServlet extends HttpServlet {

    private static String contextRoot;

    private static ServletContext context;

    public static String getContextRoot() {
        return contextRoot;
    }

    public static String getFilePath(String name) {
        return context.getRealPath(name);
    }

    public static File getFile(String name) {
        return new File(context.getRealPath(name));
    }

    @Override
    public void init() throws ServletException {
        super.init();
        System.out.println("Init RestMVC framework");
        contextRoot = getServletContext().getContextPath();
        context = getServletContext();
    }

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        response.sendError(HttpServletResponse.SC_FORBIDDEN);
    } 

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        processRequest(request, response);
    } 

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        processRequest(request, response);
    }

}
