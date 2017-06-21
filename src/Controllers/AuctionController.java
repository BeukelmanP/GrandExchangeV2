/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Controllers;

import Classes.Auctions.Auction;
import Classes.Auctions.Countdown;
import Classes.Auctions.Direct;
import Classes.Auctions.Standard;
import Classes.Auctions.StatusEnum;
import Classes.Bid;
import Classes.Grand_Exchange;
import Classes.User;
import Exceptions.NotEnoughMoneyException;
import Interfaces.IAuction;
import Interfaces.IPlaceBid;
import grandexchange.RegistryManager;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.URL;
import java.rmi.RemoteException;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.util.Duration;
import javax.swing.JOptionPane;

/**
 * FXML Controller class
 *
 * @author piete
 */
public class AuctionController implements Initializable {

    @FXML
    private ImageView bigProductImage;
    @FXML
    private ImageView sellerImage;
    @FXML
    private Label sellerName;
    @FXML
    private Label productTitle;
    @FXML
    private Label auctiontype;
    @FXML
    private Label productStatus;
    @FXML
    private Label countdownCurrentPrice;
    @FXML
    private Label InstabuyCurrentPrice;
    @FXML
    private Label countdownAvailableUnits;
    @FXML
    private Label instabuyTextid;
    @FXML
    private Label CreateDate;
    @FXML
    private Label lblUnits;
    @FXML
    private Label lblBid;
    @FXML
    private TextArea productDescription;
    @FXML
    private TextArea auctionDescription;
    @FXML
    private Button closeButton;
    @FXML
    private Button countdownBuyBtn;
    @FXML
    private TextField txtUnitstoBuy;
    @FXML
    private ScrollPane imagesPane;
    @FXML
    private ScrollPane recentPurchasesPane;
    @FXML
    private ProgressBar minutesBar;
    @FXML
    private Button countdownBidBuyBtn;
    @FXML
    private TextField txtUnitstoBuyBid;
    @FXML
    private TextField txtPriceToBid;
    @FXML
    private Button BidBtn;
    @FXML
    private Button buyButton;

    IAuction auctionInterface;
    IPlaceBid bid;
    Auction auction;
    Direct directAuction;
    Standard standardAuction;
    Grand_Exchange GX;
    private User loggedInUser;
    private String type;
    private RegistryManager RM;
    Timeline timeline1;
    Timeline timeline;
    int timeSeconds;

    /**
     * Initializes the controller class.
     *
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {

    }

    public void setUp(Auction auction, RegistryManager RM) throws RemoteException {
        this.GX = new Grand_Exchange();
        this.RM = RM;
        this.auction = auction;
        loggedInUser = RM.getUser();
        update();
        timeline1 = new Timeline();
        timeline1.getKeyFrames().add(new KeyFrame(Duration.seconds(5), new EventHandler() {

            @Override
            public void handle(Event event) {
                liveUpdate();
            }
        }));
        timeline1.playFromStart();

    }

    public void liveUpdate() {
        int updateid = auction.getId();
        try {
            auction = RM.getAuction().getAuction(updateid);
        } catch (RemoteException ex) {
            Logger.getLogger(AuctionController.class.getName()).log(Level.SEVERE, null, ex);
        }
        if (auction instanceof Countdown) {
            //setting auction
            Countdown countdownAuction = (Countdown) auction;
            countdownAuction.setPrice();
            countdownCurrentPrice.setText("€" + countdownAuction.getCurrentPrice());
            timeline.playFromStart();
            if (auction.getProductQuantity()
                    > 1) {
                countdownAvailableUnits.setText("There are " + auction.getProductQuantity() + " units available");
            } else if (auction.getProductQuantity()
                    == 1) {
                countdownAvailableUnits.setText("There is just 1 item left");
            } else if (auction.getProductQuantity()
                    == 0) {
                countdownAvailableUnits.setText("There are no items left, you missed it");
            }

            setCountdownBuys(auction);
        } else if (auction instanceof Standard) {
            standardAuction = (Standard) auction;
            countdownCurrentPrice.setText("€" + standardAuction.getCurrentPrice());
            if (auction.getProductQuantity()
                    >= 1) {
                countdownAvailableUnits.setText("You're Buying " + auction.getProductQuantity() + " units for this price!");
            }

            setCountdownBuys(auction);
        }
    }

    public void update() {
        int updateid = auction.getId();
        try {
            auction = RM.getAuction().getAuction(updateid);
        } catch (RemoteException ex) {
            Logger.getLogger(AuctionController.class.getName()).log(Level.SEVERE, null, ex);
        }
        productTitle.setText(auction.getProduct().getName());
        productDescription.setText(auction.getProduct().getDescription());
        auctionDescription.setText(auction.getDescription());
        productStatus.setText(setStatus(auction.getStatus()));
        int i = 0;
        Pane imagePane = new Pane();
        imagePane.setPrefWidth(85 * auction.getImageURLs().length);
        imagePane.setPrefHeight(70);
        for (String URL : auction.getImageURLs()) {
            ImageView image = null;
            try {
                image = new ImageView(new Image(URL));
            } catch (Exception ex) {
                image = new ImageView(new Image(this.getClass().getResource("/Classes/unavailable.jpg").toExternalForm()));
            }
            image.setFitWidth(80);
            image.setFitHeight(60);
            image.relocate(85 * i, 5);
            image.addEventHandler(MouseEvent.MOUSE_ENTERED,
                    new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent e) {
                    ImageView i = (ImageView) e.getSource();
                    bigProductImage.setImage(i.getImage());
                }
            });
            Image bigImage = null;
            try {
                bigImage = new Image(auction.getImageURLs()[0]);
            } catch (Exception ex) {
                bigImage = new Image(this.getClass().getResource("/Classes/unavailable.jpg").toExternalForm());
            }

            bigProductImage.setImage(bigImage);
            imagePane.getChildren().add(image);
            i++;
        }

        sellerImage.setImage(new Image(auction.getSeller().getImageURL()));
        sellerName.setText(auction.getSeller().getUsername());
        imagesPane.setContent(imagePane);

        // Checks if auction has instabuy.
        if (auction.isInstabuyable() == true) {
            countdownBuyBtn.setDisable(false);
            txtUnitstoBuy.setDisable(false);

        } else {
            countdownBuyBtn.setDisable(true);
            txtUnitstoBuy.setDisable(true);
        }

        //Checks if auction is of instance Countdown
        if (auction instanceof Countdown) {
            //setting auction
            auctiontype.setText("Countdown Auction");
            Countdown countdownAuction = (Countdown) auction;
            countdownAuction.setPrice();

            //setting buttons etc:
            txtPriceToBid.setVisible(false);
            txtPriceToBid.setText(countdownAuction.getCurrentPrice() + "");
            BidBtn.setVisible(false);
            lblBid.setVisible(false);
            txtUnitstoBuyBid.setVisible(true);
            buyButton.setVisible(true);
            lblUnits.setVisible(true);

            countdownCurrentPrice.setText("€" + countdownAuction.getCurrentPrice());
            InstabuyCurrentPrice.setText("€" + countdownAuction.getInstabuyPrice());
            long now = System.currentTimeMillis();
            long then = countdownAuction.getCreationDate().getTime();
            long periods_passed = (long) Math.floor(((now - then) / 1000 / 60 / (int) countdownAuction.getPriceLoweringDelay()));
            long next_period_begin = ((periods_passed + 1) * 1000 * 60 * (int) countdownAuction.getPriceLoweringDelay()) + countdownAuction.getCreationDate().getTime();
            Timestamp newDate = new Timestamp(next_period_begin);
            if (timeline != null) {
                timeline.stop();
            }
            timeline = null;
            timeline = new Timeline();
            timeline.setCycleCount(Timeline.INDEFINITE);
            long duration = (next_period_begin - System.currentTimeMillis()) / 1000;
            timeSeconds = (int) duration;
            timeline.getKeyFrames().add(new KeyFrame(Duration.seconds(1), new EventHandler() {

                @Override
                public void handle(Event event) {
                    timeSeconds--;
                    int minutesToGo = (int) Math.floor(timeSeconds / 60);
                    int secondsToGo = timeSeconds - (minutesToGo * 60);
                    String minutes = Integer.toString(minutesToGo);
                    String seconds = Integer.toString(secondsToGo);
                    if (minutesToGo < 10) {
                        minutes = "0" + minutes;
                    }
                    if (secondsToGo < 10) {
                        seconds = "0" + seconds;
                    }
                    CreateDate.setText(minutes + ":" + seconds);
                    if (minutesToGo <= 0) {
                        minutesBar.setProgress(-1);
                    } else {
                        minutesBar.setProgress(timeSeconds / (countdownAuction.getPriceLoweringDelay() * 60));
                    }
                    if (timeSeconds <= 0) {
                        timeline.stop();
                        update();
                    }
                }
            }));
            timeline.playFromStart();

            if (auction.getProductQuantity()
                    > 1) {
                countdownAvailableUnits.setText("There are " + auction.getProductQuantity() + " units available");
            } else if (auction.getProductQuantity()
                    == 1) {
                countdownAvailableUnits.setText("There is just 1 item left");
            } else if (auction.getProductQuantity()
                    == 0) {
                countdownAvailableUnits.setText("There are no items left, you missed it");
            }

            setCountdownBuys(auction);

        } else if (auction instanceof Standard) {
            //setting correct buttons:           
            txtPriceToBid.setVisible(true);
            BidBtn.setVisible(true);
            lblBid.setVisible(true);
            txtUnitstoBuyBid.setVisible(false);
            buyButton.setVisible(false);
            lblUnits.setVisible(false);

            this.type = "standard";
            this.minutesBar.setVisible(false);
            auctiontype.setText("Standard Auction");
            standardAuction = (Standard) auction;
            countdownCurrentPrice.setText("€" + standardAuction.getCurrentPrice());
            InstabuyCurrentPrice.setText("€" + standardAuction.getInstabuyPrice());

            Timestamp newDate = standardAuction.getCreationDate();
            CreateDate.setText(newDate.getMonth() + "/" + newDate.getDay() + "  " + newDate.getHours() + ":" + newDate.getMinutes() + ":" + newDate.getSeconds());

            if (auction.getProductQuantity()
                    >= 1) {
                countdownAvailableUnits.setText("You're Buying " + auction.getProductQuantity() + " units for this price!");
            }

            setCountdownBuys(auction);
            long now = System.currentTimeMillis();
            long then = standardAuction.getEndDate().getTime();
            timeSeconds = (int) ((now - then) / 1000);
            if (timeline != null) {
                timeline.stop();
            }
            timeline = null;
            timeline = new Timeline();
            timeline.setCycleCount(Timeline.INDEFINITE);
            timeline.getKeyFrames().add(new KeyFrame(Duration.seconds(1), new EventHandler() {

                @Override
                public void handle(Event event) {
                    timeSeconds--;
                    int timeDays = timeSeconds;
                    int timeHours = timeSeconds;
                    int daysToGo = (int) Math.floor(timeDays / 60 / 60 / 24);
                    int hoursToGo = (int) Math.floor(timeSeconds / 60 / 60) - daysToGo * 24;
                    int minutesToGo = (int) Math.floor(timeSeconds / 60) - (hoursToGo * 60) - (daysToGo * 24 * 60);
                    int secondsToGo = timeSeconds - (hoursToGo * 60 * 60) - (daysToGo * 24 * 60 * 60) - (minutesToGo * 60);
                    String hours = Integer.toString(hoursToGo);
                    String minutes = Integer.toString(minutesToGo);
                    String seconds = Integer.toString(secondsToGo);
                    if (hoursToGo < 10) {
                        hours = "0" + hours;
                    }
                    if (minutesToGo < 10) {
                        minutes = "0" + minutes;
                    }
                    if (secondsToGo < 10) {
                        seconds = "0" + seconds;
                    }
                    CreateDate.setText(daysToGo + "Days,  " + hours + ":" + minutes + ":" + seconds);
                    if (timeSeconds <= 0) {
                        timeline.stop();
                        update();
                    }
                }
            }));
            timeline.playFromStart();

        }

        if (auction instanceof Direct) {
            this.type = "direct";
            auctiontype.setText("Direct Auction");
            directAuction = (Direct) auction;
            countdownCurrentPrice.setText("€" + directAuction.getCurrentPrice());
            InstabuyCurrentPrice.setText("€" + directAuction.getInstabuyPrice());

            Timestamp newDate = directAuction.getCreationDate();
            CreateDate.setText(newDate.getMonth() + "/" + newDate.getDay() + "  " + newDate.getHours() + ":" + newDate.getMinutes() + ":" + newDate.getSeconds());

            if (auction.getProductQuantity()
                    > 1) {
                countdownAvailableUnits.setText("There are " + auction.getProductQuantity() + " units available");
            } else if (auction.getProductQuantity()
                    == 1) {
                countdownAvailableUnits.setText("There is just 1 item left");
            } else if (auction.getProductQuantity()
                    == 0) {
                countdownAvailableUnits.setText("There are no items left, you missed it");
            }

            setCountdownBuys(auction);

        }

        txtUnitstoBuy.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if (!newValue.matches("\\d*")) {
                    txtUnitstoBuy.setText(newValue.replaceAll("[^\\d]", ""));
                }
            }
        });
    }

    /**
     * Returns textual status
     *
     * @param status StatusEnum
     * @return
     */
    public String setStatus(StatusEnum status) {
        switch (status) {
            case New:
                return "New";
            case GoodAsNew:
                return "As good as new";
            case Refurbished:
                return "Refurbished";
            case Used_Good:
                return "little damage(small dents)";
            case Used_Bad:
                return "annoying damage(Cracks)";
            case Broken:
                return "not fully functional or Broken";
            default:
                return "";
        }
    }

