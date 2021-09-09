package com.haulmont.testtask.entities;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotNull;
import javax.persistence.*;
import javax.validation.constraints.Pattern;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Client {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int idClient;
    @NotNull
    @Length(min = 3)
    @Column(name = "fio", length = 100, nullable = false)
    private String fio;
    @NotNull
    @Pattern(regexp = "^((8|\\+7)[\\- ]?)?(\\(?\\d{3}\\)?[\\- ]?)?[\\d\\- ]{7,10}$")
    @Column(name = "phone", length = 20, nullable = false)
    private String phone;
    @Pattern(message = "Email address has invalid format: ${validatedValue}",
            regexp = "^[a-zA-Z0-9_.+-]+@[a-zA-Z0-9-]+\\.[a-zA-Z0-9-.]+$")
    @Column(name = "email", length = 50)
    private String email;
    @NotNull
    @Range(min = 100000, max = 999999999)
    @Column(name = "passport", nullable = false, unique = true)
    private int passport;
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idBank")
    private Bank fkBank;
    @OneToMany(mappedBy = "fkClient", cascade = CascadeType.ALL)
    private List<Offer> offers;

    public Client() {
    }

    public Client(String fio, String phone, String email, int passport, Bank bank) {
        this.fio = fio;
        this.phone = phone;
        this.email = email;
        this.passport = passport;
        this.fkBank = bank;
        this.offers = new ArrayList<>();
    }

    public int getIdClient() {
        return idClient;
    }

    public String getFio() {
        return fio;
    }

    public void setFio(String fio) {
        this.fio = fio;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getPassport() {
        return passport;
    }

    public void setPassport(int passport) {
        this.passport = passport;
    }

    public Bank getFkBank() {
        return fkBank;
    }

    public void setFkBank(Bank fkBank) {
        this.fkBank = fkBank;
    }

    public List<Offer> getOffers() {
        return offers;
    }

    public void setOffers(List<Offer> offers) {
        this.offers = offers;
    }

    public void addOffer(Offer offer) {
        offer.setFkClient(this);
        offers.add(offer);
    }

    @Override
    public String toString() {
        return "Name client = " + fio +
                "| Passport = " + passport;
    }
}
