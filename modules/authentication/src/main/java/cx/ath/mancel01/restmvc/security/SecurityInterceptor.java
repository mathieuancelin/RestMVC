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
import javax.interceptor.AroundInvoke;
import javax.interceptor.InvocationContext;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

/**
 *
 * @author Mathieu ANCELIN
 */
public class SecurityInterceptor {

    @AroundInvoke
    public final Object invoke(InvocationContext mi) throws Throwable {
        HttpServletRequest req = FrameworkFilter.currentRequest.get();
        boolean logged = false;
        boolean rememberMe = false;
        Cookie rememberMeCookie = null;
        for (Cookie cook : req.getCookies()) {
            if (cook.getName().equals(Session.USERNAME)) {
                logged = true;
                break;
            }
            if (cook.getName().equals(Session.REMEMBERME)) {
                rememberMe = true;
                rememberMeCookie = cook;
            }
        }
        if (logged) {
            return mi.proceed();
        } else if (rememberMe) {
            String cookieValue = rememberMeCookie.getValue();
            String username = cookieValue.split("-")[0];
            String sign = cookieValue.split("-")[1];
            if (SecurityUtils.sign(username).equals(sign)) {
                Cookie cookie = new Cookie(Session.USERNAME, username);
                FrameworkFilter.currentResponse.get().addCookie(cookie);
                return mi.proceed();
            } else {
                Session.current.get().put("callbackUrl", req.getRequestURI());
                if (!"/".equals(FrameworkFilter.getContextRoot())) {
                    return Render.redirect(FrameworkFilter.getContextRoot()
                            + "/security/loginpage");
                }
                return Render.redirect("/security/loginpage");
            }
        } else {
            Session.current.get().put("callbackUrl", req.getRequestURI());
            if (!"/".equals(FrameworkFilter.getContextRoot())) {
                return Render.redirect(FrameworkFilter.getContextRoot()
                        + "/security/loginpage");
            }
            return Render.redirect("/security/loginpage");
        }
    }
}
