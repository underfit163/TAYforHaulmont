package com.haulmont.testtask.userInterface.paymentUI;

import com.haulmont.testtask.dao.AbstractDao;
import com.haulmont.testtask.dao.OfferDao;
import com.haulmont.testtask.entities.Offer;
import com.haulmont.testtask.entities.Payment;
import com.vaadin.data.BeanValidationBinder;
import com.vaadin.data.converter.StringToIntegerConverter;
import com.vaadin.data.provider.ListDataProvider;
import com.vaadin.data.validator.BeanValidator;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;

/**
 * Класс создает окно для добавления объекта Payment
 */
public class PaymentAddWindow extends Window {
    private ListDataProvider<Payment> paymentListDataProvider;
    private AbstractDao<Payment> paymentDao;
    private AbstractDao<Offer> offerDao;

    private DateField paymentDate;
    private TextField paymentAmount;
    private TextField amountRepaymentBody;
    private TextField amountRepaymentInterest;
    private ComboBox<Offer> offerComboBox;
    private Offer offer;

    public PaymentAddWindow(AbstractDao<Payment> dao, ListDataProvider<Payment> listDataProvider, Offer offer) {
        paymentListDataProvider = listDataProvider;
        paymentDao = dao;
        offerDao = new OfferDao();
        this.offer = offer;

        paymentDate = new DateField("Payment Date");
        paymentAmount = new TextField("Payment Amount");
        amountRepaymentBody = new TextField("Amount Repayment Body");
        amountRepaymentInterest = new TextField("Amount Repayment Interest");

        paymentDate.setSizeFull();
        paymentAmount.setSizeFull();
        amountRepaymentBody.setSizeFull();
        amountRepaymentInterest.setSizeFull();
        offerComboBox = new ComboBox<>("Offer");

        setWidth("400px");
        setHeight("650px");
        setCaption("Add");
        setModal(true);
        setResizable(false);
        center();
        setContent(addLayout());
    }

    private VerticalLayout addLayout() {
        VerticalLayout addLayout = new VerticalLayout();
        addLayout.setWidthFull();

        offerComboBox.setItems(offerDao.selectAll());
        offerComboBox.setItemCaptionGenerator(Offer::toString);
        offerComboBox.setSizeFull();
        offerComboBox.setSelectedItem(offer);

        addLayout.addComponent(offerComboBox);
        addLayout.addComponent(paymentDate);
        addLayout.addComponent(paymentAmount);
        addLayout.addComponent(amountRepaymentBody);
        addLayout.addComponent(amountRepaymentInterest);
        addLayout.addComponents(addButton(), cancelButton());
        return addLayout;
    }

    private Button addButton() {
        Button addButton = new Button("Add");
        addButton.setWidthFull();
        addButton.addStyleName(ValoTheme.BUTTON_PRIMARY);
        BeanValidationBinder<Payment> binder = new BeanValidationBinder<>(Payment.class);

        binder.forField(paymentDate).bind("paymentDate");
        binder.forField(paymentAmount).withConverter(
                        new StringToIntegerConverter("Must enter a number"))
                .withValidator(new BeanValidator(Payment.class, "paymentAmount"))
                .withValidator(x -> x <= offerComboBox.getSelectedItem().get().getCreditAmount(),
                        "Limit is exceeded")
                .bind("paymentAmount");
        binder.forField(amountRepaymentBody).withConverter(
                        new StringToIntegerConverter("Must enter a number"))
                .withValidator(new BeanValidator(Payment.class, "amountRepaymentBody"))
                .withValidator(x -> x <= offerComboBox.getSelectedItem().get().getCreditAmount(),
                        "Limit is exceeded")
                .bind("amountRepaymentBody");
        binder.forField(amountRepaymentInterest).withConverter(
                        new StringToIntegerConverter("Must enter a number"))
                .withValidator(new BeanValidator(Payment.class, "amountRepaymentInterest"))
                .withValidator(x -> x <= offerComboBox.getSelectedItem().get().getCreditAmount(),
                        "Limit is exceeded")
                .bind("amountRepaymentInterest");
        binder.bind(offerComboBox, "fkOffer");

        addButton.addClickListener(event -> {
            if (binder.isValid()) {
                Payment paymant = new Payment(paymentDate.getValue(), Integer.parseInt(paymentAmount.getValue()),
                        Integer.parseInt(amountRepaymentBody.getValue()),
                        Integer.parseInt(amountRepaymentInterest.getValue()),
                        offerComboBox.getValue());
                paymentDao.save(paymant);
                paymentListDataProvider.getItems().add(paymant);
                paymentListDataProvider.refreshAll();
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
