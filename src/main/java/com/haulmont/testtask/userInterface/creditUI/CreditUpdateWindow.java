package com.haulmont.testtask.userInterface.creditUI;

import com.haulmont.testtask.dao.AbstractDao;
import com.haulmont.testtask.dao.BankDao;
import com.haulmont.testtask.entities.Bank;
import com.haulmont.testtask.entities.Credit;
import com.vaadin.data.BeanValidationBinder;
import com.vaadin.data.ValidationException;
import com.vaadin.data.converter.StringToBigDecimalConverter;
import com.vaadin.data.converter.StringToIntegerConverter;
import com.vaadin.data.provider.ListDataProvider;
import com.vaadin.data.validator.BeanValidator;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;

/**
 * Окно для обновления кредита
 */
public class CreditUpdateWindow extends Window {
    private ListDataProvider<Credit> listDataProvider;
    private AbstractDao<Credit> creditDao;
    private AbstractDao<Bank> bankDao;
    private Credit credit;
    private TextField interestRate;
    private TextField limit;
    private ComboBox<Bank> bankComboBox;

    public CreditUpdateWindow(AbstractDao<Credit> dao, ListDataProvider<Credit> listDataProvider, Credit credit) {
        this.creditDao = dao;
        this.bankDao = new BankDao();
        this.listDataProvider = listDataProvider;
        this.credit = credit;
        interestRate = new TextField("Interest Rate");
        interestRate.setSizeFull();
        limit = new TextField("Limit");
        limit.setSizeFull();
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
        updateLayout.addComponent(interestRate);
        updateLayout.addComponent(limit);
        updateLayout.addComponents(updateButton(), cancelButton());
        return updateLayout;
    }

    private Button updateButton() {
        Button updateButton = new Button("Update");
        updateButton.setWidthFull();
        updateButton.addStyleName(ValoTheme.BUTTON_FRIENDLY);
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
        binder.readBean(credit);

        updateButton.addClickListener(event -> {
            if (binder.isValid()) {
                try {
                    binder.writeBean(credit);
                    creditDao.update(credit);
                    listDataProvider.refreshItem(credit);
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
