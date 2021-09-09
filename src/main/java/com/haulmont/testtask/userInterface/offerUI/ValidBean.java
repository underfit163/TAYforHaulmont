package com.haulmont.testtask.userInterface.offerUI;

/**
 * Класс для прохождения валидации
 */
public class ValidBean {
    private int creditTerm;

    public ValidBean() {
    }

    public ValidBean(int creditTerm) {
        this.creditTerm = creditTerm;
    }

    public int getCreditTerm() {
        return creditTerm;
    }

    public void setCreditTerm(int creditTerm) {
        this.creditTerm = creditTerm;
    }
}
