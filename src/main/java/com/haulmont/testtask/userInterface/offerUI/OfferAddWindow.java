package com.haulmont.testtask.userInterface.offerUI;

import com.haulmont.testtask.dao.AbstractDao;
import com.haulmont.testtask.dao.ClientDao;
import com.haulmont.testtask.dao.CreditDao;
import com.haulmont.testtask.dao.PaymentDao;
import com.haulmont.testtask.entities.Client;
import com.haulmont.testtask.entities.Credit;
import com.haulmont.testtask.entities.Offer;
import com.haulmont.testtask.entities.Payment;
import com.vaadin.data.BeanValidationBinder;
import com.vaadin.data.Binder;
import com.vaadin.data.converter.StringToIntegerConverter;
import com.vaadin.data.provider.ListDataProvider;
import com.vaadin.data.validator.BeanValidator;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;

import java.time.LocalDate;
import java.util.NoSuchElementException;

/**
 * Класс создает окно для добавления объекта Offer
 */
public class OfferAddWindow extends Window {
    private ListDataProvider<Offer> offerListDataProvider;
    private AbstractDao<Offer> offerDao;
    private AbstractDao<Credit> creditDao;
    private AbstractDao<Client> clientDao;
    private AbstractDao<Payment> paymentDao;
    private TextField creditTerm;
    private TextField creditAmount;
    private TextField totalAmount;
    private TextField monthlyPayment;
    private ComboBox<Credit> creditComboBox;
    private ComboBox<Client> clientComboBox;

    BeanValidationBinder<Offer> binder;
    Binder<ValidBean> supBinder;

    public OfferAddWindow(AbstractDao<Offer> dao, ListDataProvider<Offer> listDataProvider) {
        offerListDataProvider = listDataProvider;
        offerDao = dao;
        paymentDao = new PaymentDao();
        clientDao = new ClientDao();
        creditDao = new CreditDao();
        creditAmount = new TextField("Credit Amount");
        creditAmount.setSizeFull();
        creditTerm = new TextField("Credit Term(month)");
        creditTerm.setSizeFull();
        totalAmount = new TextField("Total Amount");
        totalAmount.setSizeFull();
        totalAmount.setReadOnly(true);
        monthlyPayment = new TextField("Monthly Payment");
        monthlyPayment.setSizeFull();
        monthlyPayment.setReadOnly(true);
        creditComboBox = new ComboBox<>("Credit");
        clientComboBox = new ComboBox<>("Client");
        setCaption("Offer");
        setModal(true);
        setWidth("700px");
        setHeightFull();
        setResizable(false);
        center();
        setContent(addLayout());
    }

    private VerticalLayout addLayout() {
        VerticalLayout addLayout = new VerticalLayout();
        addLayout.setWidthFull();
        clientComboBox.setItems(clientDao.selectAll());
        clientComboBox.setItemCaptionGenerator(Client::toString);
        clientComboBox.setSizeFull();
        creditComboBox.setItems(creditDao.selectAll());
        creditComboBox.setItemCaptionGenerator(Credit::toString);
        creditComboBox.setSizeFull();
        addLayout.addComponent(clientComboBox);
        addLayout.addComponent(creditComboBox);
        addLayout.addComponent(creditAmount);
        addLayout.addComponent(creditTerm);
        addLayout.addComponent(totalAmount);
        addLayout.addComponent(monthlyPayment);
        addLayout.addComponents(checkButton(), addButton(), cancelButton());
        return addLayout;
    }

    private Button addButton() {
        Button addButton = new Button("Generate credit");
        addButton.setWidthFull();
        addButton.addStyleName(ValoTheme.BUTTON_PRIMARY);
        validationAddOffer();

        addButton.addClickListener(event -> {
            if (binder.isValid() && supBinder.isValid()) {
                if (monthlyPayment.isEmpty()) {
                    checkButton().click();
                }

                checkClientInBank();

                createOffer();
                close();
            } else {
                Notification.show("Warning!", "Enter correct data.", Notification.Type.WARNING_MESSAGE)
                        .setStyleName(ValoTheme.NOTIFICATION_DARK);
            }
        });
        return addButton;
    }

