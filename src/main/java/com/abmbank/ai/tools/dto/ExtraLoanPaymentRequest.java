package com.abmbank.ai.tools.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record ExtraLoanPaymentRequest(
        @NotBlank(message = "Loan number is required")
        @Pattern(regexp = "^[0-9]+$", message = "Loan number must contain only digits")
        String loanNumber,
        
        @NotBlank(message = "First name is required")
        String firstName,
        
        @NotBlank(message = "Last name is required")
        String lastName,
        
        @NotBlank(message = "Amount is required")
        @Pattern(regexp = "^[0-9]+(\\.[0-9]{1,2})?$", message = "Amount must be a valid number with up to 2 decimal places")
        String amount) {
} 