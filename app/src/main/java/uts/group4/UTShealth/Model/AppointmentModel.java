package uts.group4.UTShealth.Model;

public class AppointmentModel {
    private String id;
    private String Date;
    private String Time;
    private String doctorID;
    private String patientID;
    private String ChatCode;
    private String DoctorFullName;

    public AppointmentModel(){

    }

    public AppointmentModel(String id, String date, String time, String doctorID, String patientID, String chatCode, String doctorFullName) {
        this.id = id;
        Date = date;
        Time = time;
        this.doctorID = doctorID;
        this.patientID = patientID;
        ChatCode = chatCode;
        DoctorFullName = doctorFullName;
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
}
