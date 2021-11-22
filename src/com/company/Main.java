package com.company;

import com.company.Controllers.MainController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.Group;
import javafx.scene.text.Text;

import java.io.IOException;

public class Main extends Application{

    private Stage primaryStage;
    private AnchorPane rootLayout;

    public static void main(String[] args) {
       Application.launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        this.primaryStage = primaryStage;
        this.primaryStage.setTitle("Perceptron");
        showBaseWindow();
    }
    public void showBaseWindow() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(Main.class.getResource("/maket/MainWindow.fxml"));
            rootLayout = FXMLLoader.load(getClass().getResource("maket/MainWindow.fxml"));
            Scene scene = new Scene(rootLayout);
            primaryStage.setScene(scene);

            MainController controller = loader.getController();
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}