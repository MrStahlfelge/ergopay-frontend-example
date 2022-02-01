/*
 * #%L
 * GwtMaterial
 * %%
 * Copyright (C) 2015 - 2017 GwtMaterialDesign
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package org.ergoplatform.example.client.application.burning;

import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Widget;
import com.gwtplatform.mvp.client.ViewImpl;

import org.ergoplatform.example.client.application.QrCodeUtils;
import org.ergoplatform.example.client.application.UserSession;

import javax.inject.Inject;

import gwt.material.design.client.base.MaterialWidget;
import gwt.material.design.client.constants.Display;
import gwt.material.design.client.ui.MaterialLabel;
import gwt.material.design.client.ui.MaterialToast;
import gwt.material.design.client.ui.html.Div;

public class BurningView extends ViewImpl implements BurningPresenter.MyView {

    private boolean isAttached = false;

    interface Binder extends UiBinder<Widget, BurningView> {
    }

    @UiField
    Div connectQrcode;
    @UiField
    MaterialWidget layoutConnectWallet;
    @UiField
    MaterialWidget layoutWallet;
    @UiField
    MaterialWidget layoutStartBackend;
    @UiField
    MaterialLabel walletAddress;

    @Inject
    BurningView(Binder uiBinder) {
        initWidget(uiBinder.createAndBindUi(this));
    }

    @UiHandler("connectWalletButton")
    void connectWalletButtonClicked(ClickEvent e) {
        com.google.gwt.user.client.Window.open(UserSession.getConnectWalletErgoPayUrl(), "_blank", "");
        MaterialToast.fireToast("Wallet app opened (if installed)");
    }


    @Override
    protected void onAttach() {
        super.onAttach();
        isAttached = true;
        attemptFetchAddress();
        QrCodeUtils.setQrCodeToDiv(connectQrcode, UserSession.getConnectWalletErgoPayUrl());
    }

    @Override
    protected void onDetach() {
        isAttached = false;
        super.onDetach();
    }

    private void attemptFetchAddress() {
        if (isAttached) {
            UserSession.fetchAddressFromBackend(address -> {
                fetchAddressCallback(address);
                return null;
            });
        }
    }

    private void fetchAddressCallback(String address) {
        if (address != null && !address.isEmpty()) {
            onReceivedAddress(address);
        } else {
            // error or address not on backend

            // if no error, switch to connect display
            if (address != null) {
                // address not on backend
                layoutStartBackend.setDisplay(Display.NONE);
                layoutConnectWallet.setDisplay(Display.INITIAL);
            }

            // retry after 3 seconds
            Timer timer = new Timer() {
                @Override
                public void run() {
                    attemptFetchAddress();
                }
            };

            timer.schedule(3000);
        }
    }

    private void onReceivedAddress(String address) {
        layoutStartBackend.setDisplay(Display.NONE);
        layoutConnectWallet.setDisplay(Display.NONE);
        layoutWallet.setDisplay(Display.INITIAL);
        walletAddress.setText(address);
    }
}
