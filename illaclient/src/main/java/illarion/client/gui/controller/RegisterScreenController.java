/*
 * This file is part of the Illarion project.
 *
 * Copyright © 2016 - Illarion e.V.
 *
 * Illarion is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Illarion is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 */
package illarion.client.gui.controller;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.NiftyEventSubscriber;
import de.lessvoid.nifty.controls.*;
import de.lessvoid.nifty.elements.Element;
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.nifty.screen.ScreenController;
import illarion.client.IllaClient;
import illarion.client.util.account.AccountSystem;
import illarion.client.util.account.AccountSystemEndpoint;
import illarion.client.util.account.Credentials;
import illarion.client.util.account.response.*;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Objects;

/**
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public final class RegisterScreenController implements ScreenController {
    /**
     * The reference to the account system.
     */
    @Nonnull
    private final AccountSystem accountSystem;

    @Nullable
    private Nifty nifty;

    @Nullable
    private Screen screen;

    @Nullable
    private DropDown<String> dropDownServer;

    @Nullable
    private TextField textFieldAccount;

    @Nullable
    private Label labelAccountUsed;

    @Nullable
    private TextField textFieldEMail;

    @Nullable
    private Label labelEMailUsed;

    @Nullable
    private TextField textFieldPassword;

    @Nullable
    private Label labelPasswordQuality;

    @Nullable
    private Element popupCurrentAction;

    @Nullable
    private Element popupError;

    public RegisterScreenController(@Nonnull AccountSystem accountSystem) {
        this.accountSystem = accountSystem;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void bind(@Nonnull Nifty nifty, @Nonnull Screen screen) {
        this.nifty = nifty;
        this.screen = screen;

        dropDownServer = screen.findNiftyControl("server", DropDown.class);
        textFieldAccount = screen.findNiftyControl("accountName", TextField.class);
        textFieldEMail = screen.findNiftyControl("eMail", TextField.class);
        textFieldPassword = screen.findNiftyControl("password", TextField.class);
        labelAccountUsed = screen.findNiftyControl("accountAlreadyUsed", Label.class);
        labelEMailUsed = screen.findNiftyControl("eMailAlreadyUsed", Label.class);
        labelPasswordQuality = screen.findNiftyControl("passwordQuality", Label.class);

        popupCurrentAction = nifty.createPopup(screen, "currentActionPopup");
        popupError = nifty.createPopup(screen, "errorPopup");
    }

    @Override
    public void onStartScreen() {
        assert nifty != null;

        populateEndpointList();
        nifty.subscribeAnnotations(this);
    }

    @Override
    public void onEndScreen() {
        assert nifty != null;

        nifty.unsubscribeAnnotations(this);
    }

    @NiftyEventSubscriber(id = "registerBtn")
    public void onRegisterButtonClicked(String topic, ButtonClickedEvent event) {
        assert nifty != null;
        assert textFieldAccount != null;
        assert textFieldEMail != null;
        assert textFieldPassword != null;

        @Nullable String account = textFieldAccount.getRealText();
        @Nullable String mail = textFieldEMail.getRealText();
        @Nullable String password = textFieldPassword.getRealText();
        if (mail.isEmpty()) {
            mail = null;
        }

        showStatus("${register-bundle.creatingAccount}");

        ListenableFuture<AccountCreateResponse> lFuture = accountSystem.createAccount(account, mail, password);
        Futures.addCallback(lFuture, new AccountCreateResponseFutureCallback(account, password));
    }

    @NiftyEventSubscriber(id = "backBtn")
    public void onBackButtonClicked(String topic, ButtonClickedEvent event) {
        assert nifty != null;

        nifty.gotoScreen("login");
    }

    @NiftyEventSubscriber(id = "accountName")
    public void onAccountTextFieldChanged(String topic, TextFieldChangedEvent event) {
        performCredentialsCheck();
    }

    @NiftyEventSubscriber(id = "eMail")
    public void onEMailTextFieldChanged(String topic, TextFieldChangedEvent event) {
        performCredentialsCheck();
    }

    @NiftyEventSubscriber(id = "password")
    public void onPasswordTextFieldChanged(String topic, TextFieldChangedEvent event) {
        performCredentialsCheck();
    }

    @NiftyEventSubscriber(id = "server")
    public void onServerChanged(@Nonnull String topic, @Nonnull DropDownSelectionChangedEvent<String> data) {
        AccountSystemEndpoint endpoint = getCurrentSelectedEndpoint();
        if (endpoint != null) {
            accountSystem.setEndpoint(endpoint);
        }
    }

    @Nullable
    private AccountSystemEndpoint getCurrentSelectedEndpoint() {
        assert dropDownServer != null;

        List<AccountSystemEndpoint> endpoints = accountSystem.getEndPoints();
        int selectedIndex = dropDownServer.getSelectedIndex();
        if ((selectedIndex >= 0) && (selectedIndex < endpoints.size())) {
            return endpoints.get(selectedIndex);
        }
        return null;
    }

    private void performCredentialsCheck() {
        assert textFieldAccount != null;
        assert textFieldEMail != null;
        assert textFieldPassword != null;
        assert labelPasswordQuality != null;
        assert labelPasswordQuality.getElement() != null;

        @Nullable String account = textFieldAccount.getRealText();
        @Nullable String mail = textFieldEMail.getRealText();
        @Nullable String password = textFieldPassword.getRealText();
        if (mail.isEmpty()) {
            mail = null;
        }

        ListenableFuture<AccountCheckResponse> checkFuture = accountSystem.performAccountCredentialsCheck(account, mail);
        Futures.addCallback(checkFuture, new AccountCheckResponseFutureCallback(account, mail));

        boolean showPasswordMsg = false;
        if (password.length() < 5) {
            labelPasswordQuality.setText("${register-bundle.shortPassword}");
            showPasswordMsg = true;
        } else if (password.toUpperCase().contains(account.toUpperCase()) &&
                   ((password.length() - account.length()) < 3)) {
            labelPasswordQuality.setText("${register-bundle.passwordAccount}");
            showPasswordMsg = true;
        }

        labelPasswordQuality.getElement().setVisible(showPasswordMsg);
    }

    private void populateEndpointList() {
        assert screen != null;
        assert dropDownServer != null;

        List<AccountSystemEndpoint> endpointList = accountSystem.getEndPoints();

        if (endpointList.size() == 1) {
            Element serverPanel = screen.findElementById("serverPanel");
            assert serverPanel != null: "Failed to locate the server planel on the login screen.";
            serverPanel.hide();
        }

        dropDownServer.clear();
        for (AccountSystemEndpoint endpoint : endpointList) {
            dropDownServer.addItem(endpoint.getName());
        }

        dropDownServer.selectItemByIndex(0);

        Element ddServerElement = dropDownServer.getElement();
        if (ddServerElement != null) {
            if (endpointList.size() == 1) {
                ddServerElement.setVisible(false);
            } else {
                ddServerElement.setVisible(true);
            }
        }
    }

    private void showStatus(@Nonnull String message) {
        assert screen != null;
        assert popupCurrentAction != null;

        Label popupMessage = popupCurrentAction.findNiftyControl("#message", Label.class);
        assert popupMessage != null: "The control for the message of the popup was not found.";

        popupMessage.setText(message);
        if (!screen.isActivePopup(popupCurrentAction)) {
            screen.addPopup(popupCurrentAction, null);
        }
    }

    private void hideStatus() {
        assert screen != null;
        assert popupCurrentAction != null;

        if (screen.isActivePopup(popupCurrentAction)) {
            screen.closePopup(popupCurrentAction, null);
        }
    }

    private void showError(@Nonnull String message) {
        assert nifty != null;
        assert screen != null;
        assert popupError != null;
        assert popupError.getId() != null;

        Label errorText = popupError.findNiftyControl("#errorText", Label.class);
        assert errorText != null;

        errorText.setText(message);
        if (!screen.isActivePopup(popupError)) {
            nifty.showPopup(screen, popupError.getId(), popupError.findElementById("#closeButton"));
        }
    }

    private final class AccountCheckResponseFutureCallback implements FutureCallback<AccountCheckResponse> {
        @Nullable
        private final String account;
        @Nullable
        private final String eMail;

        private AccountCheckResponseFutureCallback(@Nullable String account, @Nullable String eMail) {
            this.account = account;
            this.eMail = eMail;
        }

        @Override
        public void onSuccess(@Nullable AccountCheckResponse result) {
            assert textFieldAccount != null;
            assert textFieldEMail != null;

            @Nullable String account = textFieldAccount.getRealText();
            @Nullable String mail = textFieldEMail.getRealText();
            if (mail.isEmpty()) {
                mail = null;
            }

            if (!Objects.equals(account, this.account) || !Objects.equals(eMail, mail)) {
                return;
            }

            @Nullable String nameCheckError = null;
            @Nullable String eMailCheckError = null;

            if (result != null) {
                List<CheckResponse> checks = result.getChecks();
                for (CheckResponse check : checks) {
                    if ("name".equals(check.getCheckedType())) {
                        nameCheckError = check.isSuccess() ? null : check.getDescription();
                    }
                    if ("email".equals(check.getCheckedType())) {
                        eMailCheckError = check.isSuccess() ? null : check.getDescription();
                    }
                }
            }

            publishValues(nameCheckError, eMailCheckError);
        }

        private void publishValues(@Nullable String nameMessage, @Nullable String eMailMessage) {
            assert labelAccountUsed != null;
            assert labelEMailUsed != null;
            assert labelAccountUsed.getElement() != null;
            assert labelEMailUsed.getElement() != null;

            if (nifty != null) {
                nifty.scheduleEndOfFrameElementAction(() -> {
                    if (nameMessage == null) {
                        labelAccountUsed.getElement().setVisible(false);
                    } else {
                        labelAccountUsed.setText(nameMessage);
                        labelAccountUsed.getElement().setVisible(true);
                    }

                    if (eMailMessage == null) {
                        labelEMailUsed.getElement().setVisible(false);
                    } else {
                        labelEMailUsed.setText(eMailMessage);
                        labelEMailUsed.getElement().setVisible(true);
                    }
                }, null);
            }
        }

        @Override
        public void onFailure(@Nonnull Throwable t) {
        }
    }

    private final class AccountCreateResponseFutureCallback implements FutureCallback<AccountCreateResponse> {
        @Nonnull
        private final String account;
        @Nonnull
        private final String password;

        public AccountCreateResponseFutureCallback(@Nonnull String account, @Nonnull String password) {
            this.account = account;
            this.password = password;
        }

        @Override
        public void onSuccess(@Nullable AccountCreateResponse result) {
            assert nifty != null;
            if (result != null) {
                ErrorResponse response = result.getError();
                if (response != null) {
                    nifty.scheduleEndOfFrameElementAction(() -> {
                        hideStatus();
                        String message = response.getMessage();
                        if (message != null) {
                            showError(message);
                        }
                    }, null);
                }
            }

            nifty.scheduleEndOfFrameElementAction(() -> showStatus("${register-bundle.createdAccount}"), null);

            /* Account was created. Lets continue to the character selection. */
            AccountSystemEndpoint endpoint = getCurrentSelectedEndpoint();
            if (endpoint == null) {
                return;
            }

            Credentials credentials = new Credentials(endpoint, IllaClient.getCfg());
            credentials.setUserName(account);
            credentials.setPassword(password);
            credentials.setStorePassword(false);
            accountSystem.setAuthentication(credentials);

            ListenableFuture<AccountGetResponse> response = accountSystem.getAccountInformation();
            Futures.addCallback(response, new AccountGetResponseFutureCallback(credentials), new NiftyExecutor(nifty));
        }

        @Override
        public void onFailure(@Nonnull Throwable t) {
            assert nifty != null;

            nifty.scheduleEndOfFrameElementAction(() -> {
                hideStatus();
                String msg = t.getLocalizedMessage();
                if (msg != null) {
                    showError(msg);
                }
            }, null);
        }
    }

    private final class AccountGetResponseFutureCallback implements FutureCallback<AccountGetResponse> {
        @Nonnull
        private final Credentials credentials;

        public AccountGetResponseFutureCallback(@Nonnull Credentials credentials) {
            this.credentials = credentials;
        }

        @Override
        public void onSuccess(@Nullable AccountGetResponse result) {
            assert nifty != null;
            hideStatus();

            if (result == null) {
                return;
            }

            @Nullable Screen charSelectScreen = nifty.getScreen("charSelect");
            if (charSelectScreen == null) {
                throw new IllegalStateException("The character select screen was not found! This is bad.");
            }
            @Nonnull ScreenController charScreenController = charSelectScreen.getScreenController();
            if (charScreenController instanceof CharScreenController) {
                ((CharScreenController) charScreenController).applyAccountData(credentials, result);
            }
            nifty.gotoScreen(charSelectScreen.getScreenId());
        }

        @Override
        public void onFailure(@Nonnull Throwable t) {
            hideStatus();
            String msg = t.getLocalizedMessage();
            if (msg != null) {
                showError(msg);
            }
        }
    }
}
