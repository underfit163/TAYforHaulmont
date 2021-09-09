package com.haulmont.testtask.userInterface.bankUI;

import com.haulmont.testtask.dao.AbstractDao;
import com.haulmont.testtask.dao.BankDao;
import com.haulmont.testtask.dao.ClientDao;
import com.haulmont.testtask.entities.Bank;
import com.haulmont.testtask.entities.Client;
import com.vaadin.data.provider.ListDataProvider;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;

/**
 * Главное окно для работы с банками
 */
public class BankUI extends VerticalLayout {
    private BankDao bankDao;
    private AbstractDao<Client> clientDao = new ClientDao();
    private Grid<Bank> grid;
    private ListDataProvider<Bank> bankListDataProvider;

    public BankUI(BankDao bankDao) {
        this.bankDao = bankDao;
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
        grid = new Grid<>(Bank.class);
        addComponent(grid);
        grid.setSizeFull();
        grid.setColumns("nameBank");
    }

    private void addDataToGrid() {
        bankListDataProvider = new ListDataProvider<>(bankDao.selectAll());
        grid.setDataProvider(bankListDataProvider);
    }

    private Button addButton() {
        Button button = new Button("Add");
        button.addClickListener(clickEvent -> getUI().addWindow(new BankAddWindow(bankDao, bankListDataProvider)));
        button.addStyleName(ValoTheme.BUTTON_PRIMARY);
        return button;
    }

    private Button updateButton() {
        Button button = new Button("Update");
        button.addClickListener(clickEvent -> {
            Bank bank = null;
            for (Bank bankSelected : grid.getSelectedItems()) {
                bank = bankSelected;
            }
            if (bank != null) {
                getUI().addWindow(new BankUpdateWindow(bankDao, bankListDataProvider, bank));
            } else {
                Notification.show("Warning!",
                        "Select bank for update!", Notification.Type.WARNING_MESSAGE);
            }
        });
        button.addStyleNames(ValoTheme.BUTTON_FRIENDLY);
        return button;
    }

    private Button deleteButton() {
        Button button = new Button("Delete");
        button.addClickListener(clickEvent -> {
            Bank bank = null;
            for (Bank bankSelected : grid.getSelectedItems()) {
                bank = bankSelected;
            }
            if (bank != null) {
                try {
                    Bank finalBank = bank;
                    if (clientDao.selectAll().stream().noneMatch(x -> x.getFkBank().getIdBank() == finalBank.getIdBank())) {
                        bankDao.delete(bank);
                        bankListDataProvider.getItems().remove(bank);
                        bankListDataProvider.refreshAll();
                    } else Notification.show("Warning!", "Can't remove bank which relevant with clients!",
                            Notification.Type.WARNING_MESSAGE);
                } catch (Exception e) {
                    Notification.show("Error: " + e, Notification.Type.ERROR_MESSAGE);
                }
            } else Notification.show("Warning!",
                    "Select bank.", Notification.Type.WARNING_MESSAGE);
        });
        button.addStyleName(ValoTheme.BUTTON_DANGER);
        return button;
    }
}
