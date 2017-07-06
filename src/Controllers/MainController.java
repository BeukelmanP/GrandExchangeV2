/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Controllers;

import Classes.Auctions.Auction;
import Classes.Bid;
import Classes.CategoryEnum;
import Interfaces.IAuction;
import Interfaces.IAuctionInfo;
import fontyspublisher.IRemotePropertyListener;
import fontyspublisher.RemotePublisher;
import grandexchange.RegistryManager;
import java.beans.PropertyChangeEvent;
import java.io.IOException;
import static java.lang.Integer.parseInt;
import java.net.URL;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Collection;
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
import javafx.scene.Node;
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
public class MainController extends UnicastRemoteObject implements IRemotePropertyListener, Initializable {

    @FXML
    private ScrollPane auctionsPane;
    @FXML
    private ImageView loggedInUserImage;
    @FXML
    private ComboBox<CategoryEnum> comboBoxCategory;
    @FXML
    private ListView lstCategory;

    private RegistryManager RM;
    private IAuction auctionInterface;
    Pane allAuctions;


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

    public MainController() throws RemoteException
    {
    }
    
    public void setUp(RegistryManager RM) throws RemoteException {
        this.RM = RM;
        allAuctions = new Pane();
        RM.getAuctionInterface();
        auctionInterface = RM.getAuction();
        //publisher = new RemotePublisher();
        //this.refreshAuctions();
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
        //get auctions               
        long start = System.currentTimeMillis();
        ArrayList<Integer> auctionIDs = auctionInterface.getAuctionIds();
        long elapsedTime = System.currentTimeMillis() - start;
        System.out.println("PerformanceGetAuctionsInMS=" + elapsedTime);

        //draw auctions
        start = System.currentTimeMillis();
        allAuctions.setPrefWidth(800);
        this.auctionsPane.setPrefWidth(allAuctions.getPrefWidth());
        
        for (Integer i : auctionIDs) {
            Pane auctionPane = this.getPaneOfAuction(i);
            if (auctionPane != null)
            {
                allAuctions.setPrefHeight(this.auctionsPane.getPrefHeight() + auctionsPane.getPrefHeight());
                this.auctionsPane.setPrefHeight(allAuctions.getPrefHeight());
//                
//                publisher.registerProperty("auctionPane" + i);
//                publisher.subscribeRemoteListener(this, "auctionPane" + i);
//                publisher.inform("auctionPane" + i, this, auctionPane);  
            }
        }
        this.auctionsPane.setContent(allAuctions);
        elapsedTime = System.currentTimeMillis() - start;
        System.out.println("PerformanceSetAuctionsPaneInMS=" + elapsedTime);
    }
    
//    @FXML
//    public void refreshAuctions() throws RemoteException {
//        //get auctions               
//        long start = System.currentTimeMillis();
//        ArrayList<Integer> auctionIDs = auctionInterface.getAuctionIds();
//        long elapsedTime = System.currentTimeMillis() - start;
//        System.out.println("PerformanceGetAuctionsInMS=" + elapsedTime);
//
//        //draw auctions
//        start = System.currentTimeMillis();
//        Pane allAuctions = new Pane();
//        allAuctions.setPrefWidth(800);
//        allAuctions.setPrefHeight(150 * auctionIDs.size());
//        
//        for (Integer i : auctionIDs) {
//            IAuctionInfo auctionInfoInterface = auctionInterface.getIAuctionInterface(i);
//            
//            Pane Auction = new Pane();
//            Auction.setPrefWidth(800);
//            Auction.setPrefHeight(150);
//            Auction.relocate(0, 150 * i);
//            if ((i % 2) == 0) {
//                Auction.setStyle("-fx-background-color: lightgrey ");
//            }
//            Label productName = new Label();
//            productName.setText(auctionInfoInterface.getProductName());
//            productName.setFont(new Font("Arial", 25));
//            productName.relocate(150, 25);
//            publisher.registerProperty("productname" + i);
//            publisher.subscribeRemoteListener(this, "productname" + i);
//            publisher.inform("productname" + i, this, auctionInfoInterface.getProductName());
//
//            Label price = new Label();
//            price.setText("€" + auctionInfoInterface.getCurrentPrice());
//            price.setFont(new Font("Arial", 20));
//            price.relocate(550, 120);
//            publisher.registerProperty("currentprice" + i);
//            publisher.subscribeRemoteListener(this, "currentprice" + i);
//            publisher.inform("currentprice" + i, this, auctionInfoInterface.getCurrentPrice());
//
//            Label seller = new Label();
//            seller.setText(auctionInfoInterface.getSellerName());
//            seller.setFont(new Font("Arial", 15));
//            seller.relocate(550, 20);
//            publisher.registerProperty("sellername" + i);
//            publisher.subscribeRemoteListener(this, "sellername" + i);
//            publisher.inform("sellername" + i, this, auctionInfoInterface.getSellerName());
//
//
//            TextArea description = new TextArea();
//            description.setPrefSize(200, 60);
//            description.relocate(150, 65);
//            description.setText(auctionInfoInterface.getDescription());
//            description.wrapTextProperty().setValue(Boolean.TRUE);
//            description.setEditable(false);
//            publisher.registerProperty("description" + i);
//            publisher.subscribeRemoteListener(this, "description" + i);
//            publisher.inform("description" + i, this, auctionInfoInterface.getDescription());
//
//            //setting image of auction
//            ImageView image;
//            try {
//                image = new ImageView(new Image(auctionInfoInterface.getImageURLs()[0]));
//            } catch (Exception ex) {
//                image = new ImageView(new Image(this.getClass().getResource("/Classes/unavailable.jpg").toExternalForm()));
//            }
//            image.setFitWidth(100);
//            image.setFitHeight(100);
//            image.relocate(25, 25);
//            image.addEventHandler(MouseEvent.MOUSE_CLICKED,
//                    new EventHandler<MouseEvent>() {
//                @Override
//                public void handle(MouseEvent e) {
//                    try {
//                        showAuction(auctionInfoInterface);
//                    } catch (IOException ex) {
//                        Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, ex);
//                    }
//                }
//            });
//            publisher.registerProperty("imagethumbnail" + i);
//            publisher.subscribeRemoteListener(this, "imagethumbnail" + i);
//            publisher.inform("imagethumbnail" + i, this, image.getUserData());
//
//            Auction.getChildren().addAll(productName, image, price, seller, description);
//            allAuctions.getChildren().add(Auction);
//        }
//        auctionsPane.setContent(allAuctions);
//        elapsedTime = System.currentTimeMillis() - start;
//        System.out.println("PerformanceSetAuctionsPaneInMS=" + elapsedTime);
//    }
    
    
    public Pane getPaneOfAuction(int ID)
    {
        Pane auctionPane = new Pane();
        try {
            IAuctionInfo auctionInfoInterface = auctionInterface.getIAuctionInterface(ID);
            auctionPane.setPrefWidth(800);
            auctionPane.setPrefHeight(150);
            auctionPane.relocate(0, 150 * ID);
            if ((ID % 2) == 0) {
                auctionPane.setStyle("-fx-background-color: lightgrey ");
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
            auctionPane.getChildren().addAll(productName, image, price, seller, description);
            
            //invisible auctionid node to store auctionid as string
            Label auctionIDlabel = new Label();
            Integer auctionID = auctionInfoInterface.getId();
            auctionIDlabel.setText(auctionID.toString());
            auctionIDlabel.setVisible(false);
            auctionPane.getChildren().add(auctionIDlabel);
            
            auctionInfoInterface.subscribeRegisterProductName(this, "productName" + auctionID, auctionInfoInterface.getProductName());
            auctionInfoInterface.subscribeRegisterCurrentPrice(this, "currentPrice" + auctionID, auctionInfoInterface.getCurrentPrice());
            auctionInfoInterface.subscribeRegisterDescription(this, "description" + auctionID, auctionInfoInterface.getDescription());
            auctionInfoInterface.subscribeRegisterImage(this, "imageThumbnail" + auctionID, image.getUserData());
            auctionInfoInterface.subscribeRegisterSellerName(this, "sellerName" + auctionID, auctionInfoInterface.getSellerName());

            //auctionInfoInterface.subscribeAndRegisterPane(this, "auctionPane" + auctionInfoInterface.getId(), auctionPane);
        } catch (RemoteException ex) {
            Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, ex);
        }
        return auctionPane;
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
    public void propertyChange(PropertyChangeEvent evt) throws RemoteException {
        System.out.println("Maincontroller detected propertyChange of " + evt.getPropertyName());
                
        String compareString = "sellerName.*";
        /* kijk wat het producttype is, if bepaald type handel af voor dat type*/
        if (evt.getPropertyName().matches(compareString))
        {
            /*haal auctionID uit de binnengekregen value*/
            Integer auctionID = Integer.parseInt(evt.getPropertyName().substring(compareString.length()-2, evt.getPropertyName().length()));
            
            /*doorloop de lijst in de GUI en update corresponderend gui element*/
            //implementeer
            Pane auctionPane = this.getPaneOfAuction(auctionID);
            if (auctionPane != null)
            {
                allAuctions.setPrefHeight(this.auctionsPane.getPrefHeight() + auctionsPane.getPrefHeight());
                this.auctionsPane.setPrefHeight(allAuctions.getPrefHeight());
                
                Collection<Node> toRemove = new ArrayList<Node>();
                for (Node node : allAuctions.getChildren())
                {
                    Pane pane = (Pane)node;
                    Label auctionIDLabel = (Label)pane.getChildren().get(5);
                    
                    Integer auctionIDFromLabel = parseInt(auctionIDLabel.getText());
                    if (auctionID.equals(auctionIDFromLabel))
                    {
                        //toRemove.add(node);
                        auctionIDLabel.setText((String)evt.getNewValue());
                    }
                }
//                if (!toRemove.isEmpty())
//                {
//                    allAuctions.getChildren().removeAll(toRemove);
//                }
                allAuctions.getChildren().add(auctionPane);
            }      
        }
    }
}

