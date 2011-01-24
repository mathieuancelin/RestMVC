package cx.ath.mancel01.restmvc.view;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;

public class View extends Renderable {

    private static final Logger logger = Logger.getLogger("View");
    private static final TemplateRenderer renderer = new TemplateRenderer();
    private static final String TYPE = MediaType.TEXT_HTML;
    private final String viewName;
    private final Map<String, Object> context;

    public View(String viewName) {
        this.contentType = TYPE;
        this.viewName = viewName;
        this.context = new HashMap<String, Object>();
    }

    public View() {
        this.contentType = TYPE;
        this.viewName = null;
        this.context = new HashMap<String, Object>();
    }

    public View(String viewName, Map<String, Object> context) {
        this.contentType = TYPE;
        this.viewName = viewName;
        this.context = context;
    }

    public View param(String name, Object value) {
        this.context.put(name, value);
        return this;
    }

    @Override
    public Response render() {
        try {
            long start = System.currentTimeMillis();          
            String name = viewName;
            Throwable t = new Throwable();
            String className = t.getStackTrace()[1].getClassName();
            String methodName = t.getStackTrace()[1].getMethodName();
            if (this.viewName == null) {
                name = methodName + ".html";
            }
            className = className.substring(className.lastIndexOf(".") + 1).toLowerCase();
            name = className + "/" + name;
            String renderText = renderer.render(name, context);
            ResponseBuilder builder = Response.ok(renderText, TYPE);
            logger.log(Level.INFO, "template view rendering : {0} ms.", System.currentTimeMillis() - start);
            return builder.build();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }
}
