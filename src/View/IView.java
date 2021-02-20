package View;

import ViewModel.MyViewModel;
import javafx.event.ActionEvent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public interface IView {
    // New game button pushed
    void newGameButtonPushed (javafx.event.ActionEvent actionEvent);

    // Setter for the stage
    void setStage(Stage primaryStage);

    // Setter for the scene
    void setScene(Scene currScene);

    // About button pushed
    void aboutButtonPushed(javafx.event.ActionEvent actionEvent);

    // Help button pushed
    void helpButtonPushed(ActionEvent actionEvent);

    // Load button pushed
    void loadButtonPushedMainMenu(javafx.event.ActionEvent actionEvent);

    // Properties button pushed
    void PropertiesButtonPushed(javafx.event.ActionEvent actionEvent);

    // Setter for the view model
    void setViewModel(MyViewModel viewModel);

    // Setter for the about window
    void setAbout(String about);

    // Setter for the help window
    void setHelp(String help);
}
