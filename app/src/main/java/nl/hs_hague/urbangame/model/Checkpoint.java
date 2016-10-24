package nl.hs_hague.urbangame.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by vural on 29.09.16.
 */

public class Checkpoint implements Serializable{
    private String name;
    private Double latitude;
    private Double longitude;
    private String hint;
    private List<String> foundBy;

    public Checkpoint(){
        foundBy = new ArrayList<String>();
    }

    public Checkpoint(String name, Double latitude, Double longitude){
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public String getHint() {
        return hint;
    }

    public void setHint(String hint) {
        this.hint = hint;
    }

    public List<String> getFoundBy() {
        return foundBy;
    }

    public void setFoundBy(List<String> foundBy) {
        this.foundBy = foundBy;
    }
}
