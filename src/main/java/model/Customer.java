package main.java.model;

public class Customer {
    private int customerId;
    private String firstname;
    private String lastname;
    private String email;
    private String city;
    private String address1;
    
    public Customer() {}
    
    public Customer(int customerId, String firstname, String lastname, String email, String city, String address1) {
        this.customerId = customerId;
        this.firstname = firstname;
        this.lastname = lastname;
        this.email = email;
        this.city = city;
        this.address1 = address1;
    }
    
    public int getCustomerId() {
        return customerId;
    }
    
    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }
    
    public String getFirstname() {
        return firstname;
    }
    
    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }
    
    public String getLastname() {
        return lastname;
    }
    
    public void setLastname(String lastname) {
        this.lastname = lastname;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getCity() {
        return city;
    }
    
    public void setCity(String city) {
        this.city = city;
    }
    
    public String getAddress1() {
        return address1;
    }
    
    public void setAddress1(String address1) {
        this.address1 = address1;
    }
    
    @Override
    public String toString() {
        return String.format("Customer{id=%d, name='%s %s', email='%s', city='%s'}", 
            customerId, firstname, lastname, email, city);
    }
}