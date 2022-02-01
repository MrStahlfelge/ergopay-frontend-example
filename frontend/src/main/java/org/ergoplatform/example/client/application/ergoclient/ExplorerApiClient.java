package org.ergoplatform.example.client.application.ergoclient;

import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;

public class ExplorerApiClient {
    public final static String MAINNET_URL = "https://api.ergoplatform.com";
    public final static String TESTNET_URL = "https://api-testnet.ergoplatform.com";

    public static void fetchTokensFromExplorer(String address, Function<List<ErgoToken>, Void> callback) {

        RequestBuilder builder = new RequestBuilder(RequestBuilder.GET,
                getExplorerUrl(address) + "/api/v1/addresses/{p1}/balance/confirmed".replace("{p1}", address));

        try {
            builder.sendRequest(null, new RequestCallback() {
                public void onError(Request request, Throwable exception) {
                    callback.apply(null);
                }

                public void onResponseReceived(Request request, Response response) {
                    callback.apply(response.getStatusCode() > 0 && response.getStatusCode() < 400 ?
                            parseBalanceResponse(response.getText()) : null);
                }
            });

        } catch (RequestException e) {
            callback.apply(null);
        }
    }

    private static List<ErgoToken> parseBalanceResponse(String text) {
        try {
            JSONObject jsonObject = JSONParser.parseStrict(text).isObject();
            JSONArray tokens = jsonObject.get("tokens").isArray();

            List<ErgoToken> retVal = new LinkedList<>();

            for (int i = 0; i < tokens.size(); i++) {
                ErgoToken token = new ErgoToken();
                JSONObject jsonToken = tokens.get(i).isObject();
                token.amount = jsonToken.get("amount").isNumber().doubleValue();
                token.id = jsonToken.get("tokenId").isString().stringValue();
                token.name = jsonToken.get("name").isString().stringValue();

                retVal.add(token);
            }

            return retVal;
        } catch (Throwable t) {
            return null;
        }

    }

    private static String getExplorerUrl(String address) {
        return address.startsWith("9") ? MAINNET_URL : TESTNET_URL;
    }

    public static class ErgoToken {
        public String id;
        public String name;
        public double amount;
    }
}
