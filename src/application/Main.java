package application;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TabPane.TabClosingPolicy;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class Main extends Application{
	static ProgressBar pb = new ProgressBar();
	public static void main(String[] args) {
        Application.launch(args);
    }

    @Override
    public void start(Stage primaryStage) {


        /*
        * Initialisation de JavaFX et de l'interface
        */
        primaryStage.setTitle("Codage de Schneier");
        TextArea mcode = new TextArea();
        mcode.setPromptText("Entrez le message à coder");
        Label lbMessCle = new Label("Clé de codage : ");
        Label lbMessCod = new Label("Message codé : ");
        Label lbCle = new Label();
        Label lbCodage = new Label();

        pb.setProgress(0);

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Resource File");

        Label lbMessDecCle = new Label("Clé de décodage : ");
        Label lbMessDec = new Label("Message décodé : ");
        Label lbCleDec = new Label();
        Label lbDecodage = new Label();
        Button submit = new Button("Encrypter");
        Button chooseFile = new Button("Charger");
        Group root = new Group();
        Scene scene = new Scene(root, 500, 400, Color.GAINSBORO);
        BorderPane borderPane = new BorderPane();
        borderPane.prefHeightProperty().bind(scene.heightProperty());
        borderPane.prefWidthProperty().bind(scene.widthProperty());
        TabPane tabPane = new TabPane();
        Tab tab = new Tab("Champ de texte");
        Tab tabFile = new Tab("Fichier");


        pb.prefWidthProperty().bind(scene.heightProperty());
        HBox textBox = new HBox(mcode);
        HBox cleBox = new HBox(lbMessCle,lbCle);
        HBox messCodBox = new HBox(lbMessCod,lbCodage);
        HBox cleDecBox = new HBox(lbMessDecCle,lbCleDec);
        HBox messDecBox = new HBox(lbMessDec, lbDecodage);

        VBox box = new VBox(textBox, submit, cleBox, messCodBox, cleDecBox, messDecBox);
        VBox boxFile = new VBox(pb,chooseFile);
        box.setPadding(new Insets(10,10,10,10));
        box.setSpacing(10);
        box.prefHeightProperty().bind(scene.heightProperty());
        box.prefWidthProperty().bind(scene.widthProperty());
        boxFile.setPadding(new Insets(10,10,10,10));
        boxFile.setSpacing(10);
        boxFile.prefHeightProperty().bind(scene.heightProperty());
        boxFile.prefWidthProperty().bind(scene.widthProperty());
        tab.setContent(box);
        tabFile.setContent(boxFile);
        tabPane.setTabClosingPolicy(TabClosingPolicy.UNAVAILABLE);
        tabPane.getTabs().add(tab);
        tabPane.getTabs().add(tabFile);
        borderPane.setCenter(tabPane);

        root.getChildren().add(borderPane);

        primaryStage.setScene(scene);

        /*
         * Events
         */

         submit.setOnAction(new EventHandler<ActionEvent>() {

             public void handle(ActionEvent event) {
                 Codage c = new Codage(mcode.getText());
                 c.init();
                 c.codage();
                 c.decodage();
                 lbCle.setText(c.getCleStr());
                 lbCodage.setText(c.getMessageCode());
                 lbCleDec.setText(c.getCleStr());
                 lbDecodage.setText(c.getMessageDecode());
             }
         });
         chooseFile.setOnAction(new EventHandler<ActionEvent>() {
             public void handle(ActionEvent event) {
                 File f = fileChooser.showOpenDialog(primaryStage);
                 CodageFichierTxt c = new CodageFichierTxt(f);
                 c.init();
                 c.codage();
                 c.decodage();
                 c.saveCodeToFile();
                 c.saveDecodeToFile();
             }
         });
         
         primaryStage.show();
    }
}
