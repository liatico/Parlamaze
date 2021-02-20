package View;

import javafx.scene.canvas.Canvas;
import java.nio.file.FileSystems;

public class FatherDisplayer extends Canvas {

    protected int mazeHeight;
    protected int mazeWidth;
    protected double zoom = 1;
    double originalCanvasHeight;
    double originialCanvasWidth;
    double cellHeight;
    double cellWidth;

    // Initialize the parameters
    protected void initSizes()
    {
        originalCanvasHeight = getHeight();
        originialCanvasWidth = getWidth();
        double canvasHeight = originalCanvasHeight * zoom;
        double canvasWidth = originialCanvasWidth * zoom;
        cellHeight = canvasHeight / mazeHeight;
        cellWidth = canvasWidth / mazeWidth;
    }

    // Setter for the maze's rows and columns
    protected void setMaze(int rows, int columns)
    {
        this.mazeHeight = rows;
        this.mazeWidth = columns;
    }

    // Getter for the current path
    protected String getCurrentFixedPath() {
        String absoluteCurrentPath = FileSystems.getDefault().getPath(".").toAbsolutePath().toString();
        absoluteCurrentPath = absoluteCurrentPath.substring(0, absoluteCurrentPath.length() - 2);
        String fixedPath = "";

        for (int i = 0; i < absoluteCurrentPath.length(); i++) {
            if (absoluteCurrentPath.charAt(i) == '\\')
                fixedPath = fixedPath + "/";
            else
                fixedPath = fixedPath + absoluteCurrentPath.charAt(i);
        }

        return fixedPath;
    }

    // Setter for the zoom
    public void setZoom(double newZoom)
    {
        this.zoom = newZoom;
    }

    // Getter for the zoom
    public double getZoom()
    {
        return zoom;
    }



}
