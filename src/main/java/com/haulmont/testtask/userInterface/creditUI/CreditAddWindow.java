package com.haulmont.testtask.userInterface.creditUI;

import com.haulmont.testtask.dao.AbstractDao;
import com.haulmont.testtask.dao.BankDao;
import com.haulmont.testtask.entities.Bank;
import com.haulmont.testtask.entities.Credit;
import com.vaadin.data.BeanValidationBinder;
import com.vaadin.data.converter.StringToBigDecimalConverter;
import com.vaadin.data.converter.StringToIntegerConverter;
import com.vaadin.data.provider.ListDataProvider;
import com.vaadin.data.validator.BeanValidator;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;

import java.math.BigDecimal;

/**
 * Класс создает окно для добавления объекта Credit
 */
public class CreditAddWindow extends Window {
    private ListDataProvider<Credit> creditListDataProvider;
    private AbstractDao<Credit> creditDao;
    private AbstractDao<Bank> bankDao;
    private TextField interestRate;
    private TextField limit;
    private ComboBox<Bank> bankComboBox;

    public CreditAddWindow(AbstractDao<Credit> dao, ListDataProvider<Credit> listDataProvider) {
        creditListDataProvider = listDataProvider;
        creditDao = dao;
        bankDao = new BankDao();
        interestRate = new TextField("Interest Rate");
        interestRate.setSizeFull();
        limit = new TextField("Limit");
        limit.setSizeFull();
        bankComboBox = new ComboBox<>("Bank");
        setCaption("Add Credit");
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
        addLayout.addComponent(interestRate);
        addLayout.addComponent(limit);
        addLayout.addComponents(addButton(), cancelButton());
        return addLayout;
    }

    private Button addButton() {
        Button addButton = new Button("Add");
        addButton.setWidthFull();
        addButton.addStyleName(ValoTheme.BUTTON_PRIMARY);
        BeanValidationBinder<Credit> binder = new BeanValidationBinder<>(Credit.class);
        binder.forField(interestRate).withConverter(
                        new StringToBigDecimalConverter("Must enter a number"))
                .withValidator(new BeanValidator(Credit.class, "interestRate"))
                .bind("interestRate");
        binder.forField(limit).withConverter(
                        new StringToIntegerConverter("Must enter a number"))
                .withValidator(new BeanValidator(Credit.class, "limit"))
                .bind("limit");
        binder.bind(bankComboBox, "fkBank");
        addButton.addClickListener(event -> {
            if (binder.isValid()) {
                Credit credit = new Credit(new BigDecimal(interestRate.getValue()), Integer.parseInt(limit.getValue()), bankComboBox.getValue());
                creditDao.save(credit);
                creditListDataProvider.getItems().add(credit);
                creditListDataProvider.refreshAll();
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
