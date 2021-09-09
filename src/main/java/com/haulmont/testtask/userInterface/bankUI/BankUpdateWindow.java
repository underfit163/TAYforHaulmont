package com.haulmont.testtask.userInterface.bankUI;

import com.haulmont.testtask.dao.AbstractDao;
import com.haulmont.testtask.entities.Bank;
import com.vaadin.data.BeanValidationBinder;
import com.vaadin.data.ValidationException;
import com.vaadin.data.provider.ListDataProvider;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
/**
 * Окно для обновления банка
 */
public class BankUpdateWindow extends Window {
    private ListDataProvider<Bank> bankListDataProvider;
    private AbstractDao<Bank> bankDao;
    private Bank bank;
    private TextField nameBank;

    public BankUpdateWindow(AbstractDao<Bank> bankDao, ListDataProvider<Bank> bankListDataProvider, Bank bank) {
        this.bankDao = bankDao;
        this.bankListDataProvider = bankListDataProvider;
        this.bank = bank;
        nameBank = new TextField("Name Bank");
        setCaption("Update");
        setModal(true);
        setResizable(false);
        center();
        setContent(updateLayout());
    }

    private VerticalLayout updateLayout() {
        VerticalLayout updateLayout = new VerticalLayout();
        updateLayout.setWidthFull();
        updateLayout.addComponent(nameBank);
        updateLayout.addComponents(updateButton(), cancelButton());
        return updateLayout;
    }

    private Button updateButton() {
        Button updateButton = new Button("Update");
        updateButton.setWidthFull();
        updateButton.addStyleName(ValoTheme.BUTTON_FRIENDLY);
        BeanValidationBinder<Bank> binder = new BeanValidationBinder<>(Bank.class);
        binder.bind(nameBank, "nameBank");
        binder.readBean(bank);

        updateButton.addClickListener(event -> {
            if (binder.isValid()) {
                try {
                    binder.writeBean(bank);
                    bankDao.update(bank);
                    bankListDataProvider.refreshItem(bank);
                    close();
                } catch (ValidationException e) {
                    Notification.show("Validation error count: "
                            + e.getValidationErrors().size());
                }

            } else {
                Notification.show("Warning!", "Enter correct data.", Notification.Type.WARNING_MESSAGE);
            }
        });
        return updateButton;
    }

    private Button cancelButton() {
        Button cancelButton = new Button("Cancel");
        cancelButton.setWidthFull();
        cancelButton.addStyleNames(ValoTheme.BUTTON_DANGER);
        cancelButton.addClickListener(event -> close());
        return cancelButton;
    }
}
