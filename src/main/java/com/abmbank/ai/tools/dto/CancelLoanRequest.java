package com.abmbank.ai.tools.dto;

public record CancelLoanRequest(
        String loanNumber,
        String firstName,
        String lastName) {
} 