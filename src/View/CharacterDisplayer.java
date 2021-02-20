package View;

import Server.Server;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import java.io.FileInputStream;

public class CharacterDisplayer extends FatherDisplayer {

    private int characterRowIndex = 0;
    private int characterColIndex = 0;
    private String characterName;
    private String side;

    // Setting the position of the character
    public void setPosition(int row, int col, String side){
        characterRowIndex = row;
        characterColIndex = col;
        this.side = side;
        redraw();
    }

    // Setter for the character's name
    public void setCharacterName(String name)
    {
        this.characterName = name;
    }

    // Drawing the character
    public void redraw() {

        initSizes();

        GraphicsContext graphicsContext2D = getGraphicsContext2D();
        graphicsContext2D.clearRect(0,0,originialCanvasWidth,originalCanvasHeight); //Clean the Canvas
        Image characterImage = null;

        try {
            String characterImagePath = getCurrentFixedPath() + "/resources/Characters/" + this.characterName +"/" + side + ".png";
            characterImage = new Image(new FileInputStream(characterImagePath));
        }
        catch (Exception e)
        {
            Server.LOG.debug("Error - can't load image - CharacterDisplayer");
        }

        graphicsContext2D.drawImage(characterImage, characterColIndex * cellWidth, characterRowIndex * cellHeight, cellWidth, cellHeight);
    }


}
