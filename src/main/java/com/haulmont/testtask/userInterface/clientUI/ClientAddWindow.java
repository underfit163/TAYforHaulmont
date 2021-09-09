package com.haulmont.testtask.userInterface.clientUI;

import com.haulmont.testtask.dao.AbstractDao;
import com.haulmont.testtask.dao.BankDao;
import com.haulmont.testtask.entities.Bank;
import com.haulmont.testtask.entities.Client;
import com.vaadin.data.BeanValidationBinder;
import com.vaadin.data.converter.StringToIntegerConverter;
import com.vaadin.data.provider.ListDataProvider;
import com.vaadin.data.validator.BeanValidator;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
/**
 * Класс создает окно для добавления объекта Client
 */
public class ClientAddWindow extends Window {
    private ListDataProvider<Client> clientListDataProvider;
    private AbstractDao<Client> clientDao;
    private AbstractDao<Bank> bankDao;
    private TextField nameClient;
    private TextField phone;
    private TextField email;
    private TextField passport;
    private ComboBox<Bank> bankComboBox;

    public ClientAddWindow(AbstractDao<Client> dao, ListDataProvider<Client> listDataProvider) {
        clientListDataProvider = listDataProvider;
        clientDao = dao;
        bankDao = new BankDao();
        nameClient = new TextField("Name Client");
        phone = new TextField("Phone");
        email = new TextField("Email");
        passport = new TextField("Passport");
        nameClient.setSizeFull();
        phone.setSizeFull();
        email.setSizeFull();
        passport.setSizeFull();
        bankComboBox = new ComboBox<>("Bank");
        setCaption("Add Client");
        setModal(true);
        setResizable(false);
        center();
        setContent(addLayout());
    }

    private VerticalLayout addLayout() {
        VerticalLayout addLayout = new VerticalLayout();
        addLayout.setWidthFull();

        bankComboBox.setItems(bankDao.selectAll());
        bankComboBox.setItemCaptionGenerator(Bank::toString);
        bankComboBox.setSizeFull();
        addLayout.addComponent(bankComboBox);
        addLayout.addComponent(nameClient);
        addLayout.addComponent(phone);
        addLayout.addComponent(email);
        addLayout.addComponent(passport);
        addLayout.addComponents(addButton(), cancelButton());
        return addLayout;
    }

    private Button addButton() {
        Button addButton = new Button("Add");
        addButton.setWidthFull();
        addButton.addStyleName(ValoTheme.BUTTON_PRIMARY);
        BeanValidationBinder<Client> binder = new BeanValidationBinder<>(Client.class);
        binder.bind(nameClient, "fio");
        binder.bind(phone, "phone");
        binder.bind(email, "email");
        binder.forField(passport).withConverter(
                        new StringToIntegerConverter("Must enter a number"))
                .withValidator(new BeanValidator(Client.class, "passport"))
                .bind("passport");
        binder.bind(bankComboBox, "fkBank");

        addButton.addClickListener(event -> {
            if (binder.isValid()) {
                Client client = new Client(nameClient.getValue(), phone.getValue(),
                        email.getValue(), Integer.parseInt(passport.getValue()), bankComboBox.getValue());
                clientDao.save(client);
                clientListDataProvider.getItems().add(client);
                clientListDataProvider.refreshAll();
                close();
            } else {
                Notification.show("Warning!", "Enter correct data.", Notification.Type.WARNING_MESSAGE);
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
