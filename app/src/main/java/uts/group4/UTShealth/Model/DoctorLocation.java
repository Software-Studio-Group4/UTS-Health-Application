package uts.group4.UTShealth.Model;

import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

public class DoctorLocation {
    GeoPoint geoPoint;
    private @ServerTimestamp Date timestamp;
    private Doctor doctor;

    public DoctorLocation(GeoPoint geoPoint, Date timestamp, Doctor doctor) {
        this.geoPoint = geoPoint;
        this.timestamp = timestamp;
        this.doctor = doctor;
    }
    public DoctorLocation() {}

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

    public Doctor getDoctor() {
        return doctor;
    }

    public void setDoctor(Doctor doctor) {
        this.doctor = doctor;
    }
}
