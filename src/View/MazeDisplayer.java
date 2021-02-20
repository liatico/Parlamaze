package View;

import Server.Server;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import java.io.FileInputStream;

public class MazeDisplayer extends FatherDisplayer {

    private int goalRow;
    private int goalCol;
    private int startRow;
    private int startCol;
    private int[][] maze;

    // Setting the goal position in the maze
    public void setGoalPosition(int row, int col)
    {
        this.goalRow = row;
        this.goalCol = col;
    }

    // Setting the start position in the maze
    public void setStartPosition(int row, int col)
    {
        this.startRow = row;
        this.startCol = col;
    }

    // Setting the maze
    public void setDimentions(int[][] maze){
        this.maze = maze;
        redraw();
    }

    // Drawing the maze
    public void redraw(){
        if (maze != null){

            initSizes();

            GraphicsContext graphicsContext2D = getGraphicsContext2D();
            graphicsContext2D.clearRect(0,0,originialCanvasWidth,originalCanvasHeight); //Clean the Canvas
            graphicsContext2D.setFill(Color.GREEN); //Set color to the context
            Image wallImage = null;
            Image goalImage = null;
            Image startImage = null;

            try {
                String wallImagePath = getCurrentFixedPath() + "/resources/General/wall.jpg";
                wallImage = new Image(new FileInputStream(wallImagePath));
                String goalImagePath = getCurrentFixedPath() + "/resources/General/solution.png";
                goalImage = new Image(new FileInputStream(goalImagePath));
                String startImagePath = getCurrentFixedPath() + "/resources/General/startPosition.png";
                startImage = new Image(new FileInputStream(startImagePath));
            }
            catch (Exception e)
            {
                Server.LOG.debug("image loader exception");
            }

            //Draw maze
            for (int row = 0; row < maze.length; row++) {
                for (int column = 0; column < maze[row].length; column++) {
                    if (maze[row][column] == 1){
                        graphicsContext2D.drawImage(wallImage, column * cellWidth, row * cellHeight, cellWidth, cellHeight);
                    }

                }
            }
            graphicsContext2D.drawImage(startImage, startCol * cellWidth, startRow * cellHeight, cellWidth, cellHeight);
            graphicsContext2D.drawImage(goalImage, goalCol * cellWidth, goalRow * cellHeight, cellWidth, cellHeight);


        }
    }
}