    public void buyButtonClick() throws RemoteException, NotEnoughMoneyException, SQLException {
        RM.getPlaceBidInterface();
        this.bid = RM.getBid();
        double price = auction.getCurrentPrice();
        if (Integer.parseInt(txtUnitstoBuyBid.getText()) >= 1 && Integer.parseInt(txtUnitstoBuyBid.getText()) <= auction.getProductQuantity()) {
            double totalprice = Double.parseDouble(txtUnitstoBuyBid.getText()) * auction.getCurrentPrice();
            int reply = JOptionPane.showConfirmDialog(null, "Are you sure you want to Buy " + txtUnitstoBuyBid.getText() + " items with a total price of: €" + totalprice + " ?", "Are You Sure?", JOptionPane.YES_NO_OPTION);
            if (reply == JOptionPane.YES_OPTION) {
                int units = Integer.parseInt(txtUnitstoBuyBid.getText());
                bid.placeBuy(units, loggedInUser.getUsername(), auction.getId(), auction.getCurrentPrice());
                update();
                JOptionPane.showMessageDialog(null, "Your order has been placed");
            } else {
                JOptionPane.showMessageDialog(null, "Canceled");
            }
        } else if (Integer.parseInt(txtUnitstoBuyBid.getText()) < 1) {
            JOptionPane.showMessageDialog(null, "You can't buy less than 1 object");
        } else if (Integer.parseInt(txtUnitstoBuyBid.getText()) > auction.getProductQuantity()) {
            JOptionPane.showMessageDialog(null, "You can't buy more objects than there are available");
        } else {
            JOptionPane.showMessageDialog(null, "Something went wrong");
        }
        checkAuctionStatus();
    }

