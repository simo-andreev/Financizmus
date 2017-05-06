package bg.o.sim.finansizmus.model;

import java.io.Serializable;

public class User implements Serializable {

    private String email;
    private String name;
    private long id;

    public User(String email, String name, long id) {
        //TODO - VALIDATION
        this.email = email;
        this.name = name;
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public String getName() {
        return name;
    }

    public long getId() {
        return id;
    }



}
