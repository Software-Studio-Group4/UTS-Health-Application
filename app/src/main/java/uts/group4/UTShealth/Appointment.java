package uts.group4.UTShealth;

import com.google.firebase.firestore.Exclude;

public class Appointment {
    private String appointmentID;
    private String Date;
    private String Time;

    public Appointment() {
        //public no-arg constructor needed
    }

    @Exclude
    public String getAppointmentID() {
        return appointmentID;
    }

    public void setAppointmentID(String appointmentID) {
        this.appointmentID = appointmentID;
    }

    public Appointment(String Date, String Time) {
        this.Date = Date;
        this.Time = Time;
    }

    public String getDate() {
        return Date;
    }

    public String getTime() {
        return Time;
    }
}

