package View;

import Server.Server;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;
import ViewModel.MyViewModel;

import java.io.*;
import java.net.URL;
import java.nio.file.FileSystems;
import java.util.*;

import static java.lang.System.exit;

public class FatherController implements Initializable, Observer {

    @FXML protected CheckMenuItem muteAllButton;
    @FXML protected CheckMenuItem muteBackgroundButton;
    @FXML protected MenuBar menuBar;
    protected Stage stage;
    protected Scene scene;
    protected MyViewModel viewModel;
    protected static MediaPlayer mediaPlayer = null;
    protected String about;
    protected String help;

    // Setter for the about's window
    public void setAbout(String about) {
        this.about = about;
    }

    // Setter for the help's window
    public void setHelp(String help) {
        this.help = help;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
    }

    // Setter for the view model
    public void setViewModel(MyViewModel viewModel){
        this.viewModel = viewModel;
    }

    // New game button pushed
    public void newGameButtonPushed (javafx.event.ActionEvent actionEvent) {
        {
            Parent game = null;
            FXMLLoader loader = new FXMLLoader();
            try {
                loader.setLocation(getClass().getResource("GameView.fxml"));
                game = loader.load();
            } catch (IOException e) {
                Server.LOG.debug("fxml loader exception");
            }

            Scene gameScene = new Scene(game);
            GameViewController controller = loader.getController();

            controller.setMuteButtons(muteAllButton.isSelected(), muteBackgroundButton.isSelected());
            controller.setStage(stage);
            controller.setScene(gameScene);
            controller.setViewModel(viewModel);
            viewModel.addObserver(controller);
            controller.setAbout(about);
            controller.setHelp(help);
            stage.setOnCloseRequest(event -> {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION,"Do you wish to exit?");
                alert.setHeaderText("Exit");
                DialogPane dialogPane = alert.getDialogPane();
                dialogPane.getStylesheets().add(getClass().getResource("myDialog.css").toExternalForm());
                dialogPane.getStyleClass().add("myDialog");
                Optional<ButtonType> result = alert.showAndWait();
                if (result.get() == ButtonType.OK){
                    viewModel.close();
                } else {
                    event.consume();
                }
            });
            gameScene.setOnKeyPressed(event -> {
                viewModel.moveCharacter(event.getCode());
                event.consume();
            });
            stage.setScene(gameScene);
            stage.show();

        }
    }

    // Setter for the mute buttons
    protected void setMuteButtons(boolean muteAll, boolean muteBackground)
    {
        muteAllButton.setSelected(muteAll);
        muteBackgroundButton.setSelected(muteBackground);

    }

    // Setter for the stage
    public void setStage(Stage primaryStage){
        stage = primaryStage;
    }

    // Setter for the scene
    public void setScene(Scene currScene){
        scene = currScene;
    }

    // Showing the alert with a specific CSS style
    protected void showAlert(String alertMessage, String alertHeader) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.getStylesheets().add(getClass().getResource("myDialog.css").toExternalForm());
        dialogPane.getStyleClass().add("myDialog");

        alert.setHeaderText(alertHeader);
        alert.setContentText(alertMessage);
        alert.show();
    }

    // About button pushed
    public void aboutButtonPushed(javafx.event.ActionEvent actionEvent) {

        showAlert(about, "About");
    }

    // Help button pushed
    public void helpButtonPushed(ActionEvent actionEvent)
    {
        showAlert(help, "Help");
    }

    // Getting the current path
    protected String getCurrentFixedPath()
    {
        String absoluteCurrentPath = FileSystems.getDefault().getPath(".").toAbsolutePath().toString();
        absoluteCurrentPath = absoluteCurrentPath.substring(0, absoluteCurrentPath.length()-2);
        String fixedPath = "";

        for (int i = 0; i < absoluteCurrentPath.length(); i++)
        {
            if (absoluteCurrentPath.charAt(i) == '\\')
                fixedPath = fixedPath + "/";
            else
                fixedPath = fixedPath + absoluteCurrentPath.charAt(i);
        }

        return fixedPath;
    }

    // Initiating the music
    protected void initMusic()
    {
        if (mediaPlayer == null)
        {
            String path = getCurrentFixedPath();
            File tmpFile = new File(path + "/resources/General/Opener.mp3");
            path = tmpFile.toURI().toASCIIString();
            Media file = new Media(path);
            mediaPlayer = new MediaPlayer(file);
        }

    }

    // Mute all button pushed
    public void muteAllButtonPushed(javafx.event.ActionEvent actionEvent) {

        if(muteAllButton.isSelected() || muteBackgroundButton.isSelected()){
            mediaPlayer.stop();
        }
        else{
            mediaPlayer.setOnEndOfMedia(new Runnable() {
                public void run() {
                    mediaPlayer.seek(Duration.ZERO);
                }
            });
            mediaPlayer.play();
        }
    }

    // Mute background music button pushed
    public void muteBackgroundButtonPushed(javafx.event.ActionEvent actionEvent) {

        if(muteAllButton.isSelected() || muteBackgroundButton.isSelected()){
            mediaPlayer.stop();
        }
        else{
            mediaPlayer.setOnEndOfMedia(new Runnable() {
                public void run() {
                    mediaPlayer.seek(Duration.ZERO);
                }
            });
            mediaPlayer.play();
        }
    }

    // Load button pushed
    public void loadButtonPushedMainMenu(javafx.event.ActionEvent actionEvent) {
        newGameButtonPushed(actionEvent);
        FileChooser fc = new FileChooser();
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("maze files", "*.maze"));
        File f = fc.showOpenDialog(null);
        viewModel.loadGame(f);

    }

    // Properties button pushed
    public void PropertiesButtonPushed(javafx.event.ActionEvent actionEvent){

        Properties prop = null;

        try {
            String current = getCurrentFixedPath();
            InputStream input = new FileInputStream(current + "/resources/config.properties");
            prop = new Properties();
            prop.load(input);

        } catch (Exception e) {
            Server.LOG.debug("GetProperty Error, Class: GameViewController");
        }

        if (prop != null)
        {
            String maxThreadsPool = prop.getProperty("maxPoolSize");
            String generator = prop.getProperty("MazeGenerator");
            String solver = prop.getProperty("SearchingAlgorithm");

            String res = "Properties\n\n\n" +
                    "maxPoolSize :\t\t" + maxThreadsPool +"\n\n" +
                    "MazeGenerator :\t\t" + generator + "\n\n" +
                    "SearchingAlgorithm :\t" + solver;

            showAlert(res, "Properties");
        }

    }

    @Override
    public void update(Observable o, Object arg) {

    }

    // Exit button pushed
    public void exitButtonPushed(ActionEvent actionEvent){

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION,"Do you wish to exit?");
        alert.setHeaderText("Exit");
        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.getStylesheets().add(getClass().getResource("myDialog.css").toExternalForm());
        dialogPane.getStyleClass().add("myDialog");
        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK){
            viewModel.close();
            exit(0);
            } else {
            actionEvent.consume();
            }
    }
}
