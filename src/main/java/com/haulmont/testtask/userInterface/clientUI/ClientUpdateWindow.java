package com.haulmont.testtask.userInterface.clientUI;

import com.haulmont.testtask.dao.AbstractDao;
import com.haulmont.testtask.dao.BankDao;
import com.haulmont.testtask.entities.Bank;
import com.haulmont.testtask.entities.Client;
import com.vaadin.data.BeanValidationBinder;
import com.vaadin.data.ValidationException;
import com.vaadin.data.converter.StringToIntegerConverter;
import com.vaadin.data.provider.ListDataProvider;
import com.vaadin.data.validator.BeanValidator;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;

/**
 * Окно для обновления клиентов
 */
public class ClientUpdateWindow extends Window {
    private ListDataProvider<Client> listDataProvider;
    private AbstractDao<Client> clientDao;
    private AbstractDao<Bank> bankDao;
    private Client client;
    private TextField nameClient;
    private TextField phone;
    private TextField email;
    private TextField passport;
    private ComboBox<Bank> bankComboBox;

    public ClientUpdateWindow(AbstractDao<Client> dao, ListDataProvider<Client> listDataProvider, Client client) {
        this.clientDao = dao;
        this.bankDao = new BankDao();
        this.listDataProvider = listDataProvider;
        this.client = client;
        nameClient = new TextField("Name Client");
        phone = new TextField("Phone");
        email = new TextField("Email");
        passport = new TextField("Passport");
        nameClient.setSizeFull();
        phone.setSizeFull();
        email.setSizeFull();
        passport.setSizeFull();
        bankComboBox = new ComboBox<>("Bank");
        setCaption("Update");
        setModal(true);
        setResizable(false);
        center();
        setContent(updateLayout());
    }

    private VerticalLayout updateLayout() {
        VerticalLayout updateLayout = new VerticalLayout();
        updateLayout.setWidthFull();
        bankComboBox.setItems(bankDao.selectAll());
        bankComboBox.setItemCaptionGenerator(Bank::toString);
        bankComboBox.setSizeFull();
        updateLayout.addComponent(bankComboBox);
        updateLayout.addComponent(nameClient);
        updateLayout.addComponent(phone);
        updateLayout.addComponent(email);
        updateLayout.addComponent(passport);
        updateLayout.addComponents(updateButton(), cancelButton());
        return updateLayout;
    }

    private Button updateButton() {
        Button updateButton = new Button("Update");
        updateButton.setWidthFull();
        updateButton.addStyleName(ValoTheme.BUTTON_FRIENDLY);
        BeanValidationBinder<Client> binder = new BeanValidationBinder<>(Client.class);
        binder.bind(nameClient, "fio");
        binder.bind(phone, "phone");
        binder.bind(email, "email");
        binder.forField(passport).withConverter(
                        new StringToIntegerConverter("Must enter a number"))
                .withValidator(new BeanValidator(Client.class, "passport"))
                .bind("passport");
        binder.bind(bankComboBox, "fkBank");

        binder.readBean(client);

        updateButton.addClickListener(event -> {
            if (binder.isValid()) {
                try {
                    binder.writeBean(client);
                    clientDao.update(client);
                    listDataProvider.refreshItem(client);
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
