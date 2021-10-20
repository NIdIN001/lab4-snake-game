package view;

import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import game.Field;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import net.Node;
import net.Role;

import java.awt.event.MouseEvent;
import java.io.IOException;
import java.lang.reflect.Array;
import java.net.InetSocketAddress;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;

public class MenuSceneController implements Initializable {
    private Injector injector = Guice.createInjector(new BasicModule());

    @Inject
    private Node node;

    @FXML
    private VBox VBox;

    @FXML
    private Button newGameButton;

    @FXML
    private TextField widthTextField;
    @FXML
    private TextField heightTextField;

    private ArrayList<InetSocketAddress> avalGames = new ArrayList<>();

    @FXML
    private void newGame(ActionEvent event) throws IOException {
        Node n = injector.getInstance(Node.class);
        n.setRole(Role.MASTER);
        n.setField(new Field(node.getConfig(), node));
        n.getField().spawnMySnake(1);

        FXMLLoader loader = new FXMLLoader();
        loader.setControllerFactory(injector::getInstance);

        loader.setLocation(getClass().getResource("/" + "GameFieldScene.fxml"));
        Parent root = loader.load();

        Stage primaryStage = (Stage) newGameButton.getScene().getWindow();
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }

    @FXML
    private void setHeight(ActionEvent event) {
    }

    @FXML
    private void setWidth(ActionEvent event) {
    }

    private void showAvalGames() {
        VBox.getChildren().clear();
        avalGames.addAll(node.getAvailableGames().getArray());
        for (InetSocketAddress addr : avalGames) {
            Button newGame = new Button(addr.getHostName());
            newGame.setPrefWidth(200);
            newGame.setOnAction(this::connect);
            VBox.getChildren().add(newGame);
        }
    }

    private void connect(ActionEvent actionEvent) {
        node.connect("stas", node.getAvailableGames().getArray().get(0));

        Node n = injector.getInstance(Node.class);
        n.setRole(Role.NORMAL);
        n.setField(new Field(node.getConfig(), node));

        FXMLLoader loader = new FXMLLoader();
        loader.setControllerFactory(injector::getInstance);

        loader.setLocation(getClass().getResource("/" + "GameFieldScene.fxml"));
        try {
            Parent root = loader.load();

            Stage primaryStage = (Stage) VBox.getScene().getWindow();
            primaryStage.setScene(new Scene(root));
            primaryStage.show();
            System.out.println("game scene");
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("connecting");
    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                Platform.runLater(() -> showAvalGames());
            }
        }, 0, 500);
    }
}
