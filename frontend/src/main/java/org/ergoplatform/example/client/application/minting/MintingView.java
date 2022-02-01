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
package org.ergoplatform.example.client.application.minting;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.safehtml.shared.UriUtils;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Widget;
import com.gwtplatform.mvp.client.ViewImpl;

import org.ergoplatform.example.client.gin.ClientModule;
import org.realityforge.gwt.qr_code.Ecc;
import org.realityforge.gwt.qr_code.QrCode;
import org.realityforge.gwt.qr_code.QrCodeTool;

import javax.inject.Inject;

import gwt.material.design.client.ui.MaterialButton;
import gwt.material.design.client.ui.MaterialIntegerBox;
import gwt.material.design.client.ui.MaterialLongBox;
import gwt.material.design.client.ui.MaterialTextBox;
import gwt.material.design.client.ui.MaterialToast;
import gwt.material.design.client.ui.html.Div;

public class MintingView extends ViewImpl implements MintingPresenter.MyView {
    interface Binder extends UiBinder<Widget, MintingView> {
    }

    @UiField
    MaterialButton mintTokenButton;

    @UiField
    MaterialTextBox tokenName;
    @UiField
    MaterialLongBox tokenNum;
    @UiField
    MaterialIntegerBox tokenDecimals;
    @UiField
    Div qrcode;

    @Inject
    MintingView(Binder uiBinder) {
        initWidget(uiBinder.createAndBindUi(this));
        tokenDecimals.setValue(0);
    }

    @UiHandler({"tokenName", "tokenNum", "tokenDecimals"})
    void onKeyEvent(KeyUpEvent e) {
        calcUrlAndSetButton();
    }

    @UiHandler("tokenDecimals")
    void onTokenDecimalsChanged(ValueChangeEvent<Integer> e) {
        calcUrlAndSetButton();
    }

    @UiHandler("tokenNum")
    void onTokenNumChanged(ValueChangeEvent<Long> e) {
        calcUrlAndSetButton();
    }

    @UiHandler("mintTokenButton")
    void mintTokenButtonClick(ClickEvent e) {
        com.google.gwt.user.client.Window.open(getErgoPayUrl(), "_blank", "");
        MaterialToast.fireToast("Wallet app opened (if installed)");
    }

    private String getErgoPayUrl() {
        return ClientModule.ERGOPAY_URL + "mintToken/#P2PK_ADDRESS#/?num=" + tokenNum.getValue()
                + "&dec=" + tokenDecimals.getValue()
                + "&name=" + UriUtils.encode(tokenName.getValue());
    }

    private void calcUrlAndSetButton() {
        boolean tokenNameOk = !tokenName.getValue().isEmpty() && tokenName.getValue().length() < 1000;
        boolean tokenNumOk = tokenNum.getValue() > 0;
        boolean tokenDecOk = tokenDecimals.getValue() >= 0;
        boolean allInputsValid = tokenNameOk && tokenNumOk && tokenDecOk;
        mintTokenButton.setEnabled(allInputsValid);
        if (!tokenDecOk)
            tokenDecimals.setErrorText("Needs to be >= 0");
        else
            tokenDecimals.clearErrorText();
        if (!tokenNumOk)
            tokenNum.setErrorText("Needs to be >= 1");
        else
            tokenNum.clearErrorText();

        if (allInputsValid) {
            // Generate a simple text base url QR Code with High error correction
            final QrCode qrCode = QrCodeTool.encodeText(getErgoPayUrl(), Ecc.HIGH);
            this.qrcode.getElement().getStyle().setProperty("marginLeft", "auto");
            this.qrcode.getElement().getStyle().setProperty("marginRight", "auto");
            this.qrcode.getElement().setInnerHTML(qrCode.toSvgString(2));
        } else {
            this.qrcode.$this().html("");
        }
    }
}
