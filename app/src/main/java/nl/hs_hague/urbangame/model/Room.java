package nl.hs_hague.urbangame.model;

import java.io.Serializable;

/**
 * Created by vural on 26.09.16.
 */

public class Room implements Serializable {
    private String name;

    public Room(String name){
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
