/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.waty.jsf31kochfractalfx;

import com.waty.Calculator;
import com.waty.MemMappedReader;
import com.waty.calculate.Edge;
import com.waty.calculate.KochFractal;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * @author Nico Kuijpers
 */
public class JSF31KochFractalFX extends Application {

    private final int kpWidth = 500;
    private final int kpHeight = 500;
    // Zoom and drag
    private double zoomTranslateX = 0.0;
    private double zoomTranslateY = 0.0;
    private double zoom = 1.0;
    // Current level of Koch fractal
    private int currentLevel = 0;
    // Labels for level, nr edges, calculation time, and drawing time
    private Label labelLevel;
    // Koch panel and its size
    private Canvas kochPanel;
    private Stage primaryStage;
    private Thread fileReaderThread;

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
        this.primaryStage = primaryStage;
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

        // Labels to present number of edges for Koch fractal
        Label labelNrEdges = new Label("Nr edges:");
        Label labelNrEdgesText = new Label();
        grid.add(labelNrEdges, 0, 0, 4, 1);
        grid.add(labelNrEdgesText, 3, 0, 22, 1);

        // Labels to present time of calculation for Koch fractal
        Label labelCalc = new Label("Calculating:");
        Label labelCalcText = new Label();
        grid.add(labelCalc, 0, 1, 4, 1);
        grid.add(labelCalcText, 3, 1, 22, 1);

        // Labels to present time of drawing for Koch fractal
        Label labelDraw = new Label("Drawing:");
        Label labelDrawText = new Label();
        grid.add(labelDraw, 0, 2, 4, 1);
        grid.add(labelDrawText, 3, 2, 22, 1);

        // Label to present current level of Koch fractal
        labelLevel = new Label("Level: " + currentLevel);
        grid.add(labelLevel, 0, 6);

        // Create Koch manager and set initial level
        resetZoom();

        // Create the scene and add the grid pane
        Group root = new Group();
        Scene scene = new Scene(root, kpWidth + 50, kpHeight + 170);
        root.getChildren().add(grid);

        // Define title and assign the scene for main window
        primaryStage.setTitle("Koch Fractal");
        primaryStage.setScene(scene);
        primaryStage.show();

        clearKochPanel();

        fileReaderThread = new Thread(() -> {
            try (MemMappedReader reader = new MemMappedReader(Calculator.PATH)) {
                //noinspection InfiniteLoopStatement
                while (true) {
                    int level = reader.getLevel();
                    if (level != currentLevel) {
                        currentLevel = level;
                        readFile(reader, level);
                    } else {
                        Thread.sleep(1);
                    }
                }
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        });
        fileReaderThread.start();
    }

    private void readFile(MemMappedReader reader, int level) throws IOException {
        System.out.println("reading lvl " + level);
        // update ui
        Platform.runLater(() -> {
            clearKochPanel();
            labelLevel.setText(level + "");
        });

        //reset position of the reader
        reader.resetPosition();

        //draw all edges
        int edgesCount = getEdgesCount();
        for (int i = 0; i < edgesCount; i++) {
            System.out.println("reading edge #" + (i + 1) + "/" + edgesCount);
            Edge edge = reader.readEdge();
            Platform.runLater(() -> drawEdge(edge));
        }
    }

    @Override
    public void stop() throws Exception {
        super.stop();
        fileReaderThread.interrupt();
    }

    public void clearKochPanel() {
        // System.out.println("Clearing panel");
        GraphicsContext gc = kochPanel.getGraphicsContext2D();
        gc.clearRect(0.0, 0.0, kpWidth, kpHeight);
        gc.setFill(Color.BLACK);
        gc.fillRect(0.0, 0.0, kpWidth, kpHeight);
    }

    public void drawEdge(Edge e) {
        // Graphics
        GraphicsContext gc = kochPanel.getGraphicsContext2D();

        // Adjust edge for zoom and drag
        Edge e1 = edgeAfterZoomAndDrag(e);

        // Set line color
        gc.setStroke(e1.color);
        //else gc.setStroke(Color.WHITE);

        // Set line width depending on level
        if (currentLevel <= 3) gc.setLineWidth(2.0);
        else if (currentLevel <= 5) gc.setLineWidth(1.5);
        else gc.setLineWidth(1.0);

        // Draw line
        gc.strokeLine(e1.X1, e1.Y1, e1.X2, e1.Y2);
    }

    private void resetZoom() {
        int kpSize = Math.min(kpWidth, kpHeight);
        zoom = kpSize;
        zoomTranslateX = (kpWidth - kpSize) / 2.0;
        zoomTranslateY = (kpHeight - kpSize) / 2.0;
    }

    private Edge edgeAfterZoomAndDrag(Edge e) {
        return new Edge(
                e.X1 * zoom + zoomTranslateX,
                e.Y1 * zoom + zoomTranslateY,
                e.X2 * zoom + zoomTranslateX,
                e.Y2 * zoom + zoomTranslateY,
                e.color);
    }

    public int getEdgesCount() {
        KochFractal kf = new KochFractal();
        kf.setLevel(currentLevel);
        return kf.getNrOfEdges();
    }
}
