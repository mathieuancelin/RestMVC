package app.services;

import javax.ejb.Stateless;

@Stateless
public class HelloService {

    public String hello(String name) {
        return "Hello " + name + "!";
    }

}
