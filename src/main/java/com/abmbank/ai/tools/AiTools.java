package com.abmbank.ai.tools;

import com.abmbank.ai.tools.dto.CancelLoanRequest;
import com.abmbank.ai.tools.dto.ExtraLoanPaymentRequest;
import com.abmbank.ai.tools.dto.LoanDetailsRequest;
import com.abmbank.dto.LoanDetails;
import com.abmbank.service.LoanService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.core.NestedExceptionUtils;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class AiTools {
    private static final Logger logger = LoggerFactory.getLogger(AiTools.class);
    private final LoanService loanService;

    @Tool(description = "Retrieve loan details for a given user. Use this tool whenever a user asks for loan information.")
    public LoanDetails getLoanDetails(
            @ToolParam(description = "Includes first name, last name, and loan number.")
            LoanDetailsRequest request) {
        try {
            return loanService.getLoanDetails(
                    request.loanNumber(), request.firstName(), request.lastName());
        } catch (Exception e) {
            logger.warn("Failed to retrieve loan details: {}",
                    NestedExceptionUtils.getMostSpecificCause(e).getMessage());

            return new LoanDetails(
                    request.loanNumber(),
                    request.firstName(),
                    request.lastName(),
                    null, null, null, null, "UNKNOWN"
            );
        }
    }

    @Tool(description = "Make an extra payment for a given loan. Use this tool whenever a user wants to make an extra payment on their loan.")
    public String extraLoanPayment(
            @ToolParam(description = "loan number User name lastname and amount will be paid extra") 
            ExtraLoanPaymentRequest request) {
        loanService.extraLoanPayment(
                request.loanNumber(), request.firstName(), request.lastName(),
                BigDecimal.valueOf(Long.parseLong(request.amount())));
        return "DONE";
    }

    @Tool(description = "Cancel loan application for given user")
    public String cancelLoanApplication(
            @ToolParam(description = "loanNumber,firstName,lastName") 
            CancelLoanRequest request) {
        loanService.cancelLoan(
                request.loanNumber(), request.firstName(), request.lastName());
        return "DONE";
    }
} 