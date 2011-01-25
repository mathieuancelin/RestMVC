/*
 *  Copyright 2010 Mathieu ANCELIN.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *  under the License.
 */

package app.controller;

import app.model.Person;
import app.services.HelloService;
import cx.ath.mancel01.restmvc.cache.CacheService;
import cx.ath.mancel01.restmvc.http.Session;
import cx.ath.mancel01.restmvc.view.Render;
import cx.ath.mancel01.restmvc.view.View;
import java.io.File;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Schedule;
import javax.ejb.Startup;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 *
 * @author Mathieu ANCELIN
 */
@Startup
@Stateless
@Path("/")
public class Application {

    @EJB CacheService cache;
    @Inject HelloService service;
    @Inject HttpServletRequest request;
    @Inject HttpServletResponse response;
    @Inject Logger appLogger;
    @Inject Session session;

    @PostConstruct
    public void init() {
        appLogger.info("load fixtures here !!!");
    }

    @Schedule(second="*/15", minute="*", hour="*")
    public void job() {
         appLogger.info("do something here !!!");
    }

    @GET @Path("application/hello/{name}")
    public Response hello(@PathParam("name") String name) {
        return new View()
            .param("name", service.hello(name))
            .render();
    }

    @GET
    public Response index() {
        return new View("index.html")
            .param("name", service.hello("guest"))
            .render();
    }

    @GET @Path("application/all")
    public Response all() {
        return new View()
            .param("persons", Person.jpa.all())
            .render();
    }

    @PUT @Path("application/put")
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response putValue(Person value) {
        try {
            Thread.sleep(2000);
            value.save();
            return Render.text("ok");
        } catch (Exception e) {
            return Render.text("ko");
        }
    }

    @POST @Path("application/file")
    public Response postFile(File file) {
        System.out.println(file);
        System.out.println(file.length());
        return Render.redirect("#");
    }
}
