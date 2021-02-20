package View;

import java.io.*;
import java.net.URL;
import java.util.Observable;
import java.util.ResourceBundle;

import Server.Server;
import ViewModel.MyViewModel;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class GameViewController extends FatherController implements Initializable {


    @FXML private static MediaPlayer characterMediaPlayer = null;
    @FXML private static MediaPlayer walkIntoWallMediaPlayer = null;
    @FXML private static MediaView endGoalMediaView = null;
    private static Stage tmpStage;
    @FXML private static MediaPlayer endGoalMediaPlayer;
    @FXML public TextField textField_mazeRows;
    @FXML public TextField textField_mazeColumns;
    @FXML private ChoiceBox player;
    @FXML private Button generateButton;
    @FXML private ImageView imageView;
    @FXML private ImageView logoImageView;
    @FXML public View.MazeDisplayer mazeDisplayer;
    @FXML public View.CharacterDisplayer charDisplayer;
    @FXML public View.SolutionDisplayer solutionDisplayer;
    @FXML public MenuItem saveButton;
    @FXML private Pane pane;
    @FXML private CheckBox solutionButton;
    private int rows;
    private int columns;


    // Information about the rows
    public void rowsInfo()
    {
        String st = "The number of rows.\n" +
                "Please enter a valid number between 5-1000.";

        showAlert(st, "Rows");
    }

    // Information about the columns
    public void columnsInfo()
    {
        String st = "The number of columns.\n" +
                "Please enter a valid number between 5-1000.";

        showAlert(st, "Columns");
    }

    // Information about the player
    public void playerInfo()
    {
        String st = "The character you will play with.";

        showAlert(st, "Character");
    }
    @Override
    public void setViewModel(MyViewModel viewModel){
        this.viewModel = viewModel;
    }

    // Generating the maze
    public void generateMaze(){
        String rowsString = textField_mazeRows.getText();
        String colsString = textField_mazeColumns.getText();

        if (player.getValue().toString().equals("default"))
            showAlert("Character field is empty, please choose a character.", "Character field invalid");

        else if (!isLegalNumber(rowsString))
            showAlert("Rows field is illegal.\n" +
                    "Only numbers allowed between 5-1000.", "Rows field invalid");

        else if (!isLegalNumber(colsString))
            showAlert("Columns field is illegal.\n" +
                    "Only numbers allowed between 5-1000.", "Columns field invalid");
        else
        {
            solutionButton.setSelected(false);
            this.solutionDisplayer.hideSolution();
            disableProperties();
            rows = Integer.valueOf(rowsString);
            columns = Integer.valueOf(colsString);
            bindDisplayers();
            String charName = player.getValue().toString();
            mazeDisplayer.setMaze(rows, columns);
            charDisplayer.setMaze(rows, columns);
            solutionDisplayer.setMaze(rows, columns);

            viewModel.generateMaze(rows, columns, charName);

        }
    }

    private void bindDisplayers(){
        this.charDisplayer.setHeight(rows);
        this.charDisplayer.setWidth(columns);
        this.charDisplayer.setCharacterName(player.getValue().toString());
        this.mazeDisplayer.widthProperty().bind(pane.widthProperty());
        this.mazeDisplayer.heightProperty().bind(pane.heightProperty());
        this.mazeDisplayer.widthProperty().addListener(event -> mazeDisplayer.redraw());
        this.mazeDisplayer.heightProperty().addListener(event -> mazeDisplayer.redraw());
        this.solutionDisplayer.widthProperty().bind(pane.widthProperty());
        this.solutionDisplayer.heightProperty().bind(pane.heightProperty());
        this.solutionDisplayer.widthProperty().addListener(event -> {if(solutionButton.isSelected())solutionDisplayer.showSolution();});
        this.solutionDisplayer.heightProperty().addListener(event -> {if(solutionButton.isSelected())solutionDisplayer.showSolution();});
        this.charDisplayer.widthProperty().bind(pane.widthProperty());
        this.charDisplayer.heightProperty().bind(pane.heightProperty());
        this.charDisplayer.widthProperty().addListener(event -> charDisplayer.redraw());
        this.charDisplayer.heightProperty().addListener(event -> charDisplayer.redraw());
    }

    @Override
    public void update(Observable o, Object arg) {
        if (o == viewModel) {
            if(arg.equals("maze-new")){
                mazeDisplayer.setGoalPosition(viewModel.getEndRow(), viewModel.getEndCol());
                mazeDisplayer.setStartPosition(viewModel.getStartRow(), viewModel.getStartCol());

                this.mazeDisplayer.setDimentions(viewModel.getMaze());
                charDisplayer.setPosition(viewModel.getCharacterPositionRow(),viewModel.getCharacterPositionColumn(), "down");
                disableProperties();
                saveButton.setDisable(false);
                solutionButton.setDisable(false);

            }
            else if(arg.equals("maze-load")){
                rows = viewModel.getMaze().length;
                columns = viewModel.getMaze()[0].length;
                mazeDisplayer.setMaze(rows, columns);
                charDisplayer.setMaze(rows, columns);
                solutionDisplayer.setMaze(rows, columns);
                bindDisplayers();
                mazeDisplayer.setGoalPosition(viewModel.getEndRow(), viewModel.getEndCol());
                mazeDisplayer.setStartPosition(viewModel.getStartRow(), viewModel.getStartCol());
                mazeDisplayer.setDimentions(viewModel.getMaze());
                player.setValue(viewModel.getCharacterName());
                charDisplayer.setPosition(viewModel.getCharacterPositionRow(),viewModel.getCharacterPositionColumn(), "down");
                disableProperties();
                solutionButton.setDisable(false);
                saveButton.setDisable(false);


            }
            else if(arg.equals("solution")) {
                this.solutionDisplayer.setMaze(rows, columns);
                this.solutionDisplayer.setSolution(viewModel.getSolutionPath());
                this.solutionDisplayer.showSolution();
                solutionButton.setDisable(false);
            }
            else if(arg.equals("movement-up")) {
               charDisplayer.setPosition(viewModel.getCharacterPositionRow(), viewModel.getCharacterPositionColumn(), "up");
                if (viewModel.getCharacterPositionRow() == viewModel.getEndRow() && viewModel.getCharacterPositionColumn() == viewModel.getEndCol())
                {
                    mediaPlayer.stop();
                    endGoalReached();
                }
            }
            else if(arg.equals("movement-down")) {
                charDisplayer.setPosition(viewModel.getCharacterPositionRow(), viewModel.getCharacterPositionColumn(), "down");
                if (viewModel.getCharacterPositionRow() == viewModel.getEndRow() && viewModel.getCharacterPositionColumn() == viewModel.getEndCol())
                {
                    mediaPlayer.stop();
                    endGoalReached();
                }
            }
            else if(arg.equals("movement-right")) {
                charDisplayer.setPosition(viewModel.getCharacterPositionRow(), viewModel.getCharacterPositionColumn(), "right");
                if (viewModel.getCharacterPositionRow() == viewModel.getEndRow() && viewModel.getCharacterPositionColumn() == viewModel.getEndCol())
                {
                    mediaPlayer.stop();
                    endGoalReached();
                }
            }
            else if(arg.equals("movement-left")) {
                charDisplayer.setPosition(viewModel.getCharacterPositionRow(), viewModel.getCharacterPositionColumn(), "left");
                if (viewModel.getCharacterPositionRow() == viewModel.getEndRow() && viewModel.getCharacterPositionColumn() == viewModel.getEndCol())
                {
                    mediaPlayer.stop();
                    endGoalReached();
                }
            }

            else if(arg.equals("movement-walk-into-wall"))
            {
                String path = getCurrentFixedPath();
                File tmpFile = new File(path + "/resources/General/walkIntoWall.mp3");

                path = tmpFile.toURI().toASCIIString();
                Media file = new Media(path);
                if (!muteAllButton.isSelected())
                {
                    walkIntoWallMediaPlayer = new MediaPlayer(file);
                    mediaPlayer.pause();

                    walkIntoWallMediaPlayer.setOnEndOfMedia(() -> {
                        if(!muteBackgroundButton.isSelected())
                            mediaPlayer.play();
                    });

                    walkIntoWallMediaPlayer.play();
                }
            }
        }
    }

    // Zoom-in and Zoom-out
    public void Scroll(ScrollEvent event)
    {
        if (viewModel.getMaze() != null && event.isControlDown())
        {
            double fact = 1.1D;

            if (event.getDeltaY() > 0.0D)
            {
                mazeDisplayer.setZoom(mazeDisplayer.getZoom() * fact);
                solutionDisplayer.setZoom(solutionDisplayer.getZoom() * fact);
                charDisplayer.setZoom(charDisplayer.getZoom() * fact);

            }
            else
            {
                mazeDisplayer.setZoom(mazeDisplayer.getZoom() / fact);
                solutionDisplayer.setZoom(solutionDisplayer.getZoom() / fact);
                charDisplayer.setZoom(charDisplayer.getZoom() / fact);
            }

            mazeDisplayer.redraw();
            if (solutionButton.isSelected())
                solutionDisplayer.showSolution();
            charDisplayer.redraw();
            event.consume();
        }
    }


    // End position reached in the maze
    private void endGoalReached()
    {
        endGoalMediaView.setPreserveRatio(true);
        mediaPlayer.pause();


        tmpStage.setOnCloseRequest(event -> {
            endGoalMediaPlayer.stop();
            if (!muteAllButton.isSelected() && !muteBackgroundButton.isSelected())
                mediaPlayer.play();

            backButtonPushed(null);

        });

        try {
            StackPane stkPane = new StackPane();
            tmpStage.setFullScreen(true);
            tmpStage.setTitle("Victory!");
            stkPane.getChildren().add(endGoalMediaView);
            Scene scene = new Scene(stkPane);
            tmpStage.setScene(scene);
            tmpStage.show();
        } catch (Exception e) {
            Server.LOG.debug("load scene to stage error");
        }
        endGoalMediaPlayer.setOnEndOfMedia(() -> {
            tmpStage.close();
            if(!muteAllButton.isSelected() && !muteBackgroundButton.isSelected())
                mediaPlayer.play();
            backButtonPushed(null);
        });
        endGoalMediaPlayer.play();

    }

    // Disable properties
    private void disableProperties() {
        solutionButton.setSelected(false);
        textField_mazeRows.setDisable(true);
        textField_mazeColumns.setDisable(true);
        generateButton.setDisable(true);
        player.setDisable(true);
    }

    // A helper function to check if a string is legal number
    private boolean isLegalNumber(String str)
    {
        if (str.length() == 0)
            return false;
        for (int i = 0; i < str.length(); i++)
            if (str.charAt(i) < '0' || str.charAt(i) > '9')
                return false;
        int num = Integer.parseInt(str);

        if (num < 5 || num > 1000)
            return false;
        return true;
    }

    // Solving the maze
    public void solveMaze() {

        solutionButton.setDisable(true);
        viewModel.solveMaze();

    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        player.getItems().addAll("Shauli", "Amatzia", "Hector", "Karako", "Avi");
        player.setValue("default");

        Image image = null;
        try {
            image = new Image(new FileInputStream(getCurrentFixedPath()+"/resources/General/logo.jpg"));
        }
        catch (Exception e) {
            Server.LOG.debug("load image exception");
        }



        if (endGoalMediaPlayer == null)
        {
            String path = getCurrentFixedPath();
            File tmpFile = new File(path + "/resources/General/endGoalReached.mp4");

            path = tmpFile.toURI().toASCIIString();
            Media file = new Media(path);

            endGoalMediaPlayer = new MediaPlayer(file);
            endGoalMediaView = new MediaView(endGoalMediaPlayer);
            tmpStage = new Stage();

        }

        logoImageView.setImage(image);

        player.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number oldValue, Number newValue) {
                charDisplayer.setCharacterName(getCharacterString(newValue.toString()));
                setCharacterImage(newValue.toString());
                playCharacterSound(newValue.toString());
            }
        });
        super.initMusic();


    }

    // Getter for the character's name from choicebox
    private String getCharacterString(String st)
    {

        String character;
        int val = Integer.parseInt(st);

        switch (val)
        {
            case 0:
                character = "Shauli";
                break;
            case 1:
                character = "Amatzia";
                break;
            case 2:
                character = "Hector";
                break;
            case 3:
                character = "Karako";
                break;
            case 4:
                character = "Avi";
                break;
            default:
                character = "Shauli";
                break;
        }

        return character;

    }

    // Setter for the character's image
    private void setCharacterImage(String st)
    {
        String character = getCharacterString(st);
        Image image = null;
        try {
            image = new Image(new FileInputStream(getCurrentFixedPath()+"/resources/Characters/" + character + "/" + character + ".jpg"));
        }
        catch (Exception e) {
        }
        imageView.setImage(image);

    }

    // Playing the character's sound
    private void playCharacterSound(String st)
    {
        String character = getCharacterString(st);
        String path = getCurrentFixedPath();
        File tmpFile;
        int i = (int)(Math.random()*3) + 1;

        if (character.equals("Karako"))
            tmpFile = new File(path + "/resources/Characters/" + character + "/" + character + ".mp3");
        else
            tmpFile = new File(path + "/resources/Characters/" + character + "/" + character + i + ".mp3");

        path = tmpFile.toURI().toASCIIString();
        Media file = new Media(path);
        if (!muteAllButton.isSelected())
        {
            characterMediaPlayer = new MediaPlayer(file);
            mediaPlayer.pause();

            characterMediaPlayer.setOnEndOfMedia(() -> {
                if(!muteBackgroundButton.isSelected())
                    mediaPlayer.play();
            });

            characterMediaPlayer.play();
        }



    }

    // Back button pushed
    public void backButtonPushed(ActionEvent actionEvent) {
        {
            FXMLLoader loader = new FXMLLoader();
            Parent mainMenu = null;
            try {
                loader.setLocation(getClass().getResource("MyView.fxml"));
                mainMenu = loader.load();
            } catch (IOException e) {

                Server.LOG.debug("fxml loader exception");
            }
            if(mainMenu!=null){
                Scene mainScene = new Scene(mainMenu);
                MyViewController controller = loader.getController();
                controller.setMuteButtons(muteAllButton.isSelected(), muteBackgroundButton.isSelected());
                controller.setStage(stage);
                controller.setScene(mainScene);
                controller.setViewModel(viewModel);
                controller.setAbout(about);
                controller.setHelp(help);
                stage.setScene(mainScene);
                stage.show();
                muteAllButtonPushed(actionEvent);
                muteBackgroundButtonPushed(actionEvent);

            }
        }
    }

    // Solution button pushed
    public void solutionButtonPushed()
    {
        MediaPlayer showSolutionMediaPlayer = null;
        if (solutionButton.isSelected())
        {
            solutionButton.setDisable(true);

            String path = getCurrentFixedPath();
            File tmpFile = new File(path + "/resources/General/showSolution.mp3");

            path = tmpFile.toURI().toASCIIString();
            Media file = new Media(path);
            if (!muteAllButton.isSelected())
            {
                showSolutionMediaPlayer = new MediaPlayer(file);
                mediaPlayer.pause();

                showSolutionMediaPlayer.setOnEndOfMedia(() -> {
                    solutionButton.setDisable(false);
                    if(!muteBackgroundButton.isSelected())
                        mediaPlayer.play();
                });

                showSolutionMediaPlayer.play();
            }

            solveMaze();
        }
        else
        {
            if (showSolutionMediaPlayer != null)
                showSolutionMediaPlayer.stop();
            this.solutionDisplayer.hideSolution();
        }
    }

    // Save button pushed
    public void saveButtonPushed(javafx.event.ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        //Set extension filter for text files
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("maze files (*.maze)", "*.maze");
        fileChooser.getExtensionFilters().add(extFilter);
        //Show save file dialog
        File file = fileChooser.showSaveDialog(stage);
        viewModel.saveGame(file);

    }
}
