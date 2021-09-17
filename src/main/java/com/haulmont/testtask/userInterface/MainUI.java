package com.haulmont.testtask.userInterface;

import com.haulmont.testtask.dao.*;
import com.haulmont.testtask.userInterface.bankUI.BankUI;
import com.haulmont.testtask.userInterface.clientUI.ClientUI;
import com.haulmont.testtask.userInterface.creditUI.CreditUI;
import com.haulmont.testtask.userInterface.offerUI.OfferUI;
import com.vaadin.annotations.Theme;
import com.vaadin.server.Page;
import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;

import java.util.Locale;

/**
 * Главное окно запуска приложения
 */
@Theme(ValoTheme.THEME_NAME)
public class MainUI extends UI {
    private VerticalLayout mainLayout;
    private VerticalLayout tabMainLayout;
    ClientUI clientUI;
    BankUI bankUI;
    CreditUI creditUI;
    OfferUI offerUI;

    @Override
    protected void init(VaadinRequest request) {
        try {
            setupLayout();
            addTabSheet();
            addHeader();
            addMain();
            addFooter();
            Locale en = Locale.ENGLISH;
            getPage().getUI().setLocale(en);
        } catch (Exception e) {
            Notification.show("ERROR!", "Error app: " + e, Notification.Type.ERROR_MESSAGE);
        }
    }

    private void setupLayout() {
        getPage().setTitle("TAY app for Haulmont");
        mainLayout = new VerticalLayout();
        mainLayout.setMargin(true);
        setContent(mainLayout);
    }

    private void addTabSheet() {
        tabMainLayout = new VerticalLayout();
        tabMainLayout.setSizeFull();
        TabSheet tabsheet = new TabSheet();
        mainLayout.addComponent(tabsheet);
        mainLayout.setComponentAlignment(tabsheet, Alignment.BOTTOM_CENTER);
        clientUI = new ClientUI(new ClientDao());
        bankUI = new BankUI(new BankDao());
        creditUI = new CreditUI(new CreditDao());
        offerUI = new OfferUI(new OfferDao());

        tabsheet.addTab(tabMainLayout, "Home page");
        tabsheet.addTab(bankUI, "Banks");
        tabsheet.addTab(clientUI, "Clients");
        tabsheet.addTab(creditUI, "Credits");
        tabsheet.addTab(offerUI, "Offers");
        tabsheet.setSizeFull();
    }

    private void addHeader() {
        Label header = new Label("WEB APP FOR HAULMONT");
        header.addStyleName(ValoTheme.LABEL_H1);
        tabMainLayout.addComponent(header);
        tabMainLayout.setComponentAlignment(header, Alignment.TOP_CENTER);
    }

    private void addMain() {
        Label mainLabel = new Label("An application for a bank that allows you to work with clients by providing them with a loan offer.");
        mainLabel.addStyleName(ValoTheme.LABEL_HUGE);
        tabMainLayout.addComponent(mainLabel);
        tabMainLayout.setComponentAlignment(mainLabel, Alignment.MIDDLE_CENTER);
    }

    private void addFooter() {
        Label footer = new Label("© Andrey Tyshkun, 1999–2021.");
        footer.addStyleNames(ValoTheme.LABEL_COLORED);
        mainLayout.addComponent(footer);
        mainLayout.setComponentAlignment(footer, Alignment.BOTTOM_CENTER);
    }
}