package com.haulmont.testtask.userInterface.paymentUI;

import com.haulmont.testtask.dao.PaymentDao;
import com.haulmont.testtask.entities.Offer;
import com.haulmont.testtask.entities.Payment;
import com.vaadin.data.provider.ListDataProvider;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
/**
 * Главное окно для создания графика платежей
 */
public class PaymentUI extends Window {
    private Grid<Payment> grid = new Grid<>(Payment.class);
    private PaymentDao paymentDao = new PaymentDao();
    private ListDataProvider<Payment> dataProvider;
    private Offer offer;

    public PaymentUI(Offer offer) {
        this.offer = offer;
        dataProvider = new ListDataProvider<>(paymentDao.selectAllForOffer(offer));
        setCaption("Payment");
        setWidthFull();
        setHeight("600px");
        setModal(true);
        setResizable(false);
        center();
        setContent(addGrid());
    }

    private HorizontalLayout crudButtonLayout() {
        HorizontalLayout crudLayout = new HorizontalLayout();
        crudLayout.addComponents(addButton(), updateButton(), deleteButton());
        crudLayout.setDefaultComponentAlignment(Alignment.TOP_LEFT);
        return crudLayout;
    }

    private VerticalLayout addGrid() {
        VerticalLayout layout = new VerticalLayout();
        layout.addComponent(crudButtonLayout());
        layout.addComponent(grid);
        grid.setDataProvider(dataProvider);
        grid.setSizeFull();
        grid.setColumns("paymentDate", "paymentAmount", "amountRepaymentBody", "amountRepaymentInterest");
        grid.addColumn("fkOffer").setCaption("Offer");
        return layout;
    }

    private Button addButton() {
        Button button = new Button("Add");
        button.addClickListener(clickEvent -> getUI().addWindow(new PaymentAddWindow(paymentDao, dataProvider, offer)));
        button.addStyleName(ValoTheme.BUTTON_PRIMARY);
        return button;
    }

    private Button updateButton() {
        Button button = new Button("Update");
        button.addClickListener(clickEvent -> {
            Payment payment = null;
            for (Payment clientSelected : grid.getSelectedItems()) {
                payment = clientSelected;
            }
            if (payment != null) {
                getUI().addWindow(new PaymentUpdateWindow(paymentDao, dataProvider, payment));
            } else {
                Notification.show("Warning!",
                        "Select payment for update!", Notification.Type.WARNING_MESSAGE);
            }
        });
        button.addStyleNames(ValoTheme.BUTTON_FRIENDLY);
        return button;
    }

    private Button deleteButton() {
        Button button = new Button("Delete");
        button.addClickListener(clickEvent -> {
            Payment payment = null;
            for (Payment clientSelected : grid.getSelectedItems()) {
                payment = clientSelected;
            }
            if (payment != null) {
                try {
                    paymentDao.delete(payment);
                    dataProvider.getItems().remove(payment);
                    dataProvider.refreshAll();

                } catch (Exception e) {
                    Notification.show("Error: " + e, Notification.Type.ERROR_MESSAGE);
                }
            } else Notification.show("Warning!",
                    "Select payment.", Notification.Type.WARNING_MESSAGE);
        });
        button.addStyleName(ValoTheme.BUTTON_DANGER);
        return button;
    }
}
