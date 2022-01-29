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
package org.ergoplatform.example.client.application;

import com.google.gwt.dom.client.Document;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.gwtplatform.mvp.client.ViewImpl;

import org.ergoplatform.example.client.gin.ClientModule;

import gwt.material.design.client.ui.MaterialContainer;

public class ApplicationView extends ViewImpl implements ApplicationPresenter.MyView {

    interface Binder extends UiBinder<Widget, ApplicationView> {
    }

    @UiField
    MaterialContainer container;

    @Inject
    ApplicationView(Binder uiBinder) {
        initWidget(uiBinder.createAndBindUi(this));
        bindSlot(ApplicationPresenter.SLOT_MAIN, container);

        if (ClientModule.BACKEND_URL.contains("heroku")) {
            doUselessBackendCall();
        }
    }

    public static void doUselessBackendCall() {
        // Heroku hibernates our backend regularly and needs longer to come up than wallet apps
        // time out. So we do a little trick here: we just wake the backend up with a useless call
        // when someone opens this page
        RequestBuilder builder = new RequestBuilder(RequestBuilder.GET,
                ClientModule.BACKEND_URL.replace("ergopay:", "https:"));

        try {
            builder.sendRequest(null, new RequestCallback() {
                public void onError(Request request, Throwable exception) {
                    // ignore
                }

                public void onResponseReceived(Request request, Response response) {
                    // ignore
                }
            });

        } catch (RequestException e) {
            // ignore
        }
    }

    @Override
    protected void onAttach() {
        super.onAttach();

        Document.get().getElementById("splashscreen").removeFromParent();
    }

}
