package com.haulmont.testtask.userInterface.creditUI;

import com.haulmont.testtask.dao.AbstractDao;
import com.haulmont.testtask.dao.CreditDao;
import com.haulmont.testtask.dao.OfferDao;
import com.haulmont.testtask.entities.Credit;
import com.haulmont.testtask.entities.Offer;
import com.vaadin.data.provider.ListDataProvider;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;

/**
 * Главное окно для работы с кредитами
 */
public class CreditUI extends VerticalLayout {
    private AbstractDao<Credit> creditDao;
    private AbstractDao<Offer> offerDao = new OfferDao();
    private Grid<Credit> grid;
    private ListDataProvider<Credit> creditListDataProvider;

    public CreditUI(CreditDao clientDao) {
        this.creditDao = clientDao;
        setSizeFull();
        crudButtonLayout();
        addGrid();
        addDataToGrid();
    }

    private void crudButtonLayout() {
        HorizontalLayout crudLayout = new HorizontalLayout();
        crudLayout.addComponents(addButton(), updateButton(), deleteButton());
        crudLayout.setDefaultComponentAlignment(Alignment.TOP_LEFT);
        addComponent(crudLayout);
    }

    private void addGrid() {
        grid = new Grid<>(Credit.class);
        addComponent(grid);
        grid.setSizeFull();
        grid.setColumns("limit");
        grid.addColumn("interestRate").setCaption("InterestRate, %");
        grid.addColumn("fkBank").setCaption("Bank");
    }

    private void addDataToGrid() {
        creditListDataProvider = new ListDataProvider<>(creditDao.selectAll());
        grid.setDataProvider(creditListDataProvider);
    }

    private Button addButton() {
        Button button = new Button("Add");
        button.addClickListener(clickEvent -> getUI().addWindow(new CreditAddWindow(creditDao, creditListDataProvider)));
        button.addStyleName(ValoTheme.BUTTON_PRIMARY);
        return button;
    }

    private Button updateButton() {
        Button button = new Button("Update");
        button.addClickListener(clickEvent -> {
            Credit credit = null;
            for (Credit creditSelected : grid.getSelectedItems()) {
                credit = creditSelected;
            }
            if (credit != null) {
                getUI().addWindow(new CreditUpdateWindow(creditDao, creditListDataProvider, credit));
            } else {
                Notification.show("Warning!",
                        "Select credit for update!", Notification.Type.WARNING_MESSAGE);
            }
        });
        button.addStyleNames(ValoTheme.BUTTON_FRIENDLY);
        return button;
    }

    private Button deleteButton() {
        Button button = new Button("Delete");
        button.addClickListener(clickEvent -> {
            Credit credit = null;
            for (Credit creditSelected : grid.getSelectedItems()) {
                credit = creditSelected;
            }
            if (credit != null) {
                try {
                    Credit finalCredit = credit;
                    if (offerDao.selectAll().stream().noneMatch(x -> x.getFkCredit().getIdCredit() == finalCredit.getIdCredit())) {
                        creditDao.delete(credit);
                        creditListDataProvider.getItems().remove(credit);
                        creditListDataProvider.refreshAll();
                    } else Notification.show("Warning!", "Can't remove credit which relevant with offers!",
                            Notification.Type.WARNING_MESSAGE);
                } catch (Exception e) {
                    Notification.show("Error: " + e, Notification.Type.ERROR_MESSAGE);
                }
            } else Notification.show("Warning!",
                    "Select credit.", Notification.Type.WARNING_MESSAGE);
        });
        button.addStyleName(ValoTheme.BUTTON_DANGER);
        return button;
    }
}
