package com.example.mobilestore.users;

public class Users {
    public String UserName;
    public String Password;
    public String FirstName;
    public String LastName;
    public String email;
    public String PhoneNumber;
    public Users(String userName, String password, String firstName, String lastName, String _email, String phoneNumber){
        UserName = userName;
        Password = password;
        FirstName = firstName;
        LastName=lastName;
        email = _email;
        PhoneNumber = phoneNumber;
    }
}

