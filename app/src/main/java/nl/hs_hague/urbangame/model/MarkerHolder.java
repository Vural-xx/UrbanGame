package nl.hs_hague.urbangame.model;

import java.io.Serializable;
import java.util.List;

/**
 * Created by vural on 07.10.16.
 */

public class MarkerHolder implements Serializable{
    private List<CustomMarker> markers;

    public List<CustomMarker> getMarkers() {
        return markers;
    }

    public void setMarkers(List<CustomMarker> markers) {
        this.markers = markers;
    }
}
