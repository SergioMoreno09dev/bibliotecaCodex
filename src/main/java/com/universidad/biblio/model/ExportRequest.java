package com.universidad.biblio.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;

import java.util.Date;
import java.util.Objects;

@Entity
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class ExportRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String format;

    @Temporal(TemporalType.TIMESTAMP)
    private Date exportDate;
    private String appliedFilters;

    @ManyToOne
    private User user;

    @ManyToOne
    private Report report;

    public ExportRequest() {
    }

    public ExportRequest(String format, Date exportDate, String appliedFilters, User user, Report report) {
        this.format = format;
        this.exportDate = exportDate;
        this.appliedFilters = appliedFilters;
        this.user = user;
        this.report = report;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public Date getExportDate() {
        return exportDate;
    }

    public void setExportDate(Date exportDate) {
        this.exportDate = exportDate;
    }

    public String getAppliedFilters() {
        return appliedFilters;
    }

    public void setAppliedFilters(String appliedFilters) {
        this.appliedFilters = appliedFilters;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Report getReport() {
        return report;
    }

    public void setReport(Report report) {
        this.report = report;
    }

    @Override
    public String toString() {
        return "ExportRequest{" +
                "format='" + format + '\'' +
                ", exportDate=" + exportDate +
                ", appliedFilters='" + appliedFilters + '\'' +
                ", user=" + user +
                ", report=" + report +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof ExportRequest that)) return false;
        return Objects.equals(exportDate, that.exportDate) && Objects.equals(user, that.user) && Objects.equals(report, that.report);
    }

    @Override
    public int hashCode() {
        return Objects.hash(exportDate, user, report);
    }
}
