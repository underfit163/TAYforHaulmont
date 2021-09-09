package com.haulmont.testtask.userInterface.offerUI;

import com.haulmont.testtask.dao.AbstractDao;
import com.haulmont.testtask.dao.ClientDao;
import com.haulmont.testtask.dao.CreditDao;
import com.haulmont.testtask.entities.Client;
import com.haulmont.testtask.entities.Credit;
import com.haulmont.testtask.entities.Offer;
import com.vaadin.data.BeanValidationBinder;
import com.vaadin.data.ValidationException;
import com.vaadin.data.converter.StringToIntegerConverter;
import com.vaadin.data.provider.ListDataProvider;
import com.vaadin.data.validator.BeanValidator;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;

import java.util.NoSuchElementException;
/**
 * Окно для обновления кредитного предложения
 */
public class OfferUpdateWindow extends Window {
    private Offer offer;
    private ListDataProvider<Offer> offerListDataProvider;
    private AbstractDao<Offer> offerDao;
    private AbstractDao<Credit> creditDao;
    private AbstractDao<Client> clientDao;
    private TextField creditAmount;
    private ComboBox<Credit> creditComboBox;
    private ComboBox<Client> clientComboBox;

    public OfferUpdateWindow(AbstractDao<Offer> dao, ListDataProvider<Offer> listDataProvider, Offer offer) {
        this.offer = offer;
        offerListDataProvider = listDataProvider;
        offerDao = dao;
        clientDao = new ClientDao();
        creditDao = new CreditDao();
        creditAmount = new TextField("Credit Amount");
        creditAmount.setSizeFull();
        creditComboBox = new ComboBox<>("Credit");
        clientComboBox = new ComboBox<>("Client");
        setWidth("500px");
        setCaption("Update");
        setModal(true);
        setResizable(false);
        center();
        setContent(updateLayout());
    }

    private VerticalLayout updateLayout() {
        VerticalLayout updateLayout = new VerticalLayout();
        updateLayout.setWidthFull();
        clientComboBox.setItems(clientDao.selectAll());
        clientComboBox.setItemCaptionGenerator(Client::toString);
        clientComboBox.setSizeFull();
        creditComboBox.setItems(creditDao.selectAll());
        creditComboBox.setItemCaptionGenerator(Credit::toString);
        creditComboBox.setSizeFull();
        updateLayout.addComponent(clientComboBox);
        updateLayout.addComponent(creditComboBox);
        updateLayout.addComponent(creditAmount);
        updateLayout.addComponents(updateButton(), cancelButton());
        return updateLayout;
    }

    private Button updateButton() {
        Button updateButton = new Button("Update");
        updateButton.setWidthFull();
        updateButton.addStyleName(ValoTheme.BUTTON_FRIENDLY);
        BeanValidationBinder<Offer> binder = new BeanValidationBinder<>(Offer.class);
        try {
            binder.forField(creditAmount).withConverter(
                            new StringToIntegerConverter("Must enter a number"))
                    .withValidator(new BeanValidator(Offer.class, "creditAmount"))
                    .withValidator(x -> x <= creditComboBox.getSelectedItem().get().getLimit(),
                            "Limit is exceeded")
                    .bind("creditAmount");
        } catch (NoSuchElementException e) {
            Notification.show("Error!", "Item not found.", Notification.Type.ERROR_MESSAGE);
        }
        binder.bind(clientComboBox, "fkClient");
        binder.bind(creditComboBox, "fkCredit");
        binder.readBean(offer);

        updateButton.addClickListener(event -> {
            if (binder.isValid()) {
                try {
                    binder.writeBean(offer);
                    offerDao.update(offer);
                    offerListDataProvider.refreshItem(offer);
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
