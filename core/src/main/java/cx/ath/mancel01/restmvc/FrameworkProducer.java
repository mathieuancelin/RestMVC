package cx.ath.mancel01.restmvc;

import cx.ath.mancel01.restmvc.http.Session;
import java.util.logging.Logger;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Any;
import javax.enterprise.inject.Produces;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RequestScoped
public class FrameworkProducer {

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

    @Produces @Any @RequestScoped
    public Session getSession() {
        return Session.current.get();
    }
}
