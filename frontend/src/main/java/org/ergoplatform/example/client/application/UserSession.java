package org.ergoplatform.example.client.application;

import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.storage.client.Storage;

import org.ergoplatform.example.client.gin.ClientModule;

import java.util.function.Function;

public class UserSession {
    public static final Storage LOCAL_STORAGE = Storage.getLocalStorageIfSupported();
    private static final String KEY_SESSION_ID = "sessionId";

    public static String getSessionId() {
        String sessionId = LOCAL_STORAGE != null ? LOCAL_STORAGE.getItem(KEY_SESSION_ID) : null;

        if (sessionId == null || sessionId.isEmpty()) {
            sessionId = uuid();
            if (LOCAL_STORAGE != null)
                LOCAL_STORAGE.setItem(KEY_SESSION_ID, sessionId);
        }

        return sessionId;
    }

    public static void fetchAddressFromBackend(Function<String, Void> callback) {

        RequestBuilder builder = new RequestBuilder(RequestBuilder.GET,
                ClientModule.BACKEND_URL + "getUserAddress/" + getSessionId());

        try {
            builder.sendRequest(null, new RequestCallback() {
                public void onError(Request request, Throwable exception) {
                    callback.apply(null);
                }

                public void onResponseReceived(Request request, Response response) {
                    callback.apply(response.getStatusCode() > 0 && response.getStatusCode() < 400 ? response.getText() : null);
                }
            });

        } catch (RequestException e) {
            callback.apply(null);
        }
    }

    public native static String uuid() /*-{
    return 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g,
            function(c) {
                var r = Math.random() * 16 | 0, v = c == 'x' ? r
                        : (r & 0x3 | 0x8);
                return v.toString(16);
            });
    }-*/;

    public static String getConnectWalletErgoPayUrl() {
        return ClientModule.ERGOPAY_URL + "setAddress/" + getSessionId() + "/#P2PK_ADDRESS#";
    }
}
