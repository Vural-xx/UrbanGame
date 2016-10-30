package nl.hs_hague.urbangame.model;

import java.io.Serializable;

/**
 * Created by vural on 29.09.16.
 */
public class User implements Serializable {

    // Uuid from firebase user
    private String uuid;
    // Email from firebase user
    private String email;
    // Username from firebase user
    private String username;

    public User(){

    }

    public User(String uuid){
        this.uuid = uuid;
    }
    public User(String uuid, String email){
        this.uuid = uuid;
        this.email = email;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
