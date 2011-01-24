package cx.ath.mancel01.restmvc.security;

import cx.ath.mancel01.restmvc.FrameworkFilter;

public class LoginModuleImpl implements LoginModule {

    @Override
    public boolean authenticate(String username, String password) {
        return true;
    }

    @Override
    public String defaultCallbackURL() {
        if (!"/".equals(FrameworkFilter.getContextRoot())) {
            return FrameworkFilter.getContextRoot()
                    + "/";
        }
        return "/";
    }

    @Override
    public String authenticationFailURL() {
        if (!"/".equals(FrameworkFilter.getContextRoot())) {
            return FrameworkFilter.getContextRoot()
                    + "/security/loginpage";
        }
        return "/security/loginpage";
    }

    @Override
    public String logoutURL() {
        if (!"/".equals(FrameworkFilter.getContextRoot())) {
            return FrameworkFilter.getContextRoot()
                    + "/security/loginpage";
        }
        return "/security/loginpage";
    }

}
