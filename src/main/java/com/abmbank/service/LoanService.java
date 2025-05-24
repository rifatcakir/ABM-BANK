package com.abmbank.service;

import com.abmbank.domain.*;
import com.abmbank.dto.LoanDetails;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import com.vaadin.hilla.BrowserCallable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@BrowserCallable
@AnonymousAllowed
public class LoanService {

    private final LoanData db;

    public LoanService() {
        db = new LoanData();
        initDemoData();
    }

    private void initDemoData() {
        var customers = new ArrayList<Customer>();
        var loans = new ArrayList<Loan>();

        // Customer 1: John Smith - Home Loan (Approved)
        Customer johnSmith = new Customer();
        johnSmith.setFirstName("John");
        johnSmith.setLastName("Smith");
        Loan johnLoan = new Loan(
            "1001",
            LocalDate.now().minusDays(30),
            johnSmith,
            LoanStatus.APPROVED,
            new BigDecimal("250000.00"),
            LoanType.HOME
        );
        johnLoan.setPaidAmount(new BigDecimal("62500.00")); // 25% paid
        johnSmith.getLoans().add(johnLoan);
        customers.add(johnSmith);
        loans.add(johnLoan);

        // Customer 2: Sarah Johnson - Car Loan (Paid)
        Customer sarahJohnson = new Customer();
        sarahJohnson.setFirstName("Sarah");
        sarahJohnson.setLastName("Johnson");
        Loan sarahLoan = new Loan(
            "1002",
            LocalDate.now().minusDays(90),
            sarahJohnson,
            LoanStatus.PAID,
            new BigDecimal("35000.00"),
            LoanType.AUTO
        );
        sarahLoan.setPaidAmount(new BigDecimal("35000.00")); // Fully paid
        sarahJohnson.getLoans().add(sarahLoan);
        customers.add(sarahJohnson);
        loans.add(sarahLoan);

        // Customer 3: Michael Brown - Personal Loan (Pending)
        Customer michaelBrown = new Customer();
        michaelBrown.setFirstName("Michael");
        michaelBrown.setLastName("Brown");
        Loan michaelLoan = new Loan(
            "1003",
            LocalDate.now().minusDays(5),
            michaelBrown,
            LoanStatus.PENDING,
            new BigDecimal("15000.00"),
            LoanType.PERSONAL
        );
        michaelLoan.setPaidAmount(BigDecimal.ZERO);
        michaelBrown.getLoans().add(michaelLoan);
        customers.add(michaelBrown);
        loans.add(michaelLoan);

        // Customer 4: Emma Wilson - Business Loan (Rejected)
        Customer emmaWilson = new Customer();
        emmaWilson.setFirstName("Emma");
        emmaWilson.setLastName("Wilson");
        Loan emmaLoan = new Loan(
            "1004",
            LocalDate.now().minusDays(15),
            emmaWilson,
            LoanStatus.REJECTED,
            new BigDecimal("500000.00"),
            LoanType.BUSINESS
        );
        emmaLoan.setPaidAmount(BigDecimal.ZERO);
        emmaWilson.getLoans().add(emmaLoan);
        customers.add(emmaWilson);
        loans.add(emmaLoan);

        // Customer 5: Robert Taylor - Education Loan (Cancelled)
        Customer robertTaylor = new Customer();
        robertTaylor.setFirstName("Robert");
        robertTaylor.setLastName("Taylor");
        Loan robertLoan = new Loan(
            "1005",
            LocalDate.now().minusDays(10),
            robertTaylor,
            LoanStatus.CANCELLED,
            new BigDecimal("20000.00"),
            LoanType.EDUCATION
        );
        robertLoan.setPaidAmount(BigDecimal.ZERO);
        robertTaylor.getLoans().add(robertLoan);
        customers.add(robertTaylor);
        loans.add(robertLoan);

        db.setCustomers(customers);
        db.setLoans(loans);
    }

    public List<LoanDetails> getLoans() {
        return db.getLoans().stream().map(this::toLoanDetails).toList();
    }

    private Loan findLoan(String loanNumber, String firstName, String lastName) {
        return db.getLoans().stream()
                .filter(b -> b.getLoanNumber().equalsIgnoreCase(loanNumber))
                .filter(b -> b.getCustomer().getFirstName().equalsIgnoreCase(firstName))
                .filter(b -> b.getCustomer().getLastName().equalsIgnoreCase(lastName))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Loan not found"));
    }

    public LoanDetails getLoanDetails(String loanNumber, String firstName, String lastName) {
        return toLoanDetails(findLoan(loanNumber, firstName, lastName));
    }

    public void extraLoanPayment(String loanNumber, String firstName, String lastName, BigDecimal extraPayment) {
        var loan = findLoan(loanNumber, firstName, lastName);
        if (loan.getTotalAmount().compareTo(loan.getPaidAmount().add(extraPayment)) != -1) {
            throw new IllegalArgumentException("The extra payment cannot exceed the total amount of the loan.");
        }
        loan.setPaidAmount(loan.getPaidAmount().add(extraPayment));
    }

    public void cancelLoan(String loanNumber, String firstName, String lastName) {
        var loan = findLoan(loanNumber, firstName, lastName);
        if (!loan.getLoanStatus().equals(LoanStatus.PENDING)) {
            throw new IllegalArgumentException("Loan cannot be cancelled as it is not in PENDING status.");
        }
        loan.setLoanStatus(LoanStatus.CANCELLED);
    }

    private LoanDetails toLoanDetails(Loan loan) {
        return new LoanDetails(
                loan.getLoanNumber(),
                loan.getCustomer().getFirstName(),
                loan.getCustomer().getLastName(),
                loan.getApplicationDate(),
                loan.getLoanStatus(),
                loan.getTotalAmount(),
                loan.getPaidAmount(),
                loan.getLoanType().toString()
        );
    }
} 