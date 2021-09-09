package com.haulmont.testtask.entities;


import org.hibernate.validator.constraints.Length;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Bank {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int idBank;
    @NotNull
    @Length(min = 3)
    @Column(name = "nameBank", length = 50, nullable = false)
    private String nameBank;
    @OneToMany(mappedBy = "fkBank", cascade = CascadeType.ALL)
    private List<Client> clients;
    @OneToMany(mappedBy = "fkBank", cascade = CascadeType.ALL)
    private List<Credit> credits;

    public Bank() {
    }

    public Bank(String nameBank) {
        this.nameBank = nameBank;
        this.clients = new ArrayList<>();
        this.credits = new ArrayList<>();
    }

    public int getIdBank() {
        return idBank;
    }

    public String getNameBank() {
        return nameBank;
    }

    public void setNameBank(String nameBank) {
        this.nameBank = nameBank;
    }

    public List<Client> getClients() {
        return clients;
    }

    public void setClients(List<Client> clients) {
        this.clients = clients;
    }

    public List<Credit> getCredits() {
        return credits;
    }

    public void setCredits(List<Credit> credits) {
        this.credits = credits;
    }

    public void addClient(Client client) {
        client.setFkBank(this);
        clients.add(client);
    }

    public void addCredit(Credit credit) {
        credit.setFkBank(this);
        credits.add(credit);
    }

    @Override
    public String toString() {
        return nameBank;
    }
}
