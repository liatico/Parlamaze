package View;

import Server.Server;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import java.io.FileInputStream;

public class SolutionDisplayer extends FatherDisplayer {

    private int[] solution;

    // Setter for the solution
    public void setSolution(int[] sol) {

        solution = sol;
    }

    // Drawing the solution
    public void showSolution() {

        initSizes();

        GraphicsContext graphicsContext2D = getGraphicsContext2D();
        graphicsContext2D.clearRect(0, 0, originialCanvasWidth, originalCanvasHeight);

        Image solutionImage = null;
        try {
            String characterImagePath = getCurrentFixedPath() + "/resources/General/solutionPath.png";
            solutionImage = new Image(new FileInputStream(characterImagePath));
        } catch (Exception e) {
            Server.LOG.debug("image loader exception");

        }
        int row, column;

        for (int i = 0; i < solution.length; i = i + 2) {
            row = solution[i];
            column = solution[i + 1];

            graphicsContext2D.drawImage(solutionImage, column * cellWidth, row * cellHeight, cellWidth, cellHeight);
        }

    }

    // Hiding the solution
    public void hideSolution() {
        GraphicsContext graphicsContext2D = getGraphicsContext2D();
        graphicsContext2D.clearRect(0, 0, getWidth(), getHeight());
    }



}