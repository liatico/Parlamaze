package View;

import Server.Server;
import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Duration;
import java.io.FileInputStream;
import java.net.URL;
import java.util.ResourceBundle;

public class MyViewController extends FatherController implements IView {

    @FXML private ImageView imageView;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Image image = null;
        try {
            image = new Image(new FileInputStream(getCurrentFixedPath()+"/resources/General/background.jpg"));
        }
        catch (Exception e) {
            Server.LOG.debug("image loader exception");
        }


        imageView.setImage(image);
        startMusic();
    }

    // Starting the music
    private void startMusic()
    {
        initMusic();

        mediaPlayer.setOnEndOfMedia(new Runnable() {
            public void run() {
                mediaPlayer.seek(Duration.ZERO);
            }
        });
        mediaPlayer.play();
    }




}
