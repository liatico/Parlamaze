package ViewModel;

import Model.IModel;
import Model.MyModel;
import javafx.scene.input.KeyCode;

import java.io.File;
import java.util.Observable;
import java.util.Observer;

public class MyViewModel extends Observable implements Observer {

    private IModel model;

    // Constructor
    public MyViewModel(IModel model) {
        this.model = model;
    }

    // Generating the maze
    public void generateMaze(int height, int width, String CharacterName) {
        model.generateMaze(height, width, CharacterName);
    }

    // Solving the maze
    public void solveMaze() {
        model.solveMaze();
    }

    // Getter for the maze
    public int[][] getMaze() {
        return model.getMaze();
    }

    // Moving the character according the the key pressed
    public void moveCharacter(KeyCode movement) {
        model.moveCharacter(movement);
    }

    // Getter for the character's row
    public int getCharacterPositionRow() {
        return model.getCharacterPositionRow();
    }

    // Getter for the character's column
    public int getCharacterPositionColumn() {
        return model.getCharacterPositionColumn();
    }

    // Closing the servers
    public void close() {
        model.close();
    }

    // Getter for the start row
    public int getStartRow() {
        return model.getStartRow();
    }

    // Getter for the start column
    public int getStartCol() {
        return model.getStartCol();
    }

    // Getter for the end row
    public int getEndRow() {
        return model.getEndRow();
    }

    // Getter for the end column
    public int getEndCol() {
        return model.getEndCol();
    }

    @Override
    public void update(Observable o, Object arg) {
        if (o == model) {
            setChanged();
            notifyObservers(arg);
        }
    }

    // Saving the game to a file
    public void saveGame(File fileToSaveTo) {
        model.saveGame(fileToSaveTo);
    }

    // Loading the game from the file
    public void loadGame(File fileToOpen){
        model.loadGame(fileToOpen);
    }

    // Getting the solution path
    public int[] getSolutionPath() {
        return model.getSolution();
    }

    // Getter for the character's name
    public String getCharacterName(){
        return model.getCharacterName();
    }


}
