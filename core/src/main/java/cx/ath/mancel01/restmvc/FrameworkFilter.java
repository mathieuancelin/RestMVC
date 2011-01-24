package cx.ath.mancel01.restmvc;

import cx.ath.mancel01.restmvc.http.Session;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.logging.Logger;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebFilter(filterName = "FrameworkFilter", urlPatterns = {"/*"})
public class FrameworkFilter implements Filter {

    public static boolean dev = true;
    public static final Logger logger = Logger.getLogger("RestMVC");
    private static final boolean debug = false;
    private FilterConfig filterConfig = null;
    private static ServletContext context;

    public static ThreadLocal<EntityManager> currentEm =
            new ThreadLocal<EntityManager>();
    public static ThreadLocal<HttpServletRequest> currentRequest =
            new ThreadLocal<HttpServletRequest>();
    public static ThreadLocal<HttpServletResponse> currentResponse =
            new ThreadLocal<HttpServletResponse>();

    @PersistenceContext
    private EntityManager em;

    private void doBeforeProcessing(ServletRequest request, ServletResponse response)
            throws IOException, ServletException {
        if (debug) {
            log("Before request");
        }
    }

    private void doAfterProcessing(ServletRequest request, ServletResponse response)
            throws IOException, ServletException {
        if (debug) {
            log("After request");
        }
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response,
            FilterChain chain)
            throws IOException, ServletException {
        currentEm.set(em);
        currentRequest.set((HttpServletRequest) request);
        currentResponse.set((HttpServletResponse) response);
        Session.current.set(Session.restore());
        doBeforeProcessing(request, response);
        Throwable problem = null;
        try {
            chain.doFilter(request, response);
        } catch (Throwable t) {
            problem = t;
            t.printStackTrace();
            Session.current.remove();
        }
        doAfterProcessing(request, response);
        if (problem != null) {
            if (problem instanceof ServletException) {
                throw (ServletException) problem;
            }
            if (problem instanceof IOException) {
                throw (IOException) problem;
            }
            sendProcessingError(problem, response);
        }
        if (Session.current.get() != null) {
            Session.current.get().save();
            Session.current.remove();
        }
        currentEm.remove();
        currentRequest.remove();
        currentResponse.remove();

    }

    public FilterConfig getFilterConfig() {
        return (this.filterConfig);
    }

    public void setFilterConfig(FilterConfig filterConfig) {
        this.filterConfig = filterConfig;
    }

    @Override
    public void destroy() {
    }

    @Override
    public void init(FilterConfig filterConfig) { 
        this.filterConfig = filterConfig;
        context = filterConfig.getServletContext();
        if (filterConfig != null) {
            log("Initialization of RestMVC framework");
        }
    }

    public static String getContextRoot() {
        return context.getContextPath();
    }

    public static String getFilePath(String name) {
        return context.getRealPath(name);
    }

    public static File getFile(String name) {
        return new File(context.getRealPath(name));
    }

    @Override
    public String toString() {
        if (filterConfig == null) {
            return ("FrameworkFilter()");
        }
        StringBuilder sb = new StringBuilder("FrameworkFilter(");
        sb.append(filterConfig);
        sb.append(")");
        return (sb.toString());
    }

    private void sendProcessingError(Throwable t, ServletResponse response) {
        String stackTrace = getStackTrace(t);
        if (stackTrace != null && !stackTrace.equals("")) {
            try {
                response.setContentType("text/html");
                PrintStream ps = new PrintStream(response.getOutputStream());
                PrintWriter pw = new PrintWriter(ps);
                pw.print("<html>\n<head>\n<title>Error</title>\n</head>\n<body>\n");
                pw.print("<h1>The resource did not process correctly</h1>\n<pre>\n");
                pw.print(stackTrace);
                pw.print("</pre></body>\n</html>");
                pw.close();
                ps.close();
                response.getOutputStream().close();
            } catch (Exception ex) {
            }
        } else {
            try {
                PrintStream ps = new PrintStream(response.getOutputStream());
                t.printStackTrace(ps);
                ps.close();
                response.getOutputStream().close();
            } catch (Exception ex) {
            }
        }
    }

    public static String getStackTrace(Throwable t) {
        String stackTrace = null;
        try {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            t.printStackTrace(pw);
            pw.close();
            sw.close();
            stackTrace = sw.getBuffer().toString();
        } catch (Exception ex) {
        }
        return stackTrace;
    }

    public void log(String msg) {
        logger.info(msg);
    }
}
