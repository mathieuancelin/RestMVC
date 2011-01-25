package cx.ath.mancel01.restmvc.security;

public interface LoginModule {
    boolean authenticate(String username, String password);
    String defaultCallbackURL();
    String authenticationFailURL();
    String logoutURL();
}
