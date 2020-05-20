package uts.group4.UTShealth.Model;

public class ShiftModel {
    private String StartTime;
    private String EndTime;
    private String Day;

    ShiftModel(){

    }

    public ShiftModel(String startTime, String endTime, String day) {
        StartTime = startTime;
        EndTime = endTime;
        Day = day;
    }

    public String getStartTime() {
        return StartTime;
    }

    public void setStartTime(String startTime) {
        StartTime = startTime;
    }

    public String getEndTime() {
        return EndTime;
    }

    public void setEndTime(String endTime) {
        EndTime = endTime;
    }

    public String getDay() {
        return Day;
    }

    public void setDay(String day) {
        Day = day;
    }
}
