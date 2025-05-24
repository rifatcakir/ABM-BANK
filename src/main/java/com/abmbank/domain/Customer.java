package com.abmbank.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Customer {
    private String customerId;
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private LocalDate dateOfBirth;
    private String address;
    private LocalDate registrationDate;
    private List<Loan> loans = new ArrayList<>();

    public Customer(String firstName, String lastName, String email, String phoneNumber) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.registrationDate = LocalDate.now();
    }

    public String getFullName() {
        return firstName + " " + lastName;
    }
} 