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
    static ProgressBar pbBin = new ProgressBar();
    static CodageFichierTxt c;
    static CodageFichierBin cBin;

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
        mcode.setPromptText("Entrez le message Ã  coder");
        Label lbMessCle = new Label("ClÃ© de codage : ");
        Label lbMessCod = new Label("Message codÃ© : ");
        Label lbCle = new Label();
        Label lbCodage = new Label();

        pb.setProgress(0);
        pbBin.setProgress(0);

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Resource File");
        
        FileChooser fileChooserDeck = new FileChooser();
        fileChooserDeck.setTitle("Choisir fichier cartes");
        
        FileChooser binChooser = new FileChooser();
        binChooser.setTitle("Choisir une image");
        
        FileChooser binChooserDeck = new FileChooser();
        fileChooserDeck.setTitle("Choisir fichier cartes");

        Label lbMessDecCle = new Label("ClÃ© de dÃ©codage : ");
        Label lbMessDec = new Label("Message dÃ©codÃ© : ");
        Label lbCleDec = new Label();
        Label lbDecodage = new Label();
        Label lbPath = new Label();
        Label lbPathBin = new Label();
        Label infoState = new Label();
        Label infoStateBin = new Label();
        Button submit = new Button("Encrypter");
        Button chooseFile = new Button("SÃ©lectionner un fichier");
        Button chooseBin = new Button("SÃ©lectionner un fichier");
        Button coder = new Button("Coder");
        coder.setDisable(true);
        Button decoder = new Button("Decoder");
        decoder.setDisable(true);
        Button coderBin = new Button("Coder");
        coderBin.setDisable(true);
        Button decoderBin = new Button("DÃ©coder");
        decoderBin.setDisable(true);
        Group root = new Group();
        Scene scene = new Scene(root, 500, 400, Color.GAINSBORO);
        BorderPane borderPane = new BorderPane();
        borderPane.prefHeightProperty().bind(scene.heightProperty());
        borderPane.prefWidthProperty().bind(scene.widthProperty());
        TabPane tabPane = new TabPane();
        Tab tab = new Tab("Champ de texte");
        Tab tabFile = new Tab("Fichier");
        Tab tabImg = new Tab("Fichier binaire");


        pb.prefWidthProperty().bind(scene.heightProperty());
        pbBin.prefWidthProperty().bind(scene.heightProperty());
        HBox textBox = new HBox(mcode);
        HBox cleBox = new HBox(lbMessCle,lbCle);
        HBox messCodBox = new HBox(lbMessCod,lbCodage);
        HBox cleDecBox = new HBox(lbMessDecCle,lbCleDec);
        HBox messDecBox = new HBox(lbMessDec, lbDecodage);
        HBox btns = new HBox(chooseFile,coder,decoder);
        HBox btnsBin = new HBox(chooseBin, coderBin, decoderBin);

        VBox box = new VBox(textBox, submit, cleBox, messCodBox, cleDecBox, messDecBox);
        VBox boxFile = new VBox(pb,lbPath,btns,infoState);
        VBox boxBin = new VBox(pbBin, lbPathBin, btnsBin, infoStateBin);
        box.setPadding(new Insets(10,10,10,10));
        box.setSpacing(10);
        box.prefHeightProperty().bind(scene.heightProperty());
        box.prefWidthProperty().bind(scene.widthProperty());
        boxFile.setPadding(new Insets(10,10,10,10));
        boxFile.setSpacing(10);
        boxFile.prefHeightProperty().bind(scene.heightProperty());
        boxFile.prefWidthProperty().bind(scene.widthProperty());
        boxBin.setPadding(new Insets(10));
        boxBin.setSpacing(10);
        boxBin.prefHeightProperty().bind(scene.heightProperty());
        boxBin.prefWidthProperty().bind(scene.widthProperty());
        tab.setContent(box);
        tabFile.setContent(boxFile);
        tabImg.setContent(boxBin);
        tabPane.setTabClosingPolicy(TabClosingPolicy.UNAVAILABLE);
        tabPane.getTabs().add(tab);
        tabPane.getTabs().add(tabFile);
        tabPane.getTabs().add(tabImg);
        borderPane.setCenter(tabPane);

        root.getChildren().add(borderPane);

        primaryStage.setScene(scene);

        /**
         * EVENTS
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
        
        /**
         * Sélection de fichier: - Si le fichier contient "_code" alors c'est un fichier codé et on pourra alors seulement le décoder
         * 						 - Sinon c'est un fichier à coder
         */
        chooseFile.setOnAction(new EventHandler<ActionEvent>() {
        	
            public void handle(ActionEvent event) {
            	//On désactive les boutons pour coder et décoder
            	coder.setDisable(true);
            	decoder.setDisable(true);
            	//On choisi le fichier
                FileChooser.ExtensionFilter extFilter = 
                    new FileChooser.ExtensionFilter("TEXT files (*.txt)", "*.txt");
                fileChooser.getExtensionFilters().add(extFilter);
                File f = fileChooser.showOpenDialog(primaryStage);
                
                //Si le fichier existe
                if (f != null) {
                	//On instancie CodageFichierTxt
                    c = new CodageFichierTxt(f);  
                    //Si le fichier contient "_code" alors on active seulement le bouton décoder car on ne pourra pas coder
                    if(f.getName().contains("_code")) {
                    	System.out.println("Fichier codé chargé");
                    	//On set le message codé
                    	c.setMCodeFromFile(f);
                    	decoder.setDisable(false);
                    }else {
                    	//sinon on initialise le codage
                    	c.init();
                    	System.out.println();
                    	coder.setDisable(false);     
                    }
                    
                    //on indique quel fichier a été séléctionné
                    lbPath.setText(f.getPath());
                    
                }    
            }
        });
        
        /**
         * Codage: on utilise ici un thread afin de ne pas bloquer l'affichage car le codage peut être long pour les fichiers lourds
         */
        coder.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent arg0) {
            // TODO Auto-generated method stub
            	//on définit un nouveau thread dans lequel on va faire nos opérations et mettre a jour la barre de progression
                Task<Void> task = new Task<Void>() {
                    @Override protected Void call() throws Exception {
                    	//MAJ de la barre de progression
                        pb.progressProperty().unbind();
                        pb.progressProperty().bind(c.progressProperty());
			c.progressProperty().addListener((obs, oldProgress, newProgress)
                                ->updateProgress(newProgress.doubleValue(), 1));						
                        c.codage();
                        c.saveCodeToFile();

                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                infoState.setText("Fichier codÃ© avec succÃ©s !");
                            }
                        });
                        return null;
                    }
                };
				//Instanciation du thread
                Thread th = new Thread(task);
                //On lance le thread
                th.start();
				coder.setDisable(true);
				decoder.setDisable(false);
            }	 
        });
        
        /**
         * Décodage: De même que pour le codage on utilise un thread et une barre de progression. On récupère aussi le fichier contenant le paquet de cartes
         */
        decoder.setOnAction(new EventHandler<ActionEvent>() {
            @Override
               public void handle(ActionEvent arg0) {
            		//Récupération du fichier contenant le paquet de cartes.
                    FileChooser.ExtensionFilter extFilter = 
                            new FileChooser.ExtensionFilter("TEXT files (*.txt)", "*.txt");
                    File f = fileChooserDeck.showOpenDialog(primaryStage);
           
                    c.setFname(f.getName());
                    
                    Task<Void> task = new Task<Void>() {
                        @Override protected Void call() throws Exception {
                            pb.progressProperty().unbind();
                            pb.progressProperty().bind(c.progressProperty());

                            c.progressProperty().addListener((obs, oldProgress, newProgress)
                                    ->updateProgress(newProgress.doubleValue(), 1));

                            c.decodage();
                            c.saveDecodeToFile();
                           

                            Platform.runLater(new Runnable() {
                                @Override
                                public void run() {
                                    infoState.setText("Fichier dï¿½codÃ© avec succÃ©s !");
                                }
                            });

                            return null;
                        }
                    };

                    Thread th = new Thread(task);
                    th.start();
                    decoder.setDisable(true);
                }
         	 
        });
        
        //Même principe que pour les fichiers textes
        chooseBin.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {    
            	coderBin.setDisable(true);
                decoderBin.setDisable(true);
                FileChooser.ExtensionFilter extFilter = 
                        new FileChooser.ExtensionFilter("BINARY files", "*.jpeg", "*.jpg", "*.gif", "*.mp3", "*.mp4", "*.avi", "*.flv", "*.mkv");
                    binChooser.getExtensionFilters().add(extFilter);
                File f = binChooser.showOpenDialog(primaryStage);
                
                if (f != null) {                
                    cBin = new CodageFichierBin(f);                    
                    if(f.getName().contains("_code")) {
                    	System.out.println("Fichier codé chargé");
                    	cBin.setMCodeFromFile(f);
                    	decoderBin.setDisable(false);
                    }else {
                    	cBin.init();
                    	System.out.println();
                    	coderBin.setDisable(false);     
                    }
                    lbPathBin.setText(f.getPath());
                    
                }
            }
        });
        
        //Même principe que pour les fichiers textes
        coderBin.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent arg0) {
                // TODO Auto-generated method stub
                Task<Void> task = new Task<Void>() {
                    @Override protected Void call() throws Exception {
                        pbBin.progressProperty().unbind();
                        pbBin.progressProperty().bind(cBin.progressProperty());
                        
                        cBin.progressProperty().addListener((obs, oldProgress, newProgress)
                                ->updateProgress(newProgress.doubleValue(), 1));						
                        
                        cBin.codage();
                        cBin.saveCodeBinToFile();

                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                infoStateBin.setText("Fichier codÃ© avec succÃ©s !");
                            }
                        });

                        return null;
                    }
                };
                
                Thread th = new Thread(task);
                th.start();
                coderBin.setDisable(true);
                decoderBin.setDisable(false);
        }


       });
         
         
        //Même principe que pour les fichiers textes
        decoderBin.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent arg0) {
                File f = binChooserDeck.showOpenDialog(primaryStage);

                cBin.setFname(f.getName());
                Task<Void> task = new Task<Void>() {
                    @Override protected Void call() throws Exception {
                        pbBin.progressProperty().unbind();
                        pbBin.progressProperty().bind(cBin.progressProperty());
                        
                        cBin.progressProperty().addListener((obs, oldProgress, newProgress)
                                ->updateProgress(newProgress.doubleValue(), 1));

                        cBin.decodage();
                        cBin.saveDecodeBinToFile();

                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                infoStateBin.setText("Fichier dï¿½codÃ© avec succÃ©s !");
                            }
                        });

                        return null;
                    }
                };
                
                
                Thread th = new Thread(task);
                th.start();
                decoderBin.setDisable(true);
             }
        });
         
         primaryStage.show();
    }
}
