package com.abmbank.dto;

import com.abmbank.domain.LoanStatus;

import java.math.BigDecimal;
import java.time.LocalDate;

public record LoanDetails(String loanNumber,
                         String firstName,
                         String lastName,
                         LocalDate date,
                         LoanStatus loanStatus,
                         BigDecimal totalAmount,
                         BigDecimal paidAmount,
                         String loanType) {
} 