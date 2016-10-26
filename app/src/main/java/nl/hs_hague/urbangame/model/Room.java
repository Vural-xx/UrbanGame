package nl.hs_hague.urbangame.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by vural on 26.09.16.
 */

public class Room implements Serializable {
    private String name;
    private String description;
    private String ownerId;
    private List<User> members;
    private Date startDate;
    private Date endDate;
    private List<Checkpoint> checkpoints;

    public Room(){

    }

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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
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

    public String gethints()
    {
        String hints="";

        for(int i=0;i<checkpoints.size();i++)
        {
            Checkpoint mycheck=checkpoints.get(i);
            hints=hints+mycheck.getHint()+"\n";
        }
        return hints;
    }

    public Checkpoint getCurrentCheckpoint(String uuid){
        Checkpoint currentCheckpoint = null;
        Boolean found = false;

        for(int i = 0; i < checkpoints.size(); i++){
            if (checkpoints.get(i).getFoundBy() == null){
                checkpoints.get(i).setFoundBy(new ArrayList<String>());
            }
            for(int j = 0 ; j < checkpoints.get(i).getFoundBy().size(); j++){
                if(checkpoints.get(i).getFoundBy().get(j).equals(uuid) && !found){
                    currentCheckpoint = checkpoints.get(i);
                    found = true;
                }
            }
        }
        if(currentCheckpoint == null){
            currentCheckpoint = checkpoints.get(0);
        }
        return currentCheckpoint;
    }

    public void foundCheckpoint(String uuid){
        getCurrentCheckpoint(uuid).getFoundBy().add(uuid);
    }

    public List<Checkpoint> foundCheckPoints(String uuid){
        List<Checkpoint> tempCheckpoints = new ArrayList<Checkpoint>();
        for(int i = 0; i < checkpoints.size(); i++){
            for(int j = 0 ; j < checkpoints.get(i).getFoundBy().size(); j++){
                if(checkpoints.get(i).getFoundBy().get(j).equals(uuid)){
                    tempCheckpoints.add(checkpoints.get(i));
                }
            }
        }
        return tempCheckpoints;
    }

    public List<Checkpoint> getCheckpointsForUser(String uuid){
        List<Checkpoint> tempCheckpoints = new ArrayList<Checkpoint>();
        boolean found = false;
        for(int i = 0; i < checkpoints.size(); i++){
            for(int j = 0 ; j < checkpoints.get(i).getFoundBy().size(); j++){
                if(checkpoints.get(i).getFoundBy().get(j).equals(uuid)){
                    found = true;
                }
            }
            if(!found){
                tempCheckpoints.add(checkpoints.get(i));
            }
            found = false;
        }
        return tempCheckpoints;
    }
}
