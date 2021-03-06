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

package moe.tristan.easyfxml.model.fxml;

import static moe.tristan.easyfxml.TestUtils.isSpringSingleton;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Supplier;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.testfx.framework.junit.ApplicationTest;

import javafx.fxml.LoadException;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import moe.tristan.easyfxml.EasyFxmlAutoConfiguration;
import moe.tristan.easyfxml.api.FxmlController;
import moe.tristan.easyfxml.api.FxmlFile;
import moe.tristan.easyfxml.api.FxmlNode;
import moe.tristan.easyfxml.model.beanmanagement.ControllerManager;
import moe.tristan.easyfxml.model.beanmanagement.Selector;
import moe.tristan.easyfxml.util.Stages;

import io.vavr.control.Option;
import io.vavr.control.Try;

@ContextConfiguration(classes = EasyFxmlAutoConfiguration.class)
@RunWith(SpringRunner.class)
public class DefaultEasyFxmlTest extends ApplicationTest {

    private static final Object SELECTOR = new Object();

    @Autowired
    private ApplicationContext context;
    @Autowired
    private DefaultEasyFxml easyFxml;
    @Autowired
    private ControllerManager controllerManager;

    @SuppressWarnings("EmptyMethod")
    @Override
    public void start(final Stage stage) {
        // initializes JavaFX Platform
    }

    @Test
    public void loadAsPaneSingle() throws InterruptedException, ExecutionException, TimeoutException {
        final Pane testPane = this.assertSuccessAndGet(this.easyFxml.loadNode(TEST_NODES.PANE).getNode());

        assertThat(testPane.getChildren()).hasSize(1);
        assertThat(testPane.getChildren().get(0).getClass()).isEqualTo(Button.class);

        this.assertControllerBoundToTestPane(
            testPane,
            this.controllerManager.getSingle(TEST_NODES.PANE)
        );
    }

    @Test
    public void loadAsPaneMultiple() throws InterruptedException, ExecutionException, TimeoutException {
        final Pane testPane = this.assertSuccessAndGet(this.easyFxml.loadNode(TEST_NODES.PANE, new Selector(SELECTOR))
                                                                    .getNode());

        this.assertControllerBoundToTestPane(
            testPane,
            this.controllerManager.getMultiple(TEST_NODES.PANE, new Selector(SELECTOR))
        );

        assertThat(testPane.getChildren()).hasSize(1);
        assertThat(testPane.getChildren().get(0).getClass()).isEqualTo(Button.class);

    }

    @Test
    public void loadWithTypeSuccess() throws InterruptedException, ExecutionException, TimeoutException {
        final Pane testPane = this.assertSuccessAndGet(this.easyFxml.loadNode(
            TEST_NODES.PANE,
            Pane.class,
            FxmlController.class
        ).getNode());

        this.assertControllerBoundToTestPane(
            testPane,
            this.controllerManager.getSingle(TEST_NODES.PANE)
        );
    }

    @Test
    public void loadWithTypeSingleInvalidClassFailure() {
        this.assertPaneFailedLoadingAndDidNotRegister(
            () -> this.easyFxml.loadNode(TEST_NODES.BUTTON, Pane.class, FxmlController.class).getNode(),
            this.controllerManager.getSingle(TEST_NODES.BUTTON),
            ClassCastException.class
        );
    }

    @Test
    public void loadWithTypeSingleInvalidFileFailure() {
        this.assertPaneFailedLoadingAndDidNotRegister(
            () -> this.easyFxml.loadNode(TEST_NODES.INVALID).getNode(),
            this.controllerManager.getSingle(TEST_NODES.INVALID),
            LoadException.class
        );
    }

    @Test
    public void loadWithTypeMultipleInvalidClassFailure() {
        this.assertPaneFailedLoadingAndDidNotRegister(
            () -> this.easyFxml.loadNode(TEST_NODES.BUTTON, Pane.class, NoControllerClass.class, new Selector(SELECTOR)).getNode(),
            this.controllerManager.getMultiple(TEST_NODES.BUTTON, new Selector(SELECTOR)),
            ClassCastException.class
        );
    }

