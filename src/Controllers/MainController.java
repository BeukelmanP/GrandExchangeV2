/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Controllers;

import Classes.CategoryEnum;
import Interfaces.IAuction;
import Interfaces.IAuctionInfo;
import fontyspublisher.IRemotePropertyListener;
import grandexchange.RegistryManager;
import java.beans.PropertyChangeEvent;
import java.io.IOException;
import java.net.URL;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Collections;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;
import javax.swing.JOptionPane;

/**
 * FXML Controller class
 *
 * @author piete
 */
public class MainController extends UnicastRemoteObject implements Initializable, IRemotePropertyListener {

    @FXML
    private ScrollPane auctionsPane;
    @FXML
    private ImageView loggedInUserImage;
    @FXML
    private ComboBox<CategoryEnum> comboBoxCategory;
    @FXML
    private ListView lstCategory;

    private RegistryManager RM;
    private IAuctionInfo auctionInfoInterface;
    private IAuction auctionInterface;

    /**
     * Initializes the controller class.
     *
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }

    public MainController() throws RemoteException {
    }
    
    public void setUp(RegistryManager RM) throws RemoteException {
        this.RM = RM;
        RM.getAuctionInterface();
        this.auctionInterface = RM.getAuction();
        this.auctionInterface.subscribe(this, "newauction");
        this.auctionInterface.subscribe(this, "currentpricechange");

        try {
            loggedInUserImage.setImage(new Image(RM.getUser().getImageURL()));
        } catch (NullPointerException ex) {
            loggedInUserImage.setImage(new Image(this.getClass().getResource("/Classes/unavailable.jpg").toExternalForm()));

            System.out.println("LoggedInUser doesn't have an imageURL yet");
        }
        comboBoxCategory.getItems().setAll(CategoryEnum.values());
    }

    @FXML
    public void refreshAuctions() throws RemoteException {
        Task taskCalc = new Task<Pane>() {
            @Override
            public Pane call() throws RemoteException {
                //get auctions               
                long start = System.currentTimeMillis();
                ArrayList<Integer> auctionIDs = RM.getAuction().getAuctionIds();
                long elapsedTime = System.currentTimeMillis() - start;
                System.out.println("PerformanceGetAuctionsInMS=" + elapsedTime);

                //draw auctions
                start = System.currentTimeMillis();
                Pane allAuctions = new Pane();
                allAuctions.setPrefWidth(800);
                allAuctions.setPrefHeight(150 * auctionIDs.size());
                for (Integer i : auctionIDs) {
                    auctionInfoInterface = RM.getAuction().getIAuctionInterface(i);

                    Pane Auction = new Pane();
                    Auction.setPrefWidth(800);
                    Auction.setPrefHeight(150);
                    Auction.relocate(0, 150 * i);
                    if ((i % 2) == 0) {
                        Auction.setStyle("-fx-background-color: lightgrey ");
                    }
                    Label productName = new Label();
                    productName.setText(auctionInfoInterface.getProductName());
                    productName.setFont(new Font("Arial", 25));
                    productName.relocate(150, 25);

                    Label price = new Label();
                    price.setText("€" + auctionInfoInterface.getCurrentPrice());
                    price.setFont(new Font("Arial", 20));
                    price.relocate(550, 120);

                    Label seller = new Label();
                    seller.setText(auctionInfoInterface.getSellerName());
                    seller.setFont(new Font("Arial", 15));
                    seller.relocate(550, 20);

                    TextArea description = new TextArea();
                    description.setPrefSize(200, 60);
                    description.relocate(150, 65);
                    description.setText(auctionInfoInterface.getDescription());
                    description.wrapTextProperty().setValue(Boolean.TRUE);
                    description.setEditable(false);

                    //setting image of auction
                    ImageView image;
                    try {
                        image = new ImageView(new Image(auctionInfoInterface.getImageURLs()[0]));
                    } catch (Exception ex) {
                        image = new ImageView(new Image(this.getClass().getResource("/Classes/unavailable.jpg").toExternalForm()));
                    }
                    image.setFitWidth(100);
                    image.setFitHeight(100);
                    image.relocate(25, 25);
                    image.addEventHandler(MouseEvent.MOUSE_CLICKED,
                            new EventHandler<MouseEvent>() {
                        @Override
                        public void handle(MouseEvent e) {
                            try {
                                showAuction(auctionInfoInterface);
                            } catch (IOException ex) {
                                Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        }
                    });

                    Auction.getChildren().addAll(productName, image, price, seller, description);
                    allAuctions.getChildren().add(Auction);
                }
                elapsedTime = System.currentTimeMillis() - start;
                System.out.println("PerformanceSetAuctionsPaneInMS=" + elapsedTime);

                return allAuctions;
            }
        };

        new Thread(() -> {
            Platform.runLater(() -> {
                try {
                    Thread auctionCalcThread = new Thread(taskCalc);
                    auctionCalcThread.start();
                    auctionsPane.setContent((Pane) taskCalc.get());
                } catch (InterruptedException | ExecutionException ex) {
                    Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, ex);
                }
            });
        }).start();
    }

    public void showAuction(IAuctionInfo auctionInfoInterface) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/Views/Auction.fxml"));
        Scene newScene;
        newScene = new Scene(loader.load());
        AuctionController controller = loader.<AuctionController>getController();
        controller.setUp(auctionInfoInterface, this.RM);
        Stage inputStage = new Stage();
        inputStage.getIcons().add(new Image("/Icon/scale.png"));
        inputStage.setScene(newScene);
        inputStage.setOnCloseRequest((WindowEvent we) -> {
            System.out.println("Stage X Disabled");
            we.consume();
            JOptionPane.showMessageDialog(null, "Please use the close button located on the top left of the screen");
        });
        inputStage.showAndWait();
    }

    public void CategorySelected(Event E) {
        ComboBox C = (ComboBox) E.getSource();
        int i = C.getSelectionModel().getSelectedIndex();

        if (!lstCategory.getItems().contains(CategoryEnum.values()[i])) {
            lstCategory.getItems().add(CategoryEnum.values()[i]);

            Collections.sort(lstCategory.getItems());
        }
    }

    public void CategoryDelete(Event E) {
        int selected = lstCategory.getSelectionModel().getSelectedIndex();
        if (selected >= 0 && selected < lstCategory.getItems().size()) {
            lstCategory.getItems().remove(selected);
        }
    }

    @FXML
    public void button_Logout() throws IOException {
        RM.getAuthorization().logout(RM.getUser().getUsername());
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/Views/Login.fxml"));
        Parent root = loader.load();
        Stage newStage = new Stage();
        newStage.getIcons().add(new Image("/Icon/scale.png"));

        newStage.initStyle(StageStyle.TRANSPARENT);
        newStage.setScene(new Scene(root, Color.TRANSPARENT));
        newStage.show();

        Stage stage = (Stage) auctionsPane.getScene().getWindow();
        stage.close();
    }

    @FXML
    public void queuePurchaseClicked() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/Views/queuePurchase.fxml"));
        Scene newScene;
        newScene = new Scene(loader.load());
        QueuePurchaseController controller = loader.<QueuePurchaseController>getController();
        controller.setUp(RM);
        Stage inputStage = new Stage();
        inputStage.getIcons().add(new Image("/Icon/scale.png"));
        inputStage.setScene(newScene);
        inputStage.showAndWait();
    }

    @FXML
    public void createAuction() throws IOException {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Views/CreateAuction.fxml"));
            Scene newScene;
            newScene = new Scene(loader.load());
            CreateAuctionController controller = loader.<CreateAuctionController>getController();
            controller.setUp(RM);
            Stage inputStage = new Stage();
            inputStage.getIcons().add(new Image("/Icon/scale.png"));
            inputStage.setScene(newScene);
            inputStage.showAndWait();
        } catch (Exception e) {
            Logger.getLogger(AuctionController.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public void button_exit() {
        Runtime.getRuntime().halt(1);
    }

    @Override
    public void propertyChange(PropertyChangeEvent pce) throws RemoteException {
        
        switch(pce.getPropertyName()) {
           case "currentpricechange" :
              // Statements
              break; // optional

           case "newauction" :
              // Statements
              break; // optional   
        }
    }
}
