import com.google.inject.Guice;
import com.google.inject.Injector;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import view.BasicModule;

import java.io.IOException;

public class JavaFXRunner extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        Injector injector = Guice.createInjector(new BasicModule());

        FXMLLoader loader = new FXMLLoader();
        loader.setControllerFactory(injector::getInstance);

        loader.setLocation(getClass().getResource("/" + "MenuScene.fxml"));
        Parent root = loader.load();

        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }
}
