package nl.hs_hague.urbangame.model;

import java.io.Serializable;
import java.util.List;

/**
 * Created by vural on 07.10.16.
 * Helpclass to return created checkpoints from MapsActivity to the calling Activity
 */
public class CheckpointHolder implements Serializable{
    // List of checkpoints, which where created by the user
    private List<Checkpoint> checkpoints;

    public List<Checkpoint> getCheckpoints() {
        return checkpoints;
    }

    public void setCheckpoints(List<Checkpoint> checkpoints) {
        this.checkpoints = checkpoints;
    }
}
