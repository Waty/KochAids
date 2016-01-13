/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.waty.client.jsf31kochfractalfx;

import com.waty.server.calculate.Edge;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

/**
 * @author Nico Kuijpers
 */
public class JSF31KochFractalFX extends Application {

    private final int kpWidth = 500;
    private final int kpHeight = 500;
    // Zoom
    private double zoom = Math.min(kpWidth, kpHeight);
    // Current level of Koch fractal
    private int currentLevel = 1;
    // Labels for level, nr edges, calculation time, and drawing time
    private Label labelLevel;
    // Koch panel and its size
    private Canvas kochPanel;
    private Socket socket;
    private DataOutputStream outputStream;
    private DataInputStream inputStream;
    private Label labelNrEdgesText;

    /**
     * The main() method is ignored in correctly deployed JavaFX application.
     * main() serves only as fallback in case the application can not be
     * launched through deployment artifacts, e.g., in IDEs with limited FX
     * support. NetBeans ignores main().
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        // Define grid pane
        GridPane grid;
        grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25, 25, 25, 25));

        // For debug purposes
        // Make de grid lines visible
        // grid.setGridLinesVisible(true);
        // Drawing panel for Koch fractal
        kochPanel = new Canvas(kpWidth, kpHeight);
        grid.add(kochPanel, 0, 3, 25, 1);
        kochPanel.addEventHandler(MouseEvent.MOUSE_CLICKED, this::kochPanelMouseClicked);

        // Labels to present number of edges for Koch fractal
        Label labelNrEdges = new Label("Nr edges:");
        labelNrEdgesText = new Label();
        grid.add(labelNrEdges, 0, 0, 4, 1);
        grid.add(labelNrEdgesText, 3, 0, 22, 1);

        // Labels to present time of calculation for Koch fractal
        Label labelCalc = new Label("Calculating: N/A");
        Label labelCalcText = new Label();
        grid.add(labelCalc, 0, 1, 4, 1);
        grid.add(labelCalcText, 3, 1, 22, 1);

        // Labels to present time of drawing for Koch fractal
        Label labelDraw = new Label("Drawing: N/A");
        Label labelDrawText = new Label();
        grid.add(labelDraw, 0, 2, 4, 1);
        grid.add(labelDrawText, 3, 2, 22, 1);

        // Label to present current level of Koch fractal
        labelLevel = new Label("Level: " + currentLevel);
        grid.add(labelLevel, 0, 6);

        // Button to increase level of Koch fractal
        Button buttonIncreaseLevel = new Button();
        buttonIncreaseLevel.setText("Increase Level");
        buttonIncreaseLevel.setOnAction(a -> setLevel(currentLevel + 1));
        grid.add(buttonIncreaseLevel, 3, 6);

        // Button to decrease level of Koch fractal
        Button buttonDecreaseLevel = new Button();
        buttonDecreaseLevel.setText("Decrease Level");
        buttonDecreaseLevel.setOnAction(event -> setLevel(currentLevel - 1));
        grid.add(buttonDecreaseLevel, 5, 6);

        Button bGetAllEdges = new Button();
        bGetAllEdges.setText("GetAllEdges");
        bGetAllEdges.setOnAction(this::bGetAllEdgesClicked);
        grid.add(bGetAllEdges, 14, 6);

        // Create the scene and add the grid pane
        Group root = new Group();
        Scene scene = new Scene(root, kpWidth + 50, kpHeight + 170);
        root.getChildren().add(grid);

        // Define title and assign the scene for main window
        primaryStage.setTitle("Koch Fractal");
        primaryStage.setScene(scene);
        primaryStage.show();

        initTcpConnection();
    }

    private void kochPanelMouseClicked(MouseEvent event) {
        if (event.getButton() == MouseButton.PRIMARY) zoom *= 2.0;
        else if (event.getButton() == MouseButton.SECONDARY) zoom /= 2.0;

        // notify the server
        try {
            outputStream.writeUTF("ChangeZoomLevel");
            outputStream.writeDouble(zoom);
            parseResponse();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void parseResponse() throws IOException {
        int responseCount = inputStream.readInt();

        clearKochPanel();
        labelNrEdgesText.setText("" + responseCount);
        for (int i = 0; i < responseCount; i++) {
            drawEdge(Edge.parse(inputStream));
        }
    }

    private void initTcpConnection() throws IOException {
        socket = new Socket("localhost", 1337);
        outputStream = new DataOutputStream(socket.getOutputStream());
        inputStream = new DataInputStream(socket.getInputStream());
        getAllEdges(false);
    }

    private void bGetAllEdgesClicked(ActionEvent actionEvent) {
        try {
            getAllEdges(true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void getAllEdges(boolean batch) throws IOException {
        if (batch) outputStream.writeUTF("GetAllEdges");
        else outputStream.writeUTF("GetEdges");
        outputStream.writeInt(currentLevel);
        parseResponse();
    }

    public void clearKochPanel() {
        // System.out.println("Clearing panel");
        GraphicsContext gc = kochPanel.getGraphicsContext2D();
        gc.clearRect(0.0, 0.0, kpWidth, kpHeight);
        gc.setFill(Color.BLACK);
        gc.fillRect(0.0, 0.0, kpWidth, kpHeight);
    }

    public void drawEdge(Edge edge) {
        // Graphics
        GraphicsContext gc = kochPanel.getGraphicsContext2D();

        // Set line color
        gc.setStroke(edge.color);

        // Set line width depending on level
        if (currentLevel <= 3) gc.setLineWidth(2.0);
        else if (currentLevel <= 5) gc.setLineWidth(1.5);
        else gc.setLineWidth(1.0);

        // Draw line
        gc.strokeLine(edge.X1, edge.Y1, edge.X2, edge.Y2);
    }

    public void setLevel(int level) {
        if (level >= 1 && level <= 12)
            this.currentLevel = level;
        labelLevel.setText("Level: " + currentLevel);
        try {
            getAllEdges(false);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void stop() throws Exception {
        socket.close();
    }
}
