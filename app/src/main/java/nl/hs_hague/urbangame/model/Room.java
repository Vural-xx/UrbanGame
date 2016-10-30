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
    // Name of Gameroom
    private String name;
    // Description of the room
    private String description;
    // Ownerid of the room
    private String ownerId;
    // List of members of the room
    private List<User> members;
    // The start Date of the room
    private Date startDate;
    // The end Date of the room
    private Date endDate;
    // The distance of the first marker of the room
    private int distance;
    // The List list of checkpoint of the room
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

    /**
     * Returns the currentCheckpoint of this room for the current user
     * @param uuid: Uuid of the current user
     * @return: Current checkpoint
     */
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

    /**
     * Sets the checkpoint as found for the current user
     * @param uuid: Uuid of the current user
     */
    public void foundCheckpoint(String uuid){
        getCurrentCheckpoint(uuid).getFoundBy().add(uuid);
    }

    /**
     * Returns a list of checkpoints that the user has found already
     * @param uuid: Uuid of the current user
     * @return: Found Checkpoints
     */
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

    /**
     * Gets the checkpoints
     * @param uuid
     * @return
     */
    public List<Checkpoint> getCheckpointsForUser(String uuid){
        List<Checkpoint> tempCheckpoints = new ArrayList<Checkpoint>();
        tempCheckpoints.add(getCurrentCheckpoint(uuid));
        return tempCheckpoints;
    }

    /**
     * Checks if room has checkpoint with same name as in parameter
     * @param checkpoint: Checkpoint to look for
     * @return: true if room contains checkpoint in param
     */
    public boolean containsChechkpoint(Checkpoint checkpoint){
        if(checkpoint != null){
            for(int i = 0; i < checkpoints.size(); i++){
                if(checkpoints.get(i).getName().equals(checkpoint.getName())){
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Shows if the user has found all the checkpoints in a room
     * @param uuid: Uuid of user
     * @return: true if all checkpoints have been found by the user
     */
    public boolean roomCompleted(String uuid){
        return foundCheckPoints(uuid).size() == checkpoints.size();
    }


    /**
     * Calculates the left time of this room
     * @param currentTime: CurrentTime of the location, the user is in
     * @return: CustomTimer with left time
     */
    public CustomTimer getLeftTime(Date currentTime){
        DateTime currentDateTime = new DateTime(currentTime.getTime());
        DateTime endDateTime = new DateTime(endDate.getTime());
        return new CustomTimer(Days.daysBetween(currentDateTime,endDateTime).getDays(), Hours.hoursBetween(currentDateTime,endDateTime).getHours(), Minutes.minutesBetween(currentDateTime,endDateTime).getMinutes());
    }

    /**
     * Shows if the time of the room is over
     * @param currentTime: Current time of location
     * @return: True if time for the room is left
     */
    public boolean timeLeft(Date currentTime){
        return endDate.after(currentTime);
    }

    /**
     * Shows if user is member of the room
     * @param uuid: Uuid of user
     * @return: ture if user is member of room
     */
    public boolean isMember(String uuid){
        if(members != null){
            for(int i =0; i< members.size(); i++){
                if(members.get(i).getUuid().equals(uuid)){
                    return true;
                }
            }
        }
        return false;
    }
}
