package gui.dialogs;

import javafx.scene.control.Alert;

/**
 * A class that displays a simple dialog with an OK button, a title, and a message.
 */
public class InformationDialog extends Alert {
    public InformationDialog(final String title, final String message) {
        super(Alert.AlertType.INFORMATION);
        
        this.setGraphic(null);
        this.setHeaderText("");
        this.setTitle(title);
        this.setContentText(message);
        this.showAndWait().ifPresent(response -> this.close());
    }
}
