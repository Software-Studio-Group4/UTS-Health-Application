package uts.group4.UTShealth.Model;


import com.google.firebase.firestore.PropertyName;

public class Doctor {
    @PropertyName("Email") private String email;
    @PropertyName("First Name") private String firstName;
    @PropertyName("Last Name") private String lastName;
    @PropertyName("Phone Number") private String phoneNumber;
    @PropertyName("Post Code") private String postCode;
    @PropertyName("State") private String state;
    @PropertyName("Street Address") private String streetAddress;
    @PropertyName("Suburb") private String suburb;
    @PropertyName("Urgent Doctor") private boolean urgentDoctor;

    public Doctor(String email, String firstName, String lastName, String phoneNumber, String postCode, String state, String streetAddress, String suburb, boolean urgentDoctor) {
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phoneNumber = phoneNumber;
        this.postCode = postCode;
        this.state = state;
        this.streetAddress = streetAddress;
        this.suburb = suburb;
        this.urgentDoctor = urgentDoctor;
    }
    public Doctor() {}

    @PropertyName("Urgent Doctor")
    public boolean isUrgentDoctor() { return urgentDoctor; }

    @PropertyName("Urgent Doctor")
    public void setUrgentDoctor(boolean urgentDoctor) { this.urgentDoctor = urgentDoctor; }

    @PropertyName("Email")
    public String getEmail() {
        return email;
    }

    @PropertyName("Email")
    public void setEmail(String email) {
        this.email = email;
    }

    @PropertyName("First Name")
    public String getFirstName() {
        return firstName;
    }

    @PropertyName("First Name")
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    @PropertyName("Last Name")
    public String getLastName() {
        return lastName;
    }

    @PropertyName("Last Name")
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    @PropertyName("Phone Number")
    public String getPhoneNumber() {
        return phoneNumber;
    }

    @PropertyName("Phone Number")
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    @PropertyName("Post Code")
    public String getPostCode() {
        return postCode;
    }

    @PropertyName("Post Code")
    public void setPostCode(String postCode) {
        this.postCode = postCode;
    }

    @PropertyName("State")
    public String getState() {
        return state;
    }

    @PropertyName("State")
    public void setState(String state) {
        this.state = state;
    }

    @PropertyName("Street Address")
    public String getStreetAddress() {
        return streetAddress;
    }

    @PropertyName("Street Address")
    public void setStreetAddress(String streetAddress) {
        this.streetAddress = streetAddress;
    }

    @PropertyName("Suburb")
    public String getSuburb() {
        return suburb;
    }

    @PropertyName("Suburb")
    public void setSuburb(String suburb) {
        this.suburb = suburb;
    }
}

