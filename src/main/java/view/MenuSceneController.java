package view;

import com.google.inject.Inject;
import game.Field;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import net.GameAnnounce;
import net.Node;
import net.Role;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;

public class MenuSceneController implements Initializable {
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

    private Timer timer;

    @FXML
    private void newGame(ActionEvent event) throws IOException {
        node.setInGame(true);
        node.setRole(Role.MASTER);
        node.setField(new Field(node.getConfig(), node));
        node.getField().spawnMySnake(1);
        for (int i = 0; i < node.getConfig().getFoodStatic(); i++)
            node.getField().spawnNewEat();

        FXMLLoader loader = new FXMLLoader();
        loader.setControllerFactory(DependencyInjector.INSTANCE.injector::getInstance);

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

        for (GameAnnounce announce : node.getAvailableGames().getMap().values()) {
            Button newGame = new Button(announce.getFrom().getHostName() + ":" + announce.getFrom().getPort());
            newGame.setPrefWidth(200);
            newGame.setOnAction(this::connect);
            VBox.getChildren().add(newGame);
        }
    }

    private void connect(ActionEvent actionEvent) {
        node.setInGame(true);
        Button sourceButton = (Button) actionEvent.getSource();
        System.out.println(sourceButton.getText());

        InetSocketAddress addr = new InetSocketAddress(
                sourceButton.getText().substring(0, sourceButton.getText().lastIndexOf(":")),
                Integer.parseInt(sourceButton.getText().substring(sourceButton.getText().lastIndexOf(":") + 1)));

        node.connect("stas", addr, node.getNodeId());
        node.setRole(Role.NORMAL);

        for (GameAnnounce a : node.getAvailableGames().getMap().values()) {
            if (a.getFrom().equals(addr)) {
                node.setField(new Field(a.getCfg(), node));
            }
        }

        FXMLLoader loader = new FXMLLoader();
        loader.setControllerFactory(DependencyInjector.INSTANCE.injector::getInstance);

        loader.setLocation(getClass().getResource("/" + "GameFieldScene.fxml"));
        try {
            Parent root = loader.load();

            Stage primaryStage = (Stage) VBox.getScene().getWindow();
            primaryStage.setScene(new Scene(root));
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("connecting");
    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                Platform.runLater(() -> showAvalGames());
            }
        }, 0, 500);
    }
}
