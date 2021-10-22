import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import view.DependencyInjector;

import java.io.IOException;

/*
fixme баг когда выходишь из игры announcer продолжает слать инвайты
fixme баг с выходами из сессии
*/

public class JavaFXRunner extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setControllerFactory(DependencyInjector.INSTANCE.injector::getInstance);

        loader.setLocation(getClass().getResource("/" + "MenuScene.fxml"));
        Parent root = loader.load();

        primaryStage.setScene(new Scene(root));
        primaryStage.show();

        primaryStage.setOnCloseRequest(e -> {
            Platform.exit();
            System.exit(0);
        });
    }
}