    public void bidButtonClick() throws RemoteException, NotEnoughMoneyException, SQLException, IOException {
        RM.getPlaceBidInterface();
        this.bid = RM.getBid();
        if (auction.getCurrentPrice() < Double.parseDouble(txtPriceToBid.getText())) {
            int reply = JOptionPane.showConfirmDialog(null, "Are you sure you want to Bid " + txtPriceToBid.getText(), "Are You Sure?", JOptionPane.YES_NO_OPTION);
            if (reply == JOptionPane.YES_OPTION) {
                bid.placeBid(1, loggedInUser.getUsername(), auction.getId(), Double.parseDouble(txtPriceToBid.getText()), checkPing("localhost"));
                update();
                JOptionPane.showMessageDialog(null, "Your bid has been placed");
            } else {
                JOptionPane.showMessageDialog(null, "Canceled");
            }
        } else if (auction.getCurrentPrice() > Double.parseDouble(txtPriceToBid.getText())) {
            JOptionPane.showMessageDialog(null, "You can't bid less than current price");
        } else {
            JOptionPane.showMessageDialog(null, "Something went wrong");
        }
        checkAuctionStatus();
    }

    public void instabuyButtonClick() throws RemoteException, NotEnoughMoneyException, SQLException {
        RM.getPlaceBidInterface();
        this.bid = RM.getBid();

        if (Integer.parseInt(txtUnitstoBuy.getText()) <= auction.getProductQuantity() && Integer.parseInt(txtUnitstoBuy.getText()) > 0 && auction != null) {
            double totalPrice = Double.parseDouble(txtUnitstoBuy.getText()) * auction.getInstabuyPrice();

            int reply = JOptionPane.showConfirmDialog(null, "Are you sure you want to buy " + txtUnitstoBuy.getText() + "\nitems with the price of: €" + auction.getInstabuyPrice() + " a item \nand a total of: €" + totalPrice, "Are you sure?", JOptionPane.YES_NO_OPTION);
            if (reply == JOptionPane.YES_OPTION) {
                int units = Integer.parseInt(txtUnitstoBuy.getText());
                bid.placeBid(units, loggedInUser.getUsername(), auction.getId(), auction.getInstabuyPrice(), 0);
                update();
            } else {
                JOptionPane.showMessageDialog(null, "Canceled");
            }
        } else if (Integer.parseInt(txtUnitstoBuy.getText()) <= 0) {
            JOptionPane.showMessageDialog(null, "You can't buy less than 1 object");
        } else {
            JOptionPane.showMessageDialog(null, "You can't buy more objects than there are available");
        }
        checkAuctionStatus();
    }

