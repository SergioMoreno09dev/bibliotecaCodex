package com.universidad.biblio.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;

import java.util.Date;
import java.util.Objects;

@Entity
public class Publisher {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String country;

    @Temporal(TemporalType.DATE)
    private Date foundingYear;

    public Publisher() {
    }

    public Publisher(String name, String country, Date foundingYear) {
        this.name = name;
        this.country = country;
        this.foundingYear = foundingYear;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public Date getFoundingYear() {
        return foundingYear;
    }

    public void setFoundingYear(Date foundingYear) {
        this.foundingYear = foundingYear;
    }

    @Override
    public String toString() {
        return "Publisher{" +
                "name='" + name + '\'' +
                ", country='" + country + '\'' +
                ", foundingYear=" + foundingYear +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Publisher publisher)) return false;
        return Objects.equals(name, publisher.name) && Objects.equals(country, publisher.country) && Objects.equals(foundingYear, publisher.foundingYear);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, country, foundingYear);
    }
}
