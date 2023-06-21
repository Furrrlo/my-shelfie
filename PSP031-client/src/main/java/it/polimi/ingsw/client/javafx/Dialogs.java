package it.polimi.ingsw.client.javafx;

import org.jetbrains.annotations.Nullable;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.scene.Node;
import javafx.scene.Parent;

import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

/** Utility class which allows to add support and manage in-window dialogs to any pane */
public class Dialogs {

    public static Dialogs setupDialogSupport(Parent parent) {
        var dialogs = new Dialogs(new SimpleObjectProperty<>(parent, "visibleDialog"));

        // Hide and disable / un-hide and enable all nodes when a dialog displayed/hidden
        dialogs.visible.addListener((obs, oldVal, newVal) -> {
            dialogs.blurredBgDialogs.removeIf(r -> r.get() == null);
            boolean isShowingPaneWithBlurredBg = dialogs.blurredBgDialogs.stream()
                    .map(Reference::get)
                    .anyMatch(v -> Objects.equals(newVal, v));
            boolean wasShowingPaneWithBlurredBg = dialogs.blurredBgDialogs.stream()
                    .map(Reference::get)
                    .anyMatch(v -> Objects.equals(oldVal, v));

            if (newVal != null) {
                // Make sure the new pane is not disabled and opaque, as it could happen if it was previously set
                // by another pane
                newVal.setDisable(false);
                newVal.setOpacity(1);
            }

            if (isShowingPaneWithBlurredBg == wasShowingPaneWithBlurredBg)
                return;

            if (isShowingPaneWithBlurredBg) {
                for (Node n : parent.getChildrenUnmodifiable()) {
                    if (!n.equals(newVal)) {
                        n.setDisable(true);
                        n.setOpacity(0.5);
                    }
                }
            } else /* if (wasShowingPaneWithBlurredBg) */ {
                // restore all the children ( setDisable = true )
                for (Node n : parent.getChildrenUnmodifiable()) {
                    n.setDisable(false);
                    n.setOpacity(1);
                }
            }
        });

        return dialogs;
    }

    private final ObjectProperty<Parent> visible;
    private final List<WeakReference<Parent>> blurredBgDialogs = new ArrayList<>();

    private Dialogs(ObjectProperty<Parent> visible) {
        this.visible = visible;
    }

    public void register(Parent dialog, boolean blurredBg) {
        var weakComponentRef = new WeakReference<>(dialog);
        dialog.setVisible(Objects.equals(visible.get(), dialog));
        dialog.visibleProperty().addListener((obs, oldV, newV) -> {
            if (newV)
                visible.set(dialog);
            else if (Objects.equals(dialog, visible.get()))
                visible.set(null);
        });
        // Use a weak ref to allow component to be garbage collected if nobody else is holding it
        final var visibleListenerRef = new AtomicReference<ChangeListener<? super Parent>>();
        visibleListenerRef.set((obs, oldV, newV) -> {
            var currComponent = weakComponentRef.get();
            if (currComponent == null) {
                ChangeListener<? super Parent> visibleListener = visibleListenerRef.getAndSet(null);
                if (visibleListener != null)
                    visible.removeListener(visibleListener);
                return;
            }

            if (Objects.equals(oldV, currComponent))
                currComponent.setVisible(false);
            if (Objects.equals(newV, currComponent)) {
                currComponent.toFront();
                currComponent.setVisible(true);
            }
        });
        visible.addListener(visibleListenerRef.get());

        if (blurredBg)
            blurredBgDialogs.add(weakComponentRef);
    }

    public @Nullable Parent getVisible() {
        return visible.get();
    }

    public ObjectProperty<Parent> visibleProperty() {
        return visible;
    }

    public void setVisible(@Nullable Parent visible) {
        this.visible.set(visible);
    }
}
