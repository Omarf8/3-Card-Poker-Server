import java.io.IOException;
import java.net.ServerSocket;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.application.Platform;
import javafx.event.ActionEvent;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

public class ServerController implements Initializable {
    // Server Off FXML Elements
    @FXML
    public BorderPane offBP;

    @FXML
    public TextField portTF;

    @FXML
    public Button start;

    // Server On FXML Elements
    @FXML
    public BorderPane onBP;

    @FXML
    public Label clientCount;

    @FXML
    public ListView<String> listItems;

    private Server server;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // TODO Auto-generated method stub
    }

    public void setServer(Server server) {
        this.server = server;
    }

    public void setListView(ListView<String> list) {
        this.listItems = list;
    }

    public void startServerMethod() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXML/serverOn.fxml"));
            Parent root2 = loader.load(); // Load view into parent
            ServerController ctrl = loader.getController(); // Controller created by the FXML
            root2.getStylesheets().add("/styles/serverOnStyle.css"); // Set the style for the second scene

            Server server = new Server(Integer.parseInt(portTF.getText()), data -> {
                Platform.runLater(()->{
                    // For some reason there's a nullpointer exception sometimes
                    if(data != null) {
                        ctrl.listItems.getItems().add(data.toString());
                    }
                });
            });

            ctrl.clientCount.textProperty().bind(server.count.asString("Clients Connected: %d")); // This binds the # of clients and shows it in the scene
            ctrl.setServer(server); // This gives us access to the Server object if we need it in the new scene
            offBP.getScene().setRoot(root2); // Set the scene from the welcome screen to the new scene when server is on
        }
        catch (NumberFormatException e) {
            // Stay in the same scene and alert the user
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Invalid Input");
            alert.setContentText("Please enter a valid port number");
            alert.showAndWait();
        }
        catch (Exception e) {
            // Stay in the same scene and alert the user
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setContentText("Something went wrong with loading the scene...");
            alert.showAndWait();
        }
    }

    public void setConnect() {
        // If the text field is empty then disable the Button and vice versa
        start.disableProperty().bind(portTF.textProperty().isEmpty());
    }

    public void stopServerMethod() {
        try {
            // Stop any threads currently active before changing scenes
            server.stopThreads();

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXML/serverWelcome.fxml"));
            Parent root = loader.load();
            ServerController ctrl = loader.getController();
            root.getStylesheets().add("/styles/serverWelcomeStyle.css"); // Set style for the welcome scene

            ctrl.setConnect(); // Set the binding with the Button and TextField
            onBP.getScene().setRoot(root);
        }
        catch (Exception e) {
            // Stay in the same scene and alert the user
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setContentText("Something went wrong with loading the scene...");
            alert.showAndWait();
        }
    }
}