    private void createOffer() {
        double bet = creditComboBox.getValue().getInterestRate().doubleValue() / 1200;
        int mp = Integer.parseInt(monthlyPayment.getValue());
        int creditSum = Integer.parseInt(creditAmount.getValue());
        Offer offer = new Offer(Integer.parseInt(creditAmount.getValue()), clientComboBox.getValue(), creditComboBox.getValue());
        offerDao.save(offer);
        int aRInterest;
        int aRBoby;
        int newCreditSum;
        if (supBinder.isValid()) {
            for (int i = 0; i < Integer.parseInt(creditTerm.getValue()); i++) {
                aRInterest = (int) (creditSum * bet); // расчет процентов, сумма для погашения процентов
                aRBoby = mp - aRInterest;
                paymentDao.save(new Payment(LocalDate.now().plusMonths(i), mp, aRBoby, aRInterest, offer));
                newCreditSum = creditSum - aRBoby;
                creditSum = newCreditSum;
            }
        } else {
            Notification.show("Warning!", "Enter correct data.", Notification.Type.WARNING_MESSAGE)
                    .setStyleName(ValoTheme.NOTIFICATION_DARK);
        }
        offerListDataProvider.getItems().add(offer);
        offerListDataProvider.refreshAll();
    }

    private void validationAddOffer() {
        binder = new BeanValidationBinder<>(Offer.class);
        try {
            binder.forField(creditAmount).withConverter(
                            new StringToIntegerConverter("Must enter a number"))
                    .withValidator(new BeanValidator(Offer.class, "creditAmount"))
                    .withValidator(x -> x <= creditComboBox.getValue().getLimit(),
                            "Limit is exceeded")
                    .bind("creditAmount");
        } catch (NoSuchElementException e) {
            Notification.show("Error!", "Item not found.", Notification.Type.ERROR_MESSAGE);
        }
        binder.bind(clientComboBox, "fkClient");
        binder.bind(creditComboBox, "fkCredit");
    }

    private void checkClientInBank() {
        if (clientComboBox.getValue().getFkBank().getIdBank() !=
                creditComboBox.getValue().getFkBank().getIdBank()) {
            if (clientDao.selectAll().stream().noneMatch(x -> x.getFkBank().getIdBank() == creditComboBox.getValue().getFkBank().getIdBank() &&
                    x.getPassport() == clientComboBox.getValue().getPassport())) {
                Client client = new Client(clientComboBox.getValue().getFio(), clientComboBox.getValue().getPhone(),
                        clientComboBox.getValue().getEmail(), clientComboBox.getValue().getPassport(), creditComboBox.getValue().getFkBank());
                clientDao.save(client);
                clientComboBox.setItems(client);
                clientComboBox.setSelectedItem(client);
            }
        }
    }

    private Button checkButton() {
        Button checkButton = new Button("Check sum credit");
        checkButton.setWidthFull();
        checkButton.addStyleName(ValoTheme.BUTTON_FRIENDLY);

        supBinder = new BeanValidationBinder<>(ValidBean.class);
        supBinder.forField(creditTerm).withConverter(
                        new StringToIntegerConverter("Must enter a number"))
                .withValidator(x -> 1 <= x && x <= 1000,
                        "Limit is exceeded").bind(ValidBean::getCreditTerm, ValidBean::setCreditTerm);

        checkButton.addClickListener(valueChangeEvent -> {
            if (binder.isValid() && supBinder.isValid()) {
                double bet = creditComboBox.getValue().getInterestRate().doubleValue() / 1200;
                double n = Double.parseDouble(creditTerm.getValue());
                double k = bet + bet / (Math.pow(1 + bet, n) - 1);
                double sum = k * Double.parseDouble(creditAmount.getValue());
                monthlyPayment.setValue(String.valueOf((int) sum));
                totalAmount.setValue(String.valueOf((int) (sum * n)));
            } else {
                Notification.show("Warning!", "Enter correct data.", Notification.Type.WARNING_MESSAGE)
                        .setStyleName(ValoTheme.NOTIFICATION_DARK);
            }
        });
        return checkButton;
    }

    private Button cancelButton() {
        Button cancelButton = new Button("Cancel");
        cancelButton.setWidthFull();
        cancelButton.addStyleNames(ValoTheme.BUTTON_DANGER);
        cancelButton.addClickListener(event -> close());
        return cancelButton;
    }
}
