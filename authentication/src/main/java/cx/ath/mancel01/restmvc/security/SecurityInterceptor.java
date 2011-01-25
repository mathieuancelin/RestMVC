package cx.ath.mancel01.restmvc.security;

import cx.ath.mancel01.restmvc.utils.SecurityUtils;
import cx.ath.mancel01.restmvc.FrameworkFilter;
import cx.ath.mancel01.restmvc.http.Session;
import cx.ath.mancel01.restmvc.view.Render;
import javax.interceptor.AroundInvoke;
import javax.interceptor.InvocationContext;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

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
