package com.haulmont.testtask.userInterface.clientUI;

import com.haulmont.testtask.dao.AbstractDao;
import com.haulmont.testtask.dao.ClientDao;
import com.haulmont.testtask.dao.OfferDao;
import com.haulmont.testtask.entities.Client;
import com.haulmont.testtask.entities.Offer;
import com.vaadin.data.provider.ListDataProvider;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
/**
 * Главное окно для работы с клиентами
 */
public class ClientUI extends VerticalLayout {
    private AbstractDao<Client> clientDao;
    private AbstractDao<Offer> offerDao = new OfferDao();
    private Grid<Client> grid;
    private ListDataProvider<Client> clientListDataProvider;

    public ClientUI(ClientDao clientDao) {
        this.clientDao = clientDao;
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
        grid = new Grid<>(Client.class);
        addComponent(grid);
        grid.setSizeFull();
        grid.setColumns("fio", "email", "phone", "passport");
        grid.addColumn("fkBank").setCaption("Bank");
    }

    private void addDataToGrid() {
        clientListDataProvider = new ListDataProvider<>(clientDao.selectAll());
        grid.setDataProvider(clientListDataProvider);
    }

    private Button addButton() {
        Button button = new Button("Add");
        button.addClickListener(clickEvent -> getUI().addWindow(new ClientAddWindow(clientDao, clientListDataProvider)));
        button.addStyleName(ValoTheme.BUTTON_PRIMARY);
        return button;
    }

    private Button updateButton() {
        Button button = new Button("Update");
        button.addClickListener(clickEvent -> {
            Client client = null;
            for (Client clientSelected : grid.getSelectedItems()) {
                client = clientSelected;
            }
            if (client != null) {
                getUI().addWindow(new ClientUpdateWindow(clientDao, clientListDataProvider, client));
            } else {
                Notification.show("Warning!",
                        "Select client for update!", Notification.Type.WARNING_MESSAGE)
                        .setStyleName(ValoTheme.NOTIFICATION_DARK);
            }
        });
        button.addStyleNames(ValoTheme.BUTTON_FRIENDLY);
        return button;
    }

    private Button deleteButton() {
        Button button = new Button("Delete");
        button.addClickListener(clickEvent -> {
            Client client = null;
            for (Client clientSelected : grid.getSelectedItems()) {
                client = clientSelected;
            }
            if (client != null) {
                try {
                    Client finalClient = client;
                    if (offerDao.selectAll().stream().noneMatch(x -> x.getFkClient().getIdClient()
                            == finalClient.getIdClient())) {
                        clientDao.delete(client);
                        clientListDataProvider.getItems().remove(client);
                        clientListDataProvider.refreshAll();
                    } else Notification.show("Warning!", "Can't remove client which relevant with offers!",
                            Notification.Type.WARNING_MESSAGE).setStyleName(ValoTheme.NOTIFICATION_DARK);
                } catch (Exception e) {
                    Notification.show("Error: " + e, Notification.Type.ERROR_MESSAGE);
                }
            } else Notification.show("Warning!",
                    "Select client.", Notification.Type.WARNING_MESSAGE)
                    .setStyleName(ValoTheme.NOTIFICATION_DARK);
        });
        button.addStyleName(ValoTheme.BUTTON_DANGER);
        return button;
    }
}
