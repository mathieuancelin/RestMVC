package cx.ath.mancel01.restmvc;

import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Produces;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RequestScoped
public class FrameworkProducer {

    @Produces
    public HttpServletRequest getRequest() {
        return FrameworkFilter.currentRequest.get();
    }

    @Produces
    public HttpServletResponse getResponse() {
        return FrameworkFilter.currentResponse.get();
    }

}
