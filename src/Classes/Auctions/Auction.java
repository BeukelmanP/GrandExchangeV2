package Classes.Auctions;

import Interfaces.IAuctionInfo;
import Classes.Bid;
import Classes.Product;
import Classes.User;
import fontyspublisher.IRemotePropertyListener;
import fontyspublisher.RemotePublisher;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.text.DecimalFormat;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class Auction extends UnicastRemoteObject implements Serializable, IAuctionInfo {

    User seller;
    private int id;

    public RemotePublisher publisher;
    private double currentPrice;
    private double instabuyPrice;

    private boolean instabuyable;
    private int productQuantity;
    private Bid lastBid;
    private ArrayList<Bid> bids;
    private Product product;
    private StatusEnum status;
    private String description;
    private String[] imageURLs;
    private String type;

    /**
     *
     * @param id
     * @param seller
     * @param product
     * @param price
     * @param quantity
     * @param status
     * @param description
     * @param imageURLs
     * @param instabuy
     */
    public Auction(int id, User seller, Product product, double price, int quantity, StatusEnum status, String description, String imageURLs, double instabuy) throws RemoteException{
        this.id = id;
        this.seller = seller;
        this.product = product;
        DecimalFormat decim = new DecimalFormat("#.00");
        this.currentPrice = price;
        this.productQuantity = quantity;
        this.instabuyable = false;
        this.status = status;
        this.description = description;
        this.imageURLs = imageURLs.split(";");
        bids = new ArrayList<>();
        this.instabuyPrice = instabuy;
        publisher = new RemotePublisher();
        publisher.registerProperty("currentprice");
        publisher.registerProperty("quantity");
        publisher.registerProperty("newbid");   
    }

    /**
     *
     * @param seller
     * @param product
     * @param quantity
     * @param price
     * @param instabuyprice
     * @param status
     * @param description
     * @param imageURLs
     */
    public Auction(User seller, Product product, int quantity, double price, double instabuyprice, StatusEnum status, String description, String imageURLs) throws RemoteException {
        this.seller = seller;
        this.product = product;
        this.currentPrice = round(price, 2);
        this.productQuantity = quantity;
        this.instabuyPrice = instabuyprice;
        this.instabuyable = true;
        this.status = status;
        this.description = description;
        this.imageURLs = imageURLs.split(";");
        bids = new ArrayList<>();
        publisher = new RemotePublisher();
        publisher.registerProperty("currentprice");
        publisher.registerProperty("quantity");
        publisher.registerProperty("newbid");
    }

    /**
     * returns instabuy price
     *
     * @return double
     */
    @Override
    public double getInstabuyPrice() {
        return instabuyPrice;
    }


    /**
     * returns is instabuyable true/false
     *
     * @return
     */
    @Override
    public boolean isInstabuyable() {
        return instabuyable;
    }

    /**
     * returns the highest bid at the moment
     *
     * @return Bid
     */
    public Bid getBestBid() {
        double money = 0.0;
        Bid returnb = null;
        for (Bid b : bids) {
            if (b.getAmount() > money) {
                returnb = b;
                money = b.getAmount();
            }
        }
        return returnb;
    }

    /**
     * adds bid to all bids of auction
     *
     * @param bid
     */
    public boolean addBid(Bid bid) {
        try {
            double amount = bid.getAmount();
            if (amount > currentPrice) {
                this.bids.add(bid);
                this.lastBid = bid;
                this.currentPrice = bid.getAmount();
                return true;
            } else if (amount == currentPrice) {
                this.bids.add(bid);
                this.lastBid = bid;
                return true;
            }
            return false;
        } catch (IllegalArgumentException ex) {
            System.out.println(ex);
            return false;
        }
    }

    public void addBid(ArrayList<Bid> bids) {
        this.bids.addAll(bids);
    }

    public void setCurrentPrice(double newPrice) {
        currentPrice = round(newPrice, 2);
    }

    /**
     * gets all bids made on the auction
     *
     * @return List<Bid>
     */
    @Override
    public ArrayList<Bid> getBids() {
        return bids;
    }

    /**
     * send a request for email contact
     *
     * @param emailRequester : may not be empty nor null
     */
    public void sendMailRequest(String emailRequester) {

    }
    

    @Override
    public String getProductDescription(){
        return product.getDescription();
    }

    public Product getProduct() {
        return product;
    }

    @Override
    public String getDescription() {
        return description;
    }
    
    @Override
    public String getProductName(){
        return product.getName();
    }

    @Override
    public StatusEnum getStatus() {
        return status;
    }

    @Override
    public Double getCurrentPrice() {
        return currentPrice;
    }

    @Override
    public int getProductQuantity() {
        return productQuantity;
    }

    public void setProductQuantity(int buyAmount) {
        productQuantity = productQuantity - buyAmount;
    }

    @Override
    public String[] getImageURLs() {
        return imageURLs;
    }
    
    @Override
    public String getSellerImageUrl(){
        return seller.getImageURL();
    }
    
    @Override
    public String getType(){
        return this.type;
    }
    
    public void setType(String type){
        this.type = type;
    }

    @Override
    public String getSellerName(){
        return seller.getUsername();
    }
    
    public User getSeller() {
        return seller;
    }
    
    

    public static double round(double value, int places) {
        if (places < 0) {
            throw new IllegalArgumentException();
        }
        BigDecimal bd = BigDecimal.valueOf(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    @Override
    public int getId() {
        return id;
    }
    
    @Override
    public void subscribe(IRemotePropertyListener listener, String property) throws RemoteException {
        publisher.subscribeRemoteListener(listener, property);
    }
    
    @Override
    public void unSubscribe(IRemotePropertyListener listener, String property) throws RemoteException {
        publisher.unsubscribeRemoteListener(listener, property);
    }
    
//    @Override
//    public void subscribeRegisterInformObjects(IRemotePropertyListener listener, String property, Collection<Object> objects) throws RemoteException {
//
//        for (Object o : objects)
//        {
//            if (o instanceof String)
//            {
//                
//            }
//            publisher.registerProperty(property);
//            publisher.subscribeRemoteListener(listener, property);
//            publisher.inform(property, listener, );  
//        }
//    }
    
    @Override
    public void subscribeRegisterProductName(IRemotePropertyListener listener, String property, String productName) throws RemoteException
    {
        publisher.registerProperty(property);
        publisher.subscribeRemoteListener(listener, property);
        publisher.inform(property, listener, productName);    
    }
    @Override
    public void subscribeRegisterCurrentPrice(IRemotePropertyListener listener, String property, Double currentPrice) throws RemoteException
    {
        publisher.registerProperty(property);
        publisher.subscribeRemoteListener(listener, property);
        publisher.inform(property, listener, currentPrice);   
    }
    @Override
    public void subscribeRegisterDescription(IRemotePropertyListener listener, String property, String description) throws RemoteException
    {
        publisher.registerProperty(property);
        publisher.subscribeRemoteListener(listener, property);
        publisher.inform(property, listener, description);   
    }
    @Override
    public void subscribeRegisterImage(IRemotePropertyListener listener, String property, Object image) throws RemoteException
    {
        publisher.registerProperty(property);
        publisher.subscribeRemoteListener(listener, property);
        publisher.inform(property, listener, image);   
    }
    @Override
    public void subscribeRegisterSellerName(IRemotePropertyListener listener, String property, String sellerName) throws RemoteException
    {
        publisher.registerProperty(property);
        publisher.subscribeRemoteListener(listener, property);
        publisher.inform(property, listener, sellerName);   
    }
    

    
    public synchronized void informClients(){
        try {
            publisher.inform("quantity", null, productQuantity);
            publisher.inform("currentprice", null, this.currentPrice);
            publisher.inform("newbid", null, lastBid);
            System.out.println("informed clients!");
        } catch (RemoteException ex) {
            Logger.getLogger(Auction.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
