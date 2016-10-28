package nl.hs_hague.urbangame.model;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.Hours;
import org.joda.time.Minutes;

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
    private int distance;
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

    public int getDistance() {
        return distance;
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

    public void setDistance(int distance){this.distance = distance;}

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
        for(int i = 0; i < checkpoints.size(); i++){
            if (checkpoints.get(i).getFoundBy() == null || checkpoints.get(i).getFoundBy().size() == 0){
                checkpoints.get(i).setFoundBy(new ArrayList<String>());
                return checkpoints.get(i);
            }
            for(int j = 0 ; j < checkpoints.get(i).getFoundBy().size(); j++){
                if(!checkpoints.get(i).getFoundBy().get(j).equals(uuid)){
                    currentCheckpoint = checkpoints.get(i);
                    return currentCheckpoint;
                }
            }
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
        tempCheckpoints.add(getCurrentCheckpoint(uuid));
        return tempCheckpoints;
    }

    public boolean containsChechkpoint(Checkpoint checkpoint){
        for(int i = 0; i < checkpoints.size(); i++){
            if(checkpoints.get(i).getName().equals(checkpoint.getName())){
                return true;
            }
        }
        return false;
    }

    public boolean roomCompleted(String uuid){
        return foundCheckPoints(uuid).size() == checkpoints.size();
    }


    public CustomTimer getLeftTime(Date currentTime){
        DateTime currentDateTime = new DateTime(currentTime.getTime());
        DateTime endDateTime = new DateTime(endDate.getTime());
        return new CustomTimer(Days.daysBetween(currentDateTime,endDateTime).getDays(), Hours.hoursBetween(currentDateTime,endDateTime).getHours(), Minutes.minutesBetween(currentDateTime,endDateTime).getMinutes());
    }

    public boolean timeLeft(Date currentTime){
        return endDate.after(currentTime);
    }
}
