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

package moe.tristan.easyfxml.samples.form.user.view;

import org.springframework.stereotype.Component;

import moe.tristan.easyfxml.FxUiManager;
import moe.tristan.easyfxml.api.FxmlNode;
import moe.tristan.easyfxml.samples.form.user.view.userform.UserFormComponent;

@Component
public class UserFormUiManager extends FxUiManager {

    private final UserFormComponent userFormComponent;

    public UserFormUiManager(UserFormComponent userFormComponent) {
        this.userFormComponent = userFormComponent;
    }

    @Override
    protected String title() {
        return "User sign up form";
    }

    @Override
    protected FxmlNode mainComponent() {
        return userFormComponent;
    }

}
