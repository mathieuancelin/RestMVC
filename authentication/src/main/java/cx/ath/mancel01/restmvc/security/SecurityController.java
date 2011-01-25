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

package cx.ath.mancel01.restmvc.security;

import cx.ath.mancel01.restmvc.utils.SecurityUtils;
import cx.ath.mancel01.restmvc.FrameworkFilter;
import cx.ath.mancel01.restmvc.http.Session;
import cx.ath.mancel01.restmvc.view.Render;
import cx.ath.mancel01.restmvc.view.View;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

/**
 *
 * @author Mathieu ANCELIN
 */
@Path("security")
@Stateless
public class SecurityController {

    @Inject
    private HttpServletResponse response;

    @Inject
    private LoginModule loginModule;

    @Path("loginpage")
    @GET
    public Response loginPage() {
        String callback =  Session.current.get().get("callbackUrl");
        if (callback == null) {
            callback = loginModule.defaultCallbackURL();
        }
        Session.current.get().remove("callbackUrl");
        return new View().param("callbackUrl", callback).render();
    }

    @Path("login")
    @POST
    public Response login(
            @FormParam("user") String user,
            @FormParam("password") String password,
            @FormParam("url") String url, 
            @FormParam("rememberme") String rememberme) {
        
        if (!loginModule.authenticate(user, password)) {
            if (!"/".equals(FrameworkFilter.getContextRoot())) {
                return Render.redirect(FrameworkFilter.getContextRoot()
                        + loginModule.authenticationFailURL());
            }
            return Render.redirect(loginModule.authenticationFailURL());
        }
        Cookie cookie = new Cookie(Session.USERNAME, user);
        response.addCookie(cookie);
        if(rememberme.equals("on")) {
            cookie = new Cookie(Session.REMEMBERME, user + "-" + SecurityUtils.sign(user));
            cookie.setMaxAge(2592000); // 30 days
            response.addCookie(cookie);
        }
        return Render.redirect(url);
    }

    @Path("logout")
    @GET
    public Response logout() {
        Cookie username = new Cookie(Session.USERNAME, "");
        Cookie rememberme = new Cookie(Session.REMEMBERME, "");
        Cookie sessionId = new Cookie(Session.SESSION_ID, "");
        username.setDomain(null);
        username.setPath("/");
        username.setMaxAge(0);
        response.addCookie(username);
        rememberme.setDomain(null);
        rememberme.setPath("/");
        rememberme.setMaxAge(0);
        response.addCookie(rememberme);
        sessionId.setDomain(null);
        sessionId.setPath("/");
        sessionId.setMaxAge(0);
        response.addCookie(sessionId);
        Session.current.get().clear();
        if (!"/".equals(FrameworkFilter.getContextRoot())) {
            return Render.redirect(FrameworkFilter.getContextRoot() + loginModule.logoutURL());
        }
        return Render.redirect(loginModule.logoutURL());
    }
}
