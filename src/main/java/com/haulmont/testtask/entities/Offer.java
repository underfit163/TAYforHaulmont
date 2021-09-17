package com.haulmont.testtask.entities;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Offer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int idOffer;
    @NotNull
    @Min(0)
    @Column(name = "creditAmount", nullable = false)
    private int creditAmount;
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idClient")
    private Client fkClient;
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idCredit")
    private Credit fkCredit;
    @OneToMany(mappedBy = "fkOffer", cascade = CascadeType.ALL)
    private List<Payment> payments;

    public Offer() {
    }

    public Offer(int creditAmount, Client client, Credit credit) {
        this.creditAmount = creditAmount;
        this.fkClient = client;
        this.fkCredit = credit;
        this.payments = new ArrayList<>();
    }

    public int getIdOffer() {
        return idOffer;
    }

    public int getCreditAmount() {
        return creditAmount;
    }

    public void setCreditAmount(int creditAmount) {
        this.creditAmount = creditAmount;
    }

    public Client getFkClient() {
        return fkClient;
    }

    public void setFkClient(Client fkClient) {
        this.fkClient = fkClient;
    }

    public Credit getFkCredit() {
        return fkCredit;
    }

    public void setFkCredit(Credit fkCredit) {
        this.fkCredit = fkCredit;
    }

    public List<Payment> getPayments() {
        return payments;
    }

    public void setPayments(List<Payment> payments) {
        this.payments = payments;
    }

    public void addPayment(Payment payment) {
        payment.setFkOffer(this);
        payments.add(payment);
    }

    @Override
    public String toString() {
        return "CreditAmount = " + creditAmount;
    }
}
