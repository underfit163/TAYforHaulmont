package com.haulmont.testtask.userInterface.paymentUI;

import com.haulmont.testtask.dao.AbstractDao;
import com.haulmont.testtask.dao.OfferDao;
import com.haulmont.testtask.entities.Offer;
import com.haulmont.testtask.entities.Payment;
import com.vaadin.data.BeanValidationBinder;
import com.vaadin.data.ValidationException;
import com.vaadin.data.converter.StringToIntegerConverter;
import com.vaadin.data.provider.ListDataProvider;
import com.vaadin.data.validator.BeanValidator;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;

/**
 * Окно для обновления графика платежей
 */
public class PaymentUpdateWindow extends Window {
    private ListDataProvider<Payment> paymentListDataProvider;
    private AbstractDao<Payment> paymentDao;
    private AbstractDao<Offer> offerDao;
    private Payment payment;

    private DateField paymentDate;
    private TextField paymentAmount;
    private TextField amountRepaymentBody;
    private TextField amountRepaymentInterest;
    private ComboBox<Offer> offerComboBox;

    public PaymentUpdateWindow(AbstractDao<Payment> dao, ListDataProvider<Payment> listDataProvider, Payment payment) {
        this.paymentDao = dao;
        this.offerDao = new OfferDao();
        this.paymentListDataProvider = listDataProvider;
        this.payment = payment;
        paymentDate = new DateField("Payment Date");
        paymentAmount = new TextField("Payment Amount");
        amountRepaymentBody = new TextField("Amount Repayment Body");
        amountRepaymentInterest = new TextField("Amount Repayment Interest");

        paymentDate.setSizeFull();
        paymentAmount.setSizeFull();
        amountRepaymentBody.setSizeFull();
        amountRepaymentInterest.setSizeFull();
        offerComboBox = new ComboBox<>("Offer");

        setCaption("Update");
        setModal(true);
        setResizable(false);
        center();
        setContent(updateLayout());
    }

    private VerticalLayout updateLayout() {
        VerticalLayout updateLayout = new VerticalLayout();
        updateLayout.setWidthFull();
        setWidth("400px");
        setHeight("650px");
        offerComboBox.setItems(offerDao.selectAll());
        offerComboBox.setItemCaptionGenerator(Offer::toString);
        offerComboBox.setSizeFull();
        updateLayout.addComponent(offerComboBox);
        updateLayout.addComponent(paymentDate);
        updateLayout.addComponent(paymentAmount);
        updateLayout.addComponent(amountRepaymentBody);
        updateLayout.addComponent(amountRepaymentInterest);
        updateLayout.addComponents(updateButton(), cancelButton());
        return updateLayout;
    }

    private Button updateButton() {
        Button updateButton = new Button("Update");
        updateButton.setWidthFull();
        updateButton.addStyleName(ValoTheme.BUTTON_FRIENDLY);
        BeanValidationBinder<Payment> binder = getPaymentBeanValidationBinder();
        updateButton.addClickListener(event -> {
            if (binder.isValid()) {
                try {
                    binder.writeBean(payment);
                    paymentDao.update(payment);
                    paymentListDataProvider.refreshItem(payment);
                    close();
                } catch (ValidationException e) {
                    Notification.show("Validation error count: "
                            + e.getValidationErrors().size()).setStyleName(ValoTheme.NOTIFICATION_DARK);
                }

            } else {
                Notification.show("Warning!", "Enter correct data.", Notification.Type.WARNING_MESSAGE)
                        .setStyleName(ValoTheme.NOTIFICATION_DARK);
            }
        });
        return updateButton;
    }

    private BeanValidationBinder<Payment> getPaymentBeanValidationBinder() {
        BeanValidationBinder<Payment> binder = new BeanValidationBinder<>(Payment.class);

        binder.forField(paymentDate).bind("paymentDate");
        binder.forField(paymentAmount).withConverter(
                        new StringToIntegerConverter("Must enter a number"))
                .withValidator(new BeanValidator(Payment.class, "paymentAmount"))
                .withValidator(x -> x <= offerComboBox.getValue().getCreditAmount(),
                        "Limit is exceeded")
                .bind("paymentAmount");
        binder.forField(amountRepaymentBody).withConverter(
                        new StringToIntegerConverter("Must enter a number"))
                .withValidator(new BeanValidator(Payment.class, "amountRepaymentBody"))
                .withValidator(x -> x <= offerComboBox.getValue().getCreditAmount(),
                        "Limit is exceeded")
                .bind("amountRepaymentBody");
        binder.forField(amountRepaymentInterest).withConverter(
                        new StringToIntegerConverter("Must enter a number"))
                .withValidator(new BeanValidator(Payment.class, "amountRepaymentInterest"))
                .withValidator(x -> x <= offerComboBox.getValue().getCreditAmount(),
                        "Limit is exceeded")
                .bind("amountRepaymentInterest");
        binder.bind(offerComboBox, "fkOffer");
        binder.readBean(payment);
        return binder;
    }

    private Button cancelButton() {
        Button cancelButton = new Button("Cancel");
        cancelButton.setWidthFull();
        cancelButton.addStyleNames(ValoTheme.BUTTON_DANGER);
        cancelButton.addClickListener(event -> close());
        return cancelButton;
    }
}
