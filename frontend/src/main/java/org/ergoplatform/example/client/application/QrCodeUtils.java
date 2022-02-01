package org.ergoplatform.example.client.application;

import org.realityforge.gwt.qr_code.Ecc;
import org.realityforge.gwt.qr_code.QrCode;
import org.realityforge.gwt.qr_code.QrCodeTool;

import gwt.material.design.client.ui.html.Div;

public class QrCodeUtils {
    public static void setQrCodeToDiv(Div div, String qrCodeContent) {
        if (qrCodeContent != null && !qrCodeContent.isEmpty()) {
            // Generate a simple text base url QR Code with High error correction
            final QrCode qrCode = QrCodeTool.encodeText(qrCodeContent, Ecc.HIGH);
            div.getElement().getStyle().setProperty("marginLeft", "auto");
            div.getElement().getStyle().setProperty("marginRight", "auto");
            div.getElement().setInnerHTML(qrCode.toSvgString(2));
        } else {
            div.getElement().setInnerHTML("");
        }
    }
}
