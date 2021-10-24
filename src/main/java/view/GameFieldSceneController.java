package view;

import com.google.inject.Inject;
import game.*;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import net.Node;
import net.Role;

import java.io.IOException;
import java.net.URL;
import java.util.*;

/* резмер игровой области: 400 на 400 */
public class GameFieldSceneController implements Initializable {
    @Inject
    private Node node;

    @FXML
    private Button againButton;

    @FXML
    private Label scoreLabel;

    @FXML
    private Button exitButton;
    @FXML
    private GridPane gameGrid;

    private Rectangle[][] rectangles;

    private Timer timer;

    @FXML
    private void exit(ActionEvent action) throws IOException {
        timer.cancel();
        node.sendAnnounces(false);
        node.setInGame(false);
        node.setRole(Role.NORMAL);

        FXMLLoader loader = new FXMLLoader();
        loader.setControllerFactory(DependencyInjector.INSTANCE.injector::getInstance);

        loader.setLocation(getClass().getResource("/" + "MenuScene.fxml"));
        Parent root = loader.load();

        Stage primaryStage = (Stage) againButton.getScene().getWindow();
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }

    @FXML
    private void again(ActionEvent action) {
        node.getField().spawnMySnake(1);
        againButton.setDisable(true);
    }

    @FXML
    private void keyProcessor(KeyEvent event) {
        if (event.getCode().equals(KeyCode.W)) {
            if (node.getRole() == Role.MASTER)
                node.getField().setDirectionToMySnake(Direction.UP);
            else
                node.sendDirection(Direction.UP);
        }

        if (event.getCode().equals(KeyCode.A)) {
            if (node.getRole() == Role.MASTER)
                node.getField().setDirectionToMySnake(Direction.LEFT);
            else
                node.sendDirection(Direction.LEFT);
        }

        if (event.getCode().equals(KeyCode.S)) {
            if (node.getRole() == Role.MASTER)
                node.getField().setDirectionToMySnake(Direction.DOWN);
            else
                node.sendDirection(Direction.DOWN);
        }

        if (event.getCode().equals(KeyCode.D)) {
            if (node.getRole() == Role.MASTER)
                node.getField().setDirectionToMySnake(Direction.RIGHT);
            else
                node.sendDirection(Direction.RIGHT);
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        System.out.println("init");
        ArrayList<ColumnConstraints> cc = new ArrayList<>();
        ArrayList<RowConstraints> rr = new ArrayList<>();

        for (int i = 0; i < node.getField().getWidth(); i++) {
            ColumnConstraints col = new ColumnConstraints(node.getField().getWidth());
            col.setMinWidth(400.0 / node.getField().getWidth());
            cc.add(col);
        }

        for (int i = 0; i < node.getField().getHeight(); i++) {
            RowConstraints row = new RowConstraints(node.getField().getHeight());
            row.setMinHeight(400.0 / node.getField().getHeight());
            rr.add(row);
        }

        gameGrid.getRowConstraints().addAll(rr);
        gameGrid.getColumnConstraints().addAll(cc);

        rectangles = new Rectangle[node.getField().getWidth()][node.getField().getHeight()];
        for (int i = 0; i < node.getField().getWidth(); i++) {
            for (int j = 0; j < node.getField().getHeight(); j++) {
                rectangles[i][j] = new Rectangle(400.0 / node.getField().getWidth(), 400.0 / node.getField().getHeight(), Color.WHITE);
                gameGrid.add(rectangles[i][j], i, j);
            }
        }
        scoreLabel.setText("0");
        againButton.setDisable(true);

        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                Platform.runLater(() -> update());
            }
        }, 0, node.getField().getConfig().getStateDelayMs());

        node.sendAnnounces(true);
    }

    private void clearField() {
        for (int i = 0; i < node.getField().getWidth(); i++) {
            for (int j = 0; j < node.getField().getHeight(); j++) {
                rectangles[i][j].setFill(Color.WHITE);
            }
        }
    }

    private void setSnakesColour(Color color) {
        for (Snake snake : node.getField().getAliveSnakes()) {
            List<Point> body = snake.getBody();

            for (Point p : body) {
                rectangles[p.width][p.height].setFill(color);
            }
        }
    }

    private void setEatColour(Color color) {
        for (Point eat : node.getField().getEatPoints()) {
            rectangles[eat.width][eat.height].setFill(color);
        }
    }

    public void update() {
        if (!node.isInGame())
            return;

        clearField();

        if (node.getRole() == Role.MASTER)
            node.getField().makeTurn();

        setEatColour(Color.RED);
        setSnakesColour(Color.GREEN);

        if (node.getField().getMySnake() != null) {
            scoreLabel.setText(String.valueOf(node.getField().getMySnake().getScore()));
        } else
            return;

        if (node.getField().getMySnake().getState() == SnakeState.ZOMBIE) {
            againButton.setDisable(false);
        }
    }
}
