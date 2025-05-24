package com.abmbank.domain;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class Loan {

    private String loanNumber;
    private LocalDate applicationDate;
    private LocalDate approvalDate;
    private LocalDate dueDate;
    private Customer customer;
    private BigDecimal totalAmount;
    private BigDecimal paidAmount;
    private BigDecimal interestRate;
    private Integer termInMonths;
    private LoanStatus loanStatus;
    private LoanType loanType;
    private String description;
    private LocalDate lastPaymentDate;

    public Loan(String loanNumber, LocalDate applicationDate, Customer customer, 
               LoanStatus loanStatus, BigDecimal totalAmount, LoanType loanType) {
        this.loanNumber = loanNumber;
        this.applicationDate = applicationDate;
        this.customer = customer;
        this.loanStatus = loanStatus;
        this.totalAmount = totalAmount;
        this.loanType = loanType;
        this.paidAmount = BigDecimal.ZERO;
        this.interestRate = calculateInterestRate(loanType);
        this.termInMonths = calculateTermInMonths(loanType, totalAmount);
        this.dueDate = applicationDate.plusMonths(termInMonths);
    }

    private BigDecimal calculateInterestRate(LoanType loanType) {
        return switch (loanType) {
            case HOME -> new BigDecimal("0.045"); // 4.5%
            case AUTO -> new BigDecimal("0.055");  // 5.5%
            case PERSONAL -> new BigDecimal("0.075"); // 7.5%
            case BUSINESS -> new BigDecimal("0.065"); // 6.5%
            case EDUCATION -> new BigDecimal("0.035"); // 3.5%
            default -> new BigDecimal("0.085"); // 8.5%
        };
    }

    private Integer calculateTermInMonths(LoanType loanType, BigDecimal amount) {
        return switch (loanType) {
            case HOME -> 360; // 30 years
            case AUTO -> 60;   // 5 years
            case PERSONAL -> 36; // 3 years
            case BUSINESS -> 120; // 10 years
            case EDUCATION -> 120; // 10 years
            default -> 24; // 2 years
        };
    }

    public BigDecimal getRemainingAmount() {
        return totalAmount.subtract(paidAmount);
    }

    public boolean isFullyPaid() {
        return paidAmount.compareTo(totalAmount) >= 0;
    }

    public BigDecimal getMonthlyPayment() {
        if (termInMonths == 0) return BigDecimal.ZERO;
        return totalAmount.divide(new BigDecimal(termInMonths), 2, BigDecimal.ROUND_HALF_UP);
    }
} 