package moe.tristan.easyfxml;

import moe.tristan.easyfxml.api.FxmlController;
import moe.tristan.easyfxml.api.FxmlNode;
import moe.tristan.easyfxml.model.beanmanagement.ControllerManager;
import moe.tristan.easyfxml.model.fxml.FxmlLoadResult;
import io.vavr.control.Try;

import javafx.scene.Node;
import javafx.scene.layout.Pane;

import java.util.function.Consumer;
import java.util.function.Function;

/**
 * This class is the base entry point of the library.
 * <p>
 * It is expected to be acquired from the Spring springContext or via
 * {@link moe.tristan.easyfxml.spring.NoSpringSupport}
 * if the project does not use Spring.
 *
 * @see Try
 * @see FxmlNode
 * @see FxmlLoadResult
 */
public interface EasyFxml {

    /**
     * Loads a {@link FxmlNode} as a Pane (this is the safest base class for all sorts of hierarchies) since most of the
     * base containers are subclasses of it.
     * <p>
     * It also registers its controller in the {@link ControllerManager} for later usage.
     * <p>
     * It returns a {@link Try} which is a monadic structure which allows us to do clean exception-handling.
     *
     * @param node The element's {@link FxmlNode} counterpart.
     *
     * @return A {@link Try} containing either the JavaFX {@link Pane} inside a {@link Try.Success} or the exception
     * that was raised during the chain of calls needed to load it, inside a {@link Try.Failure}. See {@link
     * Try#getOrElse(Object)}, {@link Try#onFailure(Consumer)}, {@link Try#recover(Function)} and related methods for
     * how to handle {@link Try.Failure}.
     */
    FxmlLoadResult<Pane, FxmlController> loadNode(final FxmlNode node);

    /**
     * Same as {@link #loadNode(FxmlNode)} but offers a selector parameter for multistage usage of {@link
     * ControllerManager}.
     *
     * @param node     The element's {@link FxmlNode} counterpart.
     * @param selector The selector for deduplication of Panes sharing the same {@link FxmlNode}.
     *
     * @return A Try of the {@link Pane} to be loaded. See {@link #loadNode(FxmlNode)} for more information on {@link
     * Try}.
     */
    FxmlLoadResult<Pane, FxmlController> loadNode(final FxmlNode node, final Object selector);

    /**
     * Same as {@link #loadNode(FxmlNode)} except you can choose the return type wished for instead of just {@link
     * Pane}.
     * <p>
     * It's a bad idea but it can sometimes be actually useful on the rare case where the element is really nothing like
     * a Pane. Be it a very complex button or a custom textfield with non-rectangular shape.
     *
     * @param node            The element's {@link FxmlNode} counterpart.
     * @param nodeClass       The class of the JavaFX {@link Node} represented by this {@link FxmlNode}.
     * @param controllerClass The class of the {@link FxmlController} managing this {@link FxmlNode}.
     * @param <N_CLAZZ>       The type of the node. Mostly for type safety.
     * @param <C_CLAZZ>       The type of the controller. Mostly for type safety.
     *
     * @return A {@link FxmlLoadResult} containing two {@link Try} instances. One for the node itself and one for
     * its controller. This allows for granular load error management.
     */
    <N_CLAZZ extends Node, C_CLAZZ extends FxmlController>
    FxmlLoadResult<N_CLAZZ, C_CLAZZ>
    loadNode(
        final FxmlNode node,
        final Class<N_CLAZZ> nodeClass,
        final Class<C_CLAZZ> controllerClass
    );

    /**
     * This is to {@link #loadNode(FxmlNode, Class, Class)} just what {@link #loadNode(FxmlNode, Object)} is to {@link
     * #loadNode(FxmlNode)}.
     *
     * @param node            The element's {@link FxmlNode} counterpart.
     * @param nodeClass       The class of the JavaFX {@link Node} represented by this {@link FxmlNode}.
     * @param controllerClass The class of the {@link FxmlController} managing this {@link FxmlNode}.
     * @param selector        The selector for deduplication of Panes sharing the same {@link FxmlNode}.
     * @param <N_CLAZZ>       The type of the node. Mostly for type safety.
     * @param <C_CLAZZ>       The type of the controller. Mostly for type safety.
     *
     * @return A {@link FxmlLoadResult} containing two {@link Try} instances. One for the node itself and one for
     * its controller. This allows for granular load error management.
     */
    <N_CLAZZ extends Node, C_CLAZZ extends FxmlController>
    FxmlLoadResult<N_CLAZZ, C_CLAZZ>
    loadNode(
        final FxmlNode node,
        final Class<N_CLAZZ> nodeClass,
        final Class<C_CLAZZ> controllerClass,
        final Object selector
    );

}
