package nl.hs_hague.urbangame.model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * Created by vural on 26.09.16.
 */

public class Room implements Serializable {
    private String name;
    private List<User> members;
    private Date startDate;
    private Date endDate;
    private List<Checkpoint> checkpoints;

    public Room(String name){
        this.name = name;
    }

    public Room(String name, Date startDate, Date endDate){
        this.name = name;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<User> getMembers() {
        return members;
    }

    public void setMembers(List<User> members) {
        this.members = members;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public List<Checkpoint> getCheckpoints() {
        return checkpoints;
    }

    public void setCheckpoints(List<Checkpoint> checkpoints) {
        this.checkpoints = checkpoints;
    }
}
