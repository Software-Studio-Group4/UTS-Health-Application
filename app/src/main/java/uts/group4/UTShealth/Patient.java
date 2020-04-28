package uts.group4.UTShealth;

/***************************************************************************************************
 * A class which stores all the data of a patient
 **************************************************************************************************/

public class Patient {

    //Email is PK of Patient. No two patient accounts should be under the same Email. Throw an
    //exception if the patient attempts to register with an email which already exists.
    private String email;
    private String password; /*important note regarding password : for now we don't have to make any
    security features because it's only sprint 1, but in future, the password should be stored using a
    hashing algorithm, and then dehashed and compared when querying the login_bg form.*/

    private String firstName;
    private String lastName;
    private int age;

    private String medicareNumber;
    private String phoneNumber;


    private String streetAddress;
    private String suburb;
    private String city;
    private String state;
    private String postCode;

    //patient will reference the prescriptions that they've been given, doctors they've seen,
    //notes, and so on. patietn should also store a list of pre-existing health issues but, that will
    //not be used until later, so for now the patient class should be fairly bare bones.

    /*****************************************CONSTRUCTOR*****************************************/
    public Patient(){

    }

    public Patient(String email, String password, String firstName, String lastName, int age,
                   String medicareNumber, String phoneNumber, String streetAddress, String city,
                   String state, String postCode){
        this.email = email;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.age = age;
        this.medicareNumber = medicareNumber;
        this.phoneNumber = phoneNumber;
        this.streetAddress = streetAddress;
        this.city = city;
        this.state = state;
        this.postCode = postCode;
    }

    /******************GETTERS AND SETTERS********************************************************/
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getMedicareNumber() {
        return medicareNumber;
    }

    public void setMedicareNumber(String medicareNumber) {
        this.medicareNumber = medicareNumber;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getStreetAddress() {
        return streetAddress;
    }

    public void setStreetAddress(String streetAddress) {
        this.streetAddress = streetAddress;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getPostCode() {
        return postCode;
    }

    public void setPostCode(String postCode) {
        this.postCode = postCode;
    }
}
