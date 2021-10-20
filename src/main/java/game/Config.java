package game;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import com.google.inject.Inject;
import constants.Constants;

public class Config {
    private int width;
    private int height;
    private int foodStatic;
    private int foodPerPlayer;
    private int stateDelayMs;
    private float deadFoodProb;
    private int pingDelayMs;
    private int nodeTimeoutMs;

    @Inject
    public Config() {
        loadProps(Constants.PathToConfig);
    }

    private void loadProps(String pathToConfig) {
        try (InputStream property = new FileInputStream(pathToConfig)) {
            Properties props = new Properties();
            props.load(property);

            width = Integer.parseInt(props.getProperty("width"));
            height = Integer.parseInt(props.getProperty("height"));
            foodStatic = Integer.parseInt(props.getProperty("foodStatic"));
            foodPerPlayer = Integer.parseInt(props.getProperty("foodPerPlayer"));
            stateDelayMs = Integer.parseInt(props.getProperty("stateDelayMs"));
            deadFoodProb = Float.parseFloat(props.getProperty("deadFoodProb"));
            pingDelayMs = Integer.parseInt(props.getProperty("pingDelayMs"));
            nodeTimeoutMs = Integer.parseInt(props.getProperty("nodeTimeoutMs"));
        } catch (IOException exception) {
            System.out.println("IOException while parse " + Constants.PathToConfig);
        } catch (NumberFormatException exception) {
            System.out.println("IOException while parse " + Constants.PathToConfig);
        }
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int getFoodStatic() {
        return foodStatic;
    }

    public int getFoodPerPlayer() {
        return foodPerPlayer;
    }

    public int getStateDelayMs() {
        return stateDelayMs;
    }

    public float getDeadFoodProb() {
        return deadFoodProb;
    }

    public int getPingDelayMs() {
        return pingDelayMs;
    }

    public int getNodeTimeoutMs() {
        return nodeTimeoutMs;
    }
}
