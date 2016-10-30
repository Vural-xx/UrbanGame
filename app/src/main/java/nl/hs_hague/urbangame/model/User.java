package nl.hs_hague.urbangame.model;

import java.io.Serializable;
import java.sql.Blob;

/**
 * Created by vural on 29.09.16.
 */

public class User implements Serializable {

    private String uuid;
    private String email;
    private String username;
    private String password;
    private Blob avatar;
    private int score;

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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Blob getAvatar() {
        return avatar;
    }

    public void setAvatar(Blob avatar) {
        this.avatar = avatar;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }


}
