import javafx.application.Application;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;


public class JavaFXTemplate extends Application {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		launch(args);
	}

	//feel free to remove the starter code from this method
	@Override
	public void start(Stage primaryStage) throws Exception {
		// TODO Auto-generated method stub
        try {
            // Read file fxml and draw interface
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXML/serverWelcome.fxml"));
            Parent root = loader.load();
            ServerController ctrl = loader.getController(); // Get the controller made
            ctrl.setConnect(); // Bind the Button and TextField

            Scene welcome = new Scene(root, 1000,700);
            welcome.getStylesheets().add("/styles/serverWelcomeStyle.css");
            primaryStage.setTitle("3 Card Poker");
            primaryStage.setScene(welcome);
            primaryStage.show();
        } catch(Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
	}

}
