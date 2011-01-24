package cx.ath.mancel01.restmvc;

import cx.ath.mancel01.restmvc.http.Session;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.util.logging.Logger;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Qualifier;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@RequestScoped
public class FrameworkProducer {

    @Target({PARAMETER, FIELD, METHOD, TYPE })
    @Retention(RUNTIME)
    @Documented
    @Qualifier
    public @interface SessionId {}

    @Produces @RequestScoped
    public HttpServletRequest getRequest() {
        System.out.println("get req");
        return FrameworkFilter.currentRequest.get();
    }

    @Produces @RequestScoped
    public HttpServletResponse getResponse() {
        return FrameworkFilter.currentResponse.get();
    }

    @Produces
    public Logger getLogger() {
        return FrameworkFilter.logger;
    }

    @Produces @RequestScoped
    public Session getSession() {
        return Session.current.get();
    }

    @Produces @SessionId 
    public String getSessionId() {
        for (Cookie cook : FrameworkFilter.currentRequest.get().getCookies()) {
            if (Session.SESSION_ID.equals(cook.getName())) {
                return cook.getValue();
            }
        }
        return "none";
    }
}