    public void setCountdownBuys(Auction auction) {
        Pane BuyPane = new Pane();
        BuyPane.setPrefWidth(371);
        BuyPane.setPrefHeight(75 * auction.getBids().size());
        int a = 0;
        ArrayList<Bid> bids = auction.getBids();
        Collections.reverse(bids);
        for (Bid b : bids) {
            Pane p = new Pane();
            p.setPrefHeight(75);
            p.setPrefWidth(371);
            p.relocate(0, a * 75);
            if ((a % 2) == 0) {
                p.setStyle("-fx-background-color: lightgrey ");
            }
            Label name = new Label();
            name.setText(b.getPlacerUsername());
            name.setFont(new Font("Arial", 17));
            Label price = new Label();
            if (auction instanceof Countdown || auction instanceof Direct) {
                price.setText("Bought at a price of: €" + b.getAmount());
            } else {
                price.setText("Bidded: €" + b.getAmount() + " for this item");
            }
            price.setFont(new Font("Arial", 14));
            name.relocate(10, 15);
            price.relocate(10, 45);
            p.getChildren().addAll(price, name);
            BuyPane.getChildren().add(p);
            a++;
        }

        recentPurchasesPane.setContent(BuyPane);
    }

    /**
     * if items in the auction are less then one, the auction closes
     */
    public void checkAuctionStatus() throws SQLException {
        if (auction instanceof Countdown) {
            Countdown countdown = (Countdown) auction;
            if (countdown.getProductQuantity() < 1) {
                GX.closeAuction();
                System.out.println("Verwijderd uit de databsase");
                closeButtonClick();
            }
        } else if (auction instanceof Direct) {
            Direct direct = (Direct) auction;
            if (direct.getProductQuantity() < 1) {
                GX.closeAuction();
                System.out.println("Verwijderd uit de databsase");
                closeButtonClick();
            }
        } else if (auction instanceof Standard) {
            Standard standard = (Standard) auction;
            if (standard.getProductQuantity() < 1) {
                GX.closeAuction();
                System.out.println("Verwijderd uit de databsase");
                closeButtonClick();
            }
        }

    }

    @FXML
    private void closeButtonClick() {
        Stage stage = (Stage) closeButton.getScene().getWindow();
        stage.close();
    }

    public long checkPing(String host) throws IOException {

        long currentTime = System.currentTimeMillis();
        boolean isPinged = InetAddress.getByName(host).isReachable(2000); 
        currentTime = System.currentTimeMillis() - currentTime;
        if (isPinged) {
            System.out.println("pinged successfully in " + currentTime + "millisecond");
        } else {
            System.out.println("PIng failed.");
        }
        return currentTime;

    }
}
