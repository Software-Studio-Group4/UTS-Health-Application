package uts.group4.UTShealth.Model;

public class TimeOffModel {
    private String Date;
    private String Day;
    private String Month;
    private String Year;

    public TimeOffModel(){

    }

    public TimeOffModel(String date, String day, String month, String year) {
        Date = date;
        Day = day;
        Month = month;
        Year = year;
    }

    public String getDate() {
        return Date;
    }

    public void setDate(String date) {
        Date = date;
    }

    public String getDay() {
        return Day;
    }

    public void setDay(String day) {
        Day = day;
    }

    public String getMonth() {
        return Month;
    }

    public void setMonth(String month) {
        Month = month;
    }

    public String getYear() {
        return Year;
    }

    public void setYear(String year) {
        Year = year;
    }
}

