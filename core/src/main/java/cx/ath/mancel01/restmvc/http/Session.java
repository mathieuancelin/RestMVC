package cx.ath.mancel01.restmvc.http;

import cx.ath.mancel01.restmvc.FrameworkFilter;
import cx.ath.mancel01.restmvc.utils.SecurityUtils;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.enterprise.context.RequestScoped;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RequestScoped
public class Session {

    public static final String SESSION_ID = "webfwk-session-id";
    public static final String SESSION = "webfwk-session";
    public static final String REMEMBERME = "webfwk-rememberme";
    public static final String USERNAME = "webfwk-username";

    private static final Pattern sessionParser
            = Pattern.compile("\u0000([^:]*):([^\u0000]*)\u0000");
    public static ThreadLocal<Session> current 
            = new ThreadLocal<Session>();
    private String sessionId;
    private Map<String, String> data = new HashMap<String, String>();

    public static Session restore() {
        HttpServletRequest req = FrameworkFilter.currentRequest.get();
        HttpServletResponse res = FrameworkFilter.currentResponse.get();
        Session session = new Session();
        try {
            Cookie[] cookies = req.getCookies();
            Cookie cookie = null;
            for (Cookie cook : cookies) {
                if (cook.getName().equals(SESSION)) {
                    cookie = cook;
                }
            }
            if (cookie != null) {
                String value = cookie.getValue();
                String sign = value.substring(0, value.indexOf("-"));
                String data = value.substring(value.indexOf("-") + 1);
                if (sign.equals(SecurityUtils.sign(data))) {
                    String sessionData = URLDecoder.decode(data, "utf-8");
                    Matcher matcher = sessionParser.matcher(sessionData);
                    while (matcher.find()) {
                        session.put(matcher.group(1), matcher.group(2));
                    }
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Corrupted HTTP session from " + req.getRemoteAddr(), e);
        }
        boolean found = false;
        for (Cookie cook : req.getCookies()) {
            if (cook.getName().equals(SESSION_ID)) {
                session.sessionId = cook.getValue();
                found = true;
            }
        }
        if (!found) {
            session.sessionId = UUID.randomUUID().toString();
            Cookie cookie = new Cookie(SESSION_ID, session.sessionId);
            res.addCookie(cookie);
        }
        return session;
    }

    public void save() {
        try {
            StringBuilder session = new StringBuilder();
            for (String key : data.keySet()) {
                session.append("\u0000");
                session.append(key);
                session.append(":");
                session.append(data.get(key));
                session.append("\u0000");
            }
            String sessionData = URLEncoder.encode(session.toString(), "utf-8");
            String sign = SecurityUtils.sign(sessionData);
            Cookie cookie = new Cookie(SESSION, sign + "-" + sessionData);
            FrameworkFilter.currentResponse.get().addCookie(cookie);
        } catch (Exception e) {
            throw new RuntimeException("Session serializationProblem", e);
        }
    }

    public Map<String, String> getData() {
        return data;
    }

    public String getSessionId() {
        return sessionId;
    }

    public String put(String key, String value) {
        return data.put(key, value);
    }

    public void remove(String key) {
        data.remove(key);
    }

    public String get(String key) {
        return data.get(key);
    }

    public void clear() {
        data.clear();
    }

    public boolean contains(String key) {
        return data.containsKey(key);
    }
}
