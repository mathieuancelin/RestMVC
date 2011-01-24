package app.model;

import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author mathieu
 */
@XmlRootElement
public class Person {

    private Long id;
    private String name;
    private String surname;
    private String address;

    public Person() {}

    public Person(String name, String surname, String address) {
        this.name = name;
        this.surname = surname;
        this.address = address;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "Person { " + "id=" + id + ", name=" + name + ", surname=" + surname + ", address=" + address + " }";
    }

}
