package app.controller;

import app.model.Person;
import app.services.HelloService;
import cx.ath.mancel01.restmvc.view.Render;
import cx.ath.mancel01.restmvc.view.View;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Stateless
@Path("application")
public class Application {

    @EJB HelloService service;

    @GET
    @Path("hello/{name}")
    public Response hello(@PathParam("name") String name) {
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
    @Path("all")
    public Response all() {
        return new View()
                .param("persons", Person.jpa.all())
                .render();
    }

    @Path("put")
    @PUT
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response putValue(Person value) {
        value.save();
        return Render.text("ok");
    }
}
