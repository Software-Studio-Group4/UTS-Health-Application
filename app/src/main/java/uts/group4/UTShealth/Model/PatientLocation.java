package uts.group4.UTShealth.Model;

import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

public class PatientLocation{
    GeoPoint geoPoint;
    private @ServerTimestamp Date timestamp;
    private Patient patient;

    public PatientLocation(GeoPoint geoPoint, Date timestamp, Doctor doctor) {
        this.geoPoint = geoPoint;
        this.timestamp = timestamp;
        this.patient = patient;
    }
    public PatientLocation() {}

    public GeoPoint getGeoPoint() {
        return geoPoint;
    }

    public void setGeoPoint(GeoPoint geoPoint) {
        this.geoPoint = geoPoint;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public Patient getPatient() {
        return patient;
    }

    public void setPatient(Patient patient) {
        this.patient = patient;
    }
}