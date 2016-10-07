package nl.hs_hague.urbangame.model;

import java.io.Serializable;

/**
 * Created by vural on 08.10.16.
 */

public class CustomMarker implements Serializable {
    private String title;
    private Double latitude;
    private Double longitude;

    public CustomMarker(String title, Double latitude, Double longitude){
        this.title = title;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
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
}
