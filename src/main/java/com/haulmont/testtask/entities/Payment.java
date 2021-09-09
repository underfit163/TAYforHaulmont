package com.haulmont.testtask.entities;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Entity
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int idPayment;
    @NotNull
    @Column(name = "paymentDate", nullable = false)
    private LocalDate paymentDate;
    @NotNull
    @Column(name = "paymentAmount", nullable = false)
    private int paymentAmount;
    @NotNull
    @Column(name = "amountRepaymentBody", nullable = false)
    private int amountRepaymentBody;
    @NotNull
    @Column(name = "amountRepaymentInterest", nullable = false)
    private int amountRepaymentInterest;
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idOffet")
    private Offer fkOffer;

    public Payment() {
    }

    public Payment(LocalDate paymentDate, int paymentAmount, int amountRepaymentBody, int amountRepaymentInterest, Offer offer) {
        this.paymentDate = paymentDate;
        this.paymentAmount = paymentAmount;
        this.amountRepaymentBody = amountRepaymentBody;
        this.amountRepaymentInterest = amountRepaymentInterest;
        this.fkOffer = offer;
    }

    public int getIdPayment() {
        return idPayment;
    }

    public LocalDate getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(LocalDate paymentDate) {
        this.paymentDate = paymentDate;
    }

    public int getPaymentAmount() {
        return paymentAmount;
    }

    public void setPaymentAmount(int paymentAmount) {
        this.paymentAmount = paymentAmount;
    }

    public int getAmountRepaymentBody() {
        return amountRepaymentBody;
    }

    public void setAmountRepaymentBody(int amountRepaymentBody) {
        this.amountRepaymentBody = amountRepaymentBody;
    }

    public int getAmountRepaymentInterest() {
        return amountRepaymentInterest;
    }

    public void setAmountRepaymentInterest(int amountRepaymentInterest) {
        this.amountRepaymentInterest = amountRepaymentInterest;
    }

    public Offer getFkOffer() {
        return fkOffer;
    }

    public void setFkOffer(Offer fkOffer) {
        this.fkOffer = fkOffer;
    }

    @Override
    public String toString() {
        return "Payment{" +
                "idPayment= " + idPayment +
                ", paymentDate= " + paymentDate +
                ", paymentAmount= " + paymentAmount +
                ", amountRepaymentBody= " + amountRepaymentBody +
                ", amountRepaymentInterest= " + amountRepaymentInterest +
                '}';
    }
}
