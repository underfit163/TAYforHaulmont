package com.haulmont.testtask.userInterface.bankUI;

import com.haulmont.testtask.dao.AbstractDao;
import com.haulmont.testtask.entities.Bank;
import com.vaadin.data.BeanValidationBinder;
import com.vaadin.data.provider.ListDataProvider;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;

/**
 * Класс создает окно для добавления объекта Bank
 */
public class BankAddWindow extends Window {
    private ListDataProvider<Bank> bankListDataProvider;
    private AbstractDao<Bank> bankDao;
    private TextField nameBank;

    public BankAddWindow(AbstractDao<Bank> bankDao, ListDataProvider<Bank> bankListDataProvider) {
        this.bankListDataProvider = bankListDataProvider;
        this.bankDao = bankDao;
        nameBank = new TextField("Name Bank");
        setCaption("Add Bank");
        setModal(true);
        setResizable(false);
        center();
        setContent(addLayout());
    }

    private VerticalLayout addLayout() {
        VerticalLayout addLayout = new VerticalLayout();
        addLayout.setWidthFull();
        addLayout.addComponent(nameBank);
        addLayout.addComponents(addButton(), cancelButton());
        return addLayout;
    }

    private Button addButton() {
        Button addButton = new Button("Add");
        addButton.setWidthFull();
        addButton.addStyleName(ValoTheme.BUTTON_PRIMARY);
        BeanValidationBinder<Bank> binder = new BeanValidationBinder<>(Bank.class);
        binder.bind(nameBank, "nameBank");

        addButton.addClickListener(event -> {
            if (binder.isValid()) {
                Bank bank = new Bank(nameBank.getValue());
                bankDao.save(bank);
                bankListDataProvider.getItems().add(bank);
                bankListDataProvider.refreshAll();
                close();
            } else {
                Notification.show("Warning!", "Enter correct data.", Notification.Type.WARNING_MESSAGE)
                        .setStyleName(ValoTheme.NOTIFICATION_DARK);
            }
        });
        return addButton;
    }

    private Button cancelButton() {
        Button cancelButton = new Button("Cancel");
        cancelButton.setWidthFull();
        cancelButton.addStyleNames(ValoTheme.BUTTON_DANGER);
        cancelButton.addClickListener(event -> close());
        return cancelButton;
    }
}
