package com.haulmont.testtask.entities;


import org.hibernate.validator.constraints.Range;

import javax.persistence.*;
import javax.validation.constraints.Digits;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Credit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int idCredit;
    @NotNull
    @Range(min = 0, max = 100)
    @Digits(integer = 3, fraction = 1)
    @Column(name = "interestRate", nullable = false)
    private BigDecimal interestRate;
    @NotNull
    @Column(name = "limit", nullable = false)
    private int limit;
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idBank")
    private Bank fkBank;
    @OneToMany(mappedBy = "fkCredit", cascade = CascadeType.ALL)
    private List<Offer> offers;

    public Credit() {
    }

    public Credit(BigDecimal interestRate, int limit, Bank bank) {
        this.interestRate = interestRate;
        this.limit = limit;
        this.fkBank = bank;
        this.offers = new ArrayList<>();
    }

    public int getIdCredit() {
        return idCredit;
    }

    public BigDecimal getInterestRate() {
        return interestRate;
    }

    public void setInterestRate(BigDecimal interestRate) {
        this.interestRate = interestRate;
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
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
        offer.setFkCredit(this);
        offers.add(offer);
    }

    @Override
    public String toString() {
        return "Interest Rate = " + interestRate +
                "| Limit = " + limit;
    }
}
