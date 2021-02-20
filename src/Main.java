import Model.MyModel;
import View.IView;
import ViewModel.MyViewModel;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogPane;
import javafx.stage.Stage;
import java.io.IOException;
import java.util.Observer;
import java.util.Optional;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{

        //ViewModel -> Model
        MyModel model = new MyModel();
        model.startServers();
        MyViewModel viewModel = new MyViewModel(model);
        model.addObserver(viewModel);

        FXMLLoader loader = new FXMLLoader();
        Parent mainMenu = null;
        try {
            loader.setLocation(getClass().getResource("View/MyView.fxml"));
            mainMenu = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }



        primaryStage.setTitle("The Parlamaze");
        primaryStage.setMinHeight(600);
        primaryStage.setMinWidth(650);
        Scene scene = new Scene(mainMenu);
        IView view = loader.getController();
        String about = "Welcome to the Parlamaze!\n\n\n" +
                "Programmed by: Liat Cohen & Adir Biran on June 2019\n\n\n" +

                "The maze is created by MyMazeGenerator using prim's algorithm.\n" +

                "Maze's solving is done by BestFirstSearch algorithm, an extended version of BFS algorithm with priorities.\n";

        String help = "Welcome to the Parlamaze!\n\n\n" +
            "The Parlamaze is a game simulating a maze in which you can choose between 5 different characters.\n" +
            "The goal of the game is to help the character go from the house to the coffee table for the Parlamaze meeting.\n" +
            "You can use a hint by checking the 'Show Solution' checkbox.\n\n\n" +

            "The character can move to the sides using the arrows or numpad's arrows.\n" +
            "The character can go diagonal using 1,3,7,9 buttons on the numpad.\n" +
            "Pay attention - the Num Lock button must be on.\n";

        view.setAbout(about);
        view.setHelp(help);
        //view.setResizeEvent(scene);
        view.setScene(scene);
        view.setStage(primaryStage);
        view.setViewModel(viewModel);
        if(view instanceof Observer){
            viewModel.addObserver((Observer) view);
        }

        //scene.getStylesheets().add(getClass().getResource("MainStyle.css").toExternalForm());
        primaryStage.setScene(scene);


        primaryStage.setOnCloseRequest(event -> {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION,"Do you wish to exit?");
            alert.setHeaderText("Exit");
            DialogPane dialogPane = alert.getDialogPane();
            dialogPane.getStylesheets().add(getClass().getResource("View/myDialog.css").toExternalForm());
            dialogPane.getStyleClass().add("myDialog");
            Optional<ButtonType> result = alert.showAndWait();
            if (result.get() == ButtonType.OK){
                model.close();
                // implement close

            } else {
                event.consume();
            }
        });
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
