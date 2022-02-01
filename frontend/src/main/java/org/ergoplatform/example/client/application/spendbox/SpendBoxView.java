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
package org.ergoplatform.example.client.application.spendbox;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Widget;
import com.gwtplatform.mvp.client.ViewImpl;

import org.ergoplatform.example.client.application.QrCodeUtils;
import org.ergoplatform.example.client.gin.ClientModule;

import javax.inject.Inject;

import gwt.material.design.client.ui.MaterialTextBox;
import gwt.material.design.client.ui.MaterialToast;
import gwt.material.design.client.ui.html.Div;

public class SpendBoxView extends ViewImpl implements SpendBoxPresenter.MyView {

    interface Binder extends UiBinder<Widget, SpendBoxView> {
    }

    @UiField
    MaterialTextBox boxId;

    @UiField
    Div qrcode;

    @Inject
    SpendBoxView(Binder uiBinder) {
        initWidget(uiBinder.createAndBindUi(this));
    }

    @UiHandler("boxId")
    void onKeyEvent(KeyUpEvent e) {
        refreshQrCode();
    }

    @UiHandler("spendBoxButton")
    void mintTokenButtonClick(ClickEvent e) {
        String url = getErgoPayUrl();
        if (url != null) {
            com.google.gwt.user.client.Window.open(url, "_blank", "");
            MaterialToast.fireToast("Wallet app opened (if installed)");
        } else {
            MaterialToast.fireToast("Please enter a valid box id");
        }
    }

    private String getErgoPayUrl() {
        if (boxId.getValue().isEmpty())
            return null;
        else
            return ClientModule.ERGOPAY_URL + "spendBox/#P2PK_ADDRESS#/" + boxId.getValue();
    }

    private void refreshQrCode() {
        QrCodeUtils.setQrCodeToDiv(this.qrcode, getErgoPayUrl());
    }
}
