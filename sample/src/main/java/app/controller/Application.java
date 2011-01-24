package app.controller;

import app.services.HelloService;
import cx.ath.mancel01.restmvc.view.Render;
import cx.ath.mancel01.restmvc.view.View;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;

@Stateless
@Path("application")
public class Application {

    @EJB HelloService service;

    @GET
    @Path("index/{name}")
    public Response index(@PathParam("name") String name) {
        return new View()
                .param("name", service.hello(name))
                .render();
    }

    @GET
    @Path("index")
    public Response index() {
        return new View("index.html")
                .param("name", service.hello("guest"))
                .render();
    }

    @GET
    @Path("test")
    public Response test() {
        return Render.text("Hello dude!");
    }
}
