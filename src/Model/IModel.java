package Model;

import javafx.scene.input.KeyCode;

import java.io.File;

public interface IModel {

    // Maze generating method
    void generateMaze(int width, int height, String CharacterName);

    // Getter for the maze
    int[][] getMaze();

    // Getter for the solution
    int[] getSolution();

    // Moving the character according to the key pressed
    void moveCharacter(KeyCode movement);

    // Getter for the current character row in the maze
    int getCharacterPositionRow();

    // Getter for the current character column in the maze
    int getCharacterPositionColumn();

    // Solving the maze
    void solveMaze();

    // Closing the servers
    void close();

    // Loading a new game from a file
    void loadGame(File fileToOpen);

    // Saving a game
    void saveGame(File fileToSaveTo);

    // Getter for the start row
    int getStartRow();

    // Getter for the start column
    int getStartCol();

    // Getter for the end row
    int getEndRow();

    // Getter for the end column
    int getEndCol();

    // Getter for the character's name
    String getCharacterName();

}
