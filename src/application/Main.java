package application;

import java.io.File;

import javafx.application.Application;
import javafx.application.Platform;
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
import javafx.concurrent.Task;

public class Main extends Application{
	static ProgressBar pb = new ProgressBar();
	static CodageFichierTxt c;
        static CodageFichierImg cImg;
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
        
        FileChooser imgChooser = new FileChooser();
        imgChooser.setTitle("Choisir une image");

        Label lbMessDecCle = new Label("Clé de décodage : ");
        Label lbMessDec = new Label("Message décodé : ");
        Label lbCleDec = new Label();
        Label lbDecodage = new Label();
        Label lbPath = new Label();
        Label infoState = new Label();
        Button submit = new Button("Encrypter");
        Button chooseFile = new Button("Sélectionner un fichier");
        Button chooseImg = new Button("Sélectionner une image");
        Button coder = new Button("Coder");
        coder.setDisable(true);
        Button decoder = new Button("Decoder");
        decoder.setDisable(true);
        Group root = new Group();
        Scene scene = new Scene(root, 500, 400, Color.GAINSBORO);
        BorderPane borderPane = new BorderPane();
        borderPane.prefHeightProperty().bind(scene.heightProperty());
        borderPane.prefWidthProperty().bind(scene.widthProperty());
        TabPane tabPane = new TabPane();
        Tab tab = new Tab("Champ de texte");
        Tab tabFile = new Tab("Fichier");
        Tab tabImg = new Tab("Image");


        pb.prefWidthProperty().bind(scene.heightProperty());
        HBox textBox = new HBox(mcode);
        HBox cleBox = new HBox(lbMessCle,lbCle);
        HBox messCodBox = new HBox(lbMessCod,lbCodage);
        HBox cleDecBox = new HBox(lbMessDecCle,lbCleDec);
        HBox messDecBox = new HBox(lbMessDec, lbDecodage);
        HBox btns = new HBox(chooseFile,coder,decoder);

        VBox box = new VBox(textBox, submit, cleBox, messCodBox, cleDecBox, messDecBox);
        VBox boxFile = new VBox(pb,lbPath,btns,infoState);
        VBox boxImg = new VBox(chooseImg);
        box.setPadding(new Insets(10,10,10,10));
        box.setSpacing(10);
        box.prefHeightProperty().bind(scene.heightProperty());
        box.prefWidthProperty().bind(scene.widthProperty());
        boxFile.setPadding(new Insets(10,10,10,10));
        boxFile.setSpacing(10);
        boxFile.prefHeightProperty().bind(scene.heightProperty());
        boxFile.prefWidthProperty().bind(scene.widthProperty());
        boxImg.setPadding(new Insets(10));
        boxImg.setSpacing(10);
        boxImg.prefHeightProperty().bind(scene.heightProperty());
        boxImg.prefWidthProperty().bind(scene.widthProperty());
        tab.setContent(box);
        tabFile.setContent(boxFile);
        tabImg.setContent(boxImg);
        tabPane.setTabClosingPolicy(TabClosingPolicy.UNAVAILABLE);
        tabPane.getTabs().add(tab);
        tabPane.getTabs().add(tabFile);
        tabPane.getTabs().add(tabImg);
        borderPane.setCenter(tabPane);

        root.getChildren().add(borderPane);

        primaryStage.setScene(scene);
        
        File f = null;

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
            	 FileChooser.ExtensionFilter extFilter = 
                         new FileChooser.ExtensionFilter("TEXT files (*.txt)", "*.txt");
                 fileChooser.getExtensionFilters().add(extFilter);
                 File f = fileChooser.showOpenDialog(primaryStage);
                 
                 
                 if (f != null) {
                	 
                     c = new CodageFichierTxt(f);
                     c.init();
                     System.out.println();
                     coder.setDisable(false);
                     decoder.setDisable(false);
                     lbPath.setText(f.getPath());
                 }
                 
             }
         });
         
         coder.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent arg0) {
				// TODO Auto-generated method stub
				Task<Void> task = new Task<Void>() {
					@Override protected Void call() throws Exception {
						pb.progressProperty().unbind();
						pb.progressProperty().bind(c.progressProperty());
						
		
						c.progressProperty().addListener((obs, oldProgress, newProgress) 
						->updateProgress(newProgress.doubleValue(), 1));
						
						c.codage();
						c.saveCodeToFile();
						
						Platform.runLater(new Runnable() {
						    @Override
						    public void run() {
						    	infoState.setText("Fichier codé avec succés !");
						    }
						});
						return null;
					}
				};
				
				Thread th = new Thread(task);
				th.start();
				
			}
        	 
        	 
         });
         
         decoder.setOnAction(new EventHandler<ActionEvent>() {

 			@Override
 			public void handle(ActionEvent arg0) {

				c.decodage();
				c.saveDecodeToFile();
				
				infoState.setText("Fichier décodé avec succés !");
				
 			}
         	 
         	 
          });
         
         chooseImg.setOnAction(new EventHandler<ActionEvent>() {
             public void handle(ActionEvent event) {
            	 /*FileChooser.ExtensionFilter extFilter = 
                         new FileChooser.ExtensionFilter("TEXT files (*.txt)", "*.txt");
                 fileChooser.getExtensionFilters().add(extFilter);*/
                 File f = fileChooser.showOpenDialog(primaryStage);
                 
                 
                 if (f != null) {
                     lbPath.setText(f.getPath());
                     cImg = new CodageFichierImg(f);
                     cImg.init();
                     cImg.codage();
                     cImg.decodage();
                     cImg.saveImgToFile();
                     
                     /*coder.setDisable(false);
                     decoder.setDisable(false);*/
                 }
                 
             }
         });
         
         primaryStage.show();
    }
}
