/*
 * Copyright 2017 - 2019 EasyFXML project and contributors
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package moe.tristan.easyfxml.samples.form.user.view.userform.fields.email;

import static java.util.concurrent.CompletableFuture.runAsync;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

import org.springframework.stereotype.Component;

import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import moe.tristan.easyfxml.fxkit.form.sample.StringFormFieldController;

@Component
public class EmailController extends StringFormFieldController {

    public TextField emailField;
    public Label errorLabel;

    @Override
    public void validate(String fieldValue) {
        runAsync(() -> {
            try {
                InternetAddress address = new InternetAddress(fieldValue);
                address.validate();
                Platform.runLater(this::onValid);
            } catch (AddressException e) {
                Platform.runLater(() -> onInvalid(e.getMessage()));
            }
        });
    }

    @Override
    public void onValid() {
        errorLabel.setVisible(false);
    }

    @Override
    public void onInvalid(String reason) {
        errorLabel.setText(reason);
        errorLabel.setVisible(true);
    }

    @Override
    public ObservableValue<String> getObservableValue() {
        return emailField.textProperty();
    }

    @Override
    public String getFieldName() {
        return EmailComponent.EMAIL_FIELD_NAME;
    }

}
