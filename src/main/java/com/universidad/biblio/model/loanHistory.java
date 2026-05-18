package com.universidad.biblio.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;

import java.util.Date;
import java.util.Objects;

@Entity
@Table(name = "loan_history")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class loanHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String previousStatus;
    private String newStatus;

    @Temporal(TemporalType.TIMESTAMP)
    private Date changeDate;

    @ManyToOne
    private Loan loan;

    public loanHistory() {
    }

    public loanHistory(String previousStatus, String newStatus, Date changeDate, Loan loan) {
        this.previousStatus = previousStatus;
        this.newStatus = newStatus;
        this.changeDate = changeDate;
        this.loan = loan;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPreviousStatus() {
        return previousStatus;
    }

    public void setPreviousStatus(String previousStatus) {
        this.previousStatus = previousStatus;
    }

    public String getNewStatus() {
        return newStatus;
    }

    public void setNewStatus(String newStatus) {
        this.newStatus = newStatus;
    }

    public Date getChangeDate() {
        return changeDate;
    }

    public void setChangeDate(Date changeDate) {
        this.changeDate = changeDate;
    }

    public Loan getLoan() {
        return loan;
    }

    public void setLoan(Loan loan) {
        this.loan = loan;
    }

    @Override
    public String toString() {
        return "loanHistory{" +
                "previousStatus='" + previousStatus + '\'' +
                ", newStatus='" + newStatus + '\'' +
                ", changeDate=" + changeDate +
                ", loan=" + loan +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof loanHistory that)) return false;
        return Objects.equals(changeDate, that.changeDate) && Objects.equals(loan, that.loan);
    }

    @Override
    public int hashCode() {
        return Objects.hash(changeDate, loan);
    }
}
