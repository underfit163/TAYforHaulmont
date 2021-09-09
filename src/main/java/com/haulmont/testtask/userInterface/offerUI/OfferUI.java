package com.haulmont.testtask.userInterface.offerUI;

import com.haulmont.testtask.dao.AbstractDao;
import com.haulmont.testtask.dao.OfferDao;
import com.haulmont.testtask.entities.Offer;
import com.haulmont.testtask.userInterface.paymentUI.PaymentUI;
import com.vaadin.data.provider.ListDataProvider;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;

/**
 * Главное окно для оформления кредита
 */
public class OfferUI extends VerticalLayout {
    private AbstractDao<Offer> offerDao;
    private Grid<Offer> grid;
    private ListDataProvider<Offer> offerListDataProvider;

    public OfferUI(OfferDao offerDao) {
        this.offerDao = offerDao;
        setSizeFull();
        crudButtonLayout();
        addGrid();
        addDataToGrid();
    }

    private void crudButtonLayout() {
        HorizontalLayout crudLayout = new HorizontalLayout();
        crudLayout.addComponents(addButton(), updateButton(), deleteButton(), openPaymentWindow());
        crudLayout.setDefaultComponentAlignment(Alignment.TOP_LEFT);
        addComponent(crudLayout);
    }

    private void addGrid() {
        grid = new Grid<>(Offer.class);
        addComponent(grid);
        grid.setSizeFull();
        grid.setColumns("creditAmount");
        grid.addColumn("fkClient").setCaption("Client");
        grid.addColumn("fkCredit").setCaption("Credit");
    }

    private void addDataToGrid() {
        offerListDataProvider = new ListDataProvider<>(offerDao.selectAll());
        grid.setDataProvider(offerListDataProvider);
    }

    private Button addButton() {
        Button button = new Button("Credit Offer");
        button.addClickListener(clickEvent -> getUI().addWindow(new OfferAddWindow(offerDao, offerListDataProvider)));
        button.addStyleName(ValoTheme.BUTTON_PRIMARY);
        return button;
    }

    private Button updateButton() {
        Button button = new Button("Update");
        button.addClickListener(clickEvent -> {
            Offer offer = null;
            for (Offer offerSelected : grid.getSelectedItems()) {
                offer = offerSelected;
            }
            if (offer != null) {
                getUI().addWindow(new OfferUpdateWindow(offerDao, offerListDataProvider, offer));
            } else {
                Notification.show("Warning!",
                        "Select offer for update!", Notification.Type.WARNING_MESSAGE);
            }
        });
        button.addStyleNames(ValoTheme.BUTTON_FRIENDLY);
        return button;
    }

    private Button deleteButton() {
        Button button = new Button("Delete");
        button.addClickListener(clickEvent -> {
            Offer offer = null;
            for (Offer creditSelected : grid.getSelectedItems()) {
                offer = creditSelected;
            }
            if (offer != null) {
                try {
                    offerDao.delete(offer);
                    offerListDataProvider.getItems().remove(offer);
                    offerListDataProvider.refreshAll();
                } catch (Exception e) {
                    Notification.show("Error: " + e, Notification.Type.ERROR_MESSAGE);
                }
            } else Notification.show("Warning!",
                    "Select offer.", Notification.Type.WARNING_MESSAGE);
        });
        button.addStyleName(ValoTheme.BUTTON_DANGER);
        return button;
    }

    private Button openPaymentWindow() {
        Button button = new Button("Open Payment");
        button.addStyleName(ValoTheme.BUTTON_PRIMARY);
        button.addClickListener(clickEvent -> {
            Offer offer = null;
            for (Offer creditSelected : grid.getSelectedItems()) {
                offer = creditSelected;
            }
            if (offer != null) {
                getUI().addWindow(new PaymentUI(offer));
            } else Notification.show("Warning!",
                    "Select offer.", Notification.Type.WARNING_MESSAGE);
        });
        return button;
    }
}
