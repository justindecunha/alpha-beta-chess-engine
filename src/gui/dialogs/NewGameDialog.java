package gui.dialogs;


import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

/**
 * The game setup dialog, which allows the player to select player type and teams
 */
public class NewGameDialog extends Dialog {

     public NewGameDialog() {
        this.setTitle("New Game");
        DialogPane dialogPane = this.getDialogPane();
        dialogPane.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        ObservableList<String> team = FXCollections.observableArrayList("White  ", "Black");
        ObservableList<String> playerType = FXCollections.observableArrayList("AI", "Human");

        ComboBox<String> comboBox1 = new ComboBox<>(team);
        ComboBox<String> comboBox2 = new ComboBox<>(playerType);

        comboBox1.getSelectionModel().selectFirst();
        comboBox2.getSelectionModel().selectFirst();

        Label label1 = new Label("Your Colour: ");
        Label label2 = new Label("   Opponent: ");
        VBox vBox = new VBox(16, label1, label2);
        vBox.setTranslateY(5);
        HBox hBox = new HBox(8, vBox, new VBox(8, comboBox1, comboBox2));
        dialogPane.setContent(hBox);

        this.setResultConverter(button -> {

            Boolean[] response = new Boolean[2];

            if (button == ButtonType.OK) {
                boolean playerIsWhite = comboBox1.getValue().equals("White  ");
                boolean aiPlayer = comboBox2.getValue().equals("AI");

                response[0] = playerIsWhite;
                response[1] = aiPlayer;

                return response;
            } else {
                return null;
            }
        });
    }
}
