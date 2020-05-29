package uts.group4.UTShealth.Model;

import com.google.firebase.firestore.PropertyName;

public class Patient {
    @PropertyName("Email") private String email;
    @PropertyName("First Name") private String firstName;
    @PropertyName("Last Name") private String lastName;
    @PropertyName("Phone Number") private String phoneNumber;
    @PropertyName("Post Code") private String postCode;
    @PropertyName("State") private String state;
    @PropertyName("Street Address") private String streetAddress;
    @PropertyName("City") private String city;
    @PropertyName("Medicare Number") private String medicareNumber;

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
    @PropertyName("City")
    public String getCity() {
        return city;
    }
    @PropertyName("City")
    public void setCity(String city) {
        this.city = city;
    }
    @PropertyName("Medicare Number")
    public String getMedicareNumber() {
        return medicareNumber;
    }
    @PropertyName("Medicare Number")
    public void setMedicareNumber(String medicareNumber) {
        this.medicareNumber = medicareNumber;
    }
}
