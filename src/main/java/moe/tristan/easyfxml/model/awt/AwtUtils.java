package moe.tristan.easyfxml.model.awt;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public final class AwtUtils {
    private static final Logger LOG = LoggerFactory.getLogger(AwtUtils.class);

    private static final java.awt.Toolkit awtTk = java.awt.Toolkit.getDefaultToolkit();

    public static <RES> CompletionStage<RES> asyncAwtOperation(final Supplier<RES> awtOperation) {
        final CompletableFuture<RES> futureResult = new CompletableFuture<>();
        SwingUtilities.invokeLater(() -> {
            final RES value = awtOperation.get();
            futureResult.complete(value);
        });
        return futureResult;
    }

    public static <RES, REQ> CompletionStage<RES> asyncAwtCallbackWithRequirement(
        Supplier<REQ> requirement,
        Function<REQ, RES> awtOperation
    ) {
        return asyncAwtOperation(requirement)
            .thenCompose(
                req -> asyncAwtOperation(
                    () -> awtOperation.apply(req)
                )
            );
    }

    public static <REQ> CompletionStage<Void> asyncAwtRunnableWithRequirement(
        Supplier<REQ> requirement,
        Consumer<REQ> awtOperation
    ) {
        return asyncAwtCallbackWithRequirement(
            requirement,
            req -> {
                awtOperation.accept(req);
                return null;
            }
        );
    }
}
