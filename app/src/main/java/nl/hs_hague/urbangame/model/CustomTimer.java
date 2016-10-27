package nl.hs_hague.urbangame.model;

/**
 * Created by vural on 27.10.16.
 */

public class CustomTimer {
    private int daysLeft;
    private int hoursLeft;
    private int minutesLeft;

    public CustomTimer(int daysLeft, int hoursLeft, int minutesLeft){
        this.daysLeft = daysLeft;
        this.hoursLeft = hoursLeft;
        this.minutesLeft = minutesLeft;
    }

    public int getDaysLeft() {
        return daysLeft;
    }

    public void setDaysLeft(int daysLeft) {
        this.daysLeft = daysLeft;
    }

    public int getHoursLeft() {
        return hoursLeft;
    }

    public void setHoursLeft(int hoursLeft) {
        this.hoursLeft = hoursLeft;
    }

    public int getMinutesLeft() {
        return minutesLeft;
    }

    public void setMinutesLeft(int minutesLeft) {
        this.minutesLeft = minutesLeft;
    }
}
