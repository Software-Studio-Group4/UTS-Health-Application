package uts.group4.UTShealth.Model;

import com.google.firebase.Timestamp;

public class AppointmentModel {
    private String id;
    private String Date;
    private String Time;
    private String doctorID;
    private String patientID;
    private String ChatCode;
    private String DoctorFullName;
    private String PatientFullName;
    private String WeekDay;
    private boolean CompletionStatus;
    private boolean UrgentStatus;
    private Timestamp TimeStamp;

    public AppointmentModel(){

    }


    public AppointmentModel(String date, String time){
        this.Date = date;
        this.Time = time;
    }

    public AppointmentModel(String id, String date, String time, String doctorID, String patientID, String chatCode, String doctorFullName, String patientFullName, String weekDay, boolean completionStatus, boolean urgentStatus, Timestamp TimeStamp) {
        this.id = id;
        Date = date;
        Time = time;
        this.doctorID = doctorID;
        this.patientID = patientID;
        ChatCode = chatCode;
        DoctorFullName = doctorFullName;
        PatientFullName = patientFullName;
        WeekDay = weekDay;
        CompletionStatus = completionStatus;
        UrgentStatus = urgentStatus;
        this.TimeStamp = TimeStamp;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDate() {
        return Date;
    }

    public void setDate(String date) {
        Date = date;
    }

    public String getTime() {
        return Time;
    }

    public void setTime(String time) {
        Time = time;
    }

    public String getDoctorID() {
        return doctorID;
    }

    public void setDoctorID(String doctorID) {
        this.doctorID = doctorID;
    }

    public String getPatientID() {
        return patientID;
    }

    public void setPatientID(String patientID) {
        this.patientID = patientID;
    }

    public String getChatCode() {
        return ChatCode;
    }

    public void setChatCode(String chatCode) {
        ChatCode = chatCode;
    }

    public String getDoctorFullName() {
        return DoctorFullName;
    }

    public void setDoctorFullName(String doctorFullName) {
        DoctorFullName = doctorFullName;
    }

    public String getPatientFullName() {
        return PatientFullName;
    }

    public void setPatientFullName(String patientFullName) {
        PatientFullName = patientFullName;
    }

    public String getWeekDay() {
        return WeekDay;
    }

    public void setWeekDay(String weekDay) {
        WeekDay = weekDay;
    }

    public boolean isCompletionStatus() {
        return CompletionStatus;
    }

    public void setCompletionStatus(boolean completionStatus) {
        CompletionStatus = completionStatus;
    }

    public boolean isUrgentStatus(){return UrgentStatus;}

    public void setUrgentStatus(boolean urgentStatus) {
        UrgentStatus = urgentStatus;
    }

    public Timestamp getTimeStamp() {
        return TimeStamp;
    }

    public void setTimeStamp(Timestamp TimeStamp) {
        this.TimeStamp = TimeStamp;
    }
}
