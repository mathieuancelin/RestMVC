package cx.ath.mancel01.restmvc.view;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 *
 * @author mathieuancelin
 */
public abstract class Renderable {

    protected int statusCode = 200;
    protected String contentType = MediaType.APPLICATION_OCTET_STREAM;

    public int getStatusCode() {
        return statusCode;
    }

    public String getContentType() {
        return contentType;
    }

    public abstract Response render();

}