    @Test
    public void loadWithTypeMultipleInvalidFileFailure() {
        this.assertPaneFailedLoadingAndDidNotRegister(
            () -> this.easyFxml.loadNode(TEST_NODES.INVALID, new Selector(SELECTOR)).getNode(),
            this.controllerManager.getMultiple(TEST_NODES.INVALID, new Selector(SELECTOR)),
            LoadException.class
        );
    }

    @Test
    public void canInstantiateControllerAsPrototype() {
        assertThat(isSpringSingleton(this.context, SAMPLE_CONTROL_CLASS.class)).isFalse();
    }

    private <T extends Node> T assertSuccessAndGet(final Try<T> loadResult) {
        assertThat(loadResult.isSuccess()).isTrue();
        return loadResult.get();
    }

    /**
     * We have to sleep here because the event firing in JavaFX can't be waited on all the way. So if we don't wait, as
     * soon as the click is actually sent, but not yet registered, we are already asserting. The wait is a horrific
     * thing that the whole async life promised to save us from. But it did not deliver (yet).
     *
     * @param testPane         The pane to test bounding on
     * @param controllerLookup The controller as an {@link Option} so we can know if the test actually failed because of
     *                         some outside reason.
     */
    private void assertControllerBoundToTestPane(final Pane testPane, final Option<FxmlController> controllerLookup)
    throws InterruptedException, ExecutionException, TimeoutException {
        assertThat(controllerLookup.isDefined()).isTrue();
        assertThat(controllerLookup.get().getClass()).isEqualTo(SAMPLE_CONTROL_CLASS.class);

        Stages.stageOf("TEST_PANE", testPane)
              .whenCompleteAsync((stage, err) -> Stages.scheduleDisplaying(stage))
              .whenCompleteAsync((stage, err) -> {
                  final Button btn = (Button) stage.getScene().getRoot().getChildrenUnmodifiable().get(0);
                  btn.fire();
                  Stages.scheduleHiding(stage);
              })
              .whenCompleteAsync((stage, err) -> {
                  final SAMPLE_CONTROL_CLASS testController = (SAMPLE_CONTROL_CLASS) controllerLookup.get();
                  assertThat(testController.locatedInstance).isTrue();
              })
              .toCompletableFuture()
              .get(5, TimeUnit.SECONDS);
    }

    private void assertPaneFailedLoadingAndDidNotRegister(
        final Supplier<Try<? extends Node>> failingLoadResultSupplier,
        final Option<FxmlController> controllerLookup,
        final Class<? extends Throwable> expectedFailureCauseClass
    ) {
        assertThatThrownBy(failingLoadResultSupplier::get)
            .isInstanceOf(FxmlNodeLoadException.class)
            .hasCauseInstanceOf(expectedFailureCauseClass);
        assertThat(controllerLookup.isEmpty()).isTrue();
    }

    @Ignore("This is not a test class")
    public enum TEST_NODES implements FxmlNode {
        PANE(
            () -> "fxml/test_pane.fxml",
            SAMPLE_CONTROL_CLASS.class
        ),

        BUTTON(
            () -> "fxml/button.fxml",
            NoControllerClass.class
        ),

        INVALID(
            () -> "fxml/invalid_file.fxml",
            NoControllerClass.class
        );

        private final FxmlFile fxmlFile;
        private final Class<? extends FxmlController> controllerClass;

        TEST_NODES(final FxmlFile fxmlFile, final Class<? extends FxmlController> controllerClass) {
            this.fxmlFile = fxmlFile;
            this.controllerClass = controllerClass;
        }

        @Override
        public FxmlFile getFile() {
            return this.fxmlFile;
        }

        @Override
        public Class<? extends FxmlController> getControllerClass() {
            return this.controllerClass;
        }

    }

}
