package com.abmbank.domain;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Data
public class LoanData {
    private List<Customer> customers = new ArrayList<>();
    private List<Loan> loans = new ArrayList<>();

    public Optional<Customer> findCustomerById(String customerId) {
        return customers.stream()
                .filter(c -> c.getCustomerId().equals(customerId))
                .findFirst();
    }

    public Optional<Loan> findLoanByNumber(String loanNumber) {
        return loans.stream()
                .filter(l -> l.getLoanNumber().equals(loanNumber))
                .findFirst();
    }

    public List<Loan> findLoansByCustomerId(String customerId) {
        return loans.stream()
                .filter(l -> l.getCustomer().getCustomerId().equals(customerId))
                .toList();
    }

    public List<Loan> findLoansByStatus(LoanStatus status) {
        return loans.stream()
                .filter(l -> l.getLoanStatus() == status)
                .toList();
    }

    public List<Loan> findLoansByType(LoanType type) {
        return loans.stream()
                .filter(l -> l.getLoanType() == type)
                .toList();
    }
} 