/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Database;

import Classes.Auctions.Auction;
import Classes.Auctions.Countdown;
import Classes.Auctions.Direct;
import Classes.Auctions.Standard;
import Classes.Auctions.StatusEnum;
import Classes.Bid;
import Classes.Product;
import Classes.User;
import static Database.ProductConnection.SET_PRODUCT_NEW;
import java.sql.CallableStatement;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author kyle_
 */
public class AuctionConnection {

    private java.sql.Connection myConn;
    private PreparedStatement pstmt;
    private Statement myStmt;
    private ResultSet myRs;
    private Auction auction;
    private ArrayList<Auction> auctions;
    private ArrayList<Bid> bids;
    // Connections
    private UserConnection userConn = new UserConnection();
    private Connection conn = new Connection();
    private ProductConnection productConn = new ProductConnection();

    // SQL codes
    static final String GET_AUCTION_BY_ID = "SELECT * FROM auction WHERE id = ?";
    static final String GET_BID_FROM_AUCTION_ID = "SELECT * FROM bid WHERE auctionID = ?";
    static final String GET_FROM_AUCTIONS = "SELECT * FROM auction";
    static final String SET_AUCTION_NEW = "INSERT INTO auction(sellerID, productID, timecreated, currentprice, instabuyprice, instabuyable, productquantity, timeend, priceloweringAmount, priceloweringdelay, type, status, imageUrl, description) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
    static final String SET_CLOSE_AUCTION = "DELETE FROM `auction` WHERE  `productquantity` < 1";

    // Constructor
    public AuctionConnection() {
        myConn = conn.getMyConn();
    }

    /**
     * Get the Auction with the given id.
     *
     * @param id of the Auction
     * @return Auction with the given id.
     */
    public Auction getAuction(int id) {
        User user;
        Product product;
        int quantity;
        Timestamp date;
        double price;
        double instabuyprice;
        double priceloweringAmount;
        double priceloweringDelay;
        double minprice;
        StatusEnum status;
        String description;
        String imageURL;
        ArrayList<Bid> bids;

        try {
            conn.getConnection();
            pstmt = conn.getMyConn().prepareStatement(GET_AUCTION_BY_ID);
            pstmt.setInt(1, id);

            myRs = pstmt.executeQuery();
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
            Logger.getLogger(Connection.class.getName()).log(Level.SEVERE, null, ex);
        }

        try {
            myRs.next();
            switch (myRs.getString("type")) {
                case "countdown":
                    id = myRs.getInt("id");
                    user = userConn.getUser((myRs.getInt("sellerID")));
                    product = productConn.getProduct(myRs.getInt("productID"));
                    quantity = myRs.getInt("productquantity");
                    price = myRs.getDouble("currentprice");
                    priceloweringAmount = myRs.getDouble("priceloweringAmount");
                    priceloweringDelay = myRs.getDouble("priceloweringdelay");
                    minprice = myRs.getDouble("minPrice");
                    status = StatusEnum.values()[myRs.getInt("status")];
                    description = myRs.getString("description");
                    imageURL = myRs.getString("imageUrl");
                    instabuyprice = myRs.getDouble("instabuyprice");
                    date = myRs.getTimestamp("timecreated");
                    auction = new Countdown(id, user, product, quantity, price, priceloweringAmount, priceloweringDelay, minprice, status, description, imageURL, instabuyprice, date);
                    auction.addBid(getBids(id));
                    break;
                case "direct": {
                    id = myRs.getInt("id");
                    user = userConn.getUser(myRs.getInt("sellerID"));
                    product = productConn.getProduct(myRs.getInt("productID"));
                    price = myRs.getDouble("currentprice");
                    quantity = myRs.getInt("productquantity");
                    Timestamp begin = myRs.getTimestamp("timecreated");
                    status = StatusEnum.values()[myRs.getInt("status")];
                    description = myRs.getString("description");
                    imageURL = myRs.getString("imageUrl");
                    instabuyprice = myRs.getDouble("instabuyprice");
                    auction = new Direct(id, user, product, price, begin, quantity, status, description, imageURL, instabuyprice);
                    auction.addBid(getBids(id));
                    break;
                }
                case "standard": {
                    id = myRs.getInt("id");
                    user = userConn.getUser(myRs.getInt("sellerID"));
                    product = productConn.getProduct(myRs.getInt("productID"));
                    price = myRs.getDouble("currentprice");
                    quantity = myRs.getInt("productquantity");
                    Timestamp begin = myRs.getTimestamp("timecreated");
                    date = myRs.getTimestamp("timeend");
                    status = StatusEnum.values()[myRs.getInt("status")];
                    description = myRs.getString("description");
                    imageURL = myRs.getString("imageUrl");
                    instabuyprice = myRs.getDouble("instabuyprice");
                    auction = new Standard(id, user, product, price, quantity, begin, date, status, description, imageURL, instabuyprice);
                    auction.addBid(getBids(id));
                    break;
                }
                default:
                    break;
            }
            return auction;

        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            Logger.getLogger(Connection.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("Failed to get auction by ID");
        }

        conn.closeConnection();
        return null;
    }

    /**
     * Gets an arrayList with all the auctions in the database.
     *
     * @param selectFrom Does Nothing
     * @param where Does Nothing
     * @param is Does Nothing
     * @return ArrayList with auctions
     */
    public ArrayList<Auction> getAuctions(String selectFrom, String where, String is) {

        auctions = new ArrayList<>();
        int id;
        User user;
        Product product;
        int quantity;
        Timestamp date;
        double price, instabuyprice, priceloweringAmount, priceloweringDelay, minprice;
        StatusEnum status;
        String description;
        String imageURL;
        ArrayList<Bid> bids;

        try {
            conn.getConnection();
            pstmt = conn.getMyConn().prepareStatement(GET_FROM_AUCTIONS);
            myRs = pstmt.executeQuery();
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
            Logger.getLogger(Connection.class.getName()).log(Level.SEVERE, null, ex);
        }

        try {
            while (myRs.next()) {

                switch (myRs.getString("type")) {
                    // In case of Direct
                    case "countdown":
                        id = myRs.getInt("id");
                        user = userConn.getUser((myRs.getInt("sellerID")));
                        product = productConn.getProduct(myRs.getInt("productID"));
                        quantity = myRs.getInt("productquantity");
                        price = myRs.getDouble("currentprice");
                        priceloweringAmount = myRs.getDouble("priceloweringAmount");
                        priceloweringDelay = myRs.getDouble("priceloweringdelay");
                        minprice = myRs.getDouble("minPrice");
                        status = StatusEnum.values()[myRs.getInt("status")];
                        description = myRs.getString("description");
                        imageURL = myRs.getString("imageUrl");
                        instabuyprice = myRs.getDouble("instabuyprice");
                        date = myRs.getTimestamp("timecreated");
                        auction = new Countdown(id, user, product, quantity, price, priceloweringAmount, priceloweringDelay, minprice, status, description, imageURL, instabuyprice, date);
                        //auction.addBid(getBids(id));
                        break;
                    case "direct": {
                        id = myRs.getInt("id");
                        user = userConn.getUser(myRs.getInt("sellerID"));
                        product = productConn.getProduct(myRs.getInt("productID"));
                        price = myRs.getDouble("currentprice");
                        quantity = myRs.getInt("productquantity");
                        Timestamp begin = myRs.getTimestamp("timecreated");
                        status = StatusEnum.values()[myRs.getInt("status")];
                        description = myRs.getString("description");
                        imageURL = myRs.getString("imageUrl");
                        instabuyprice = myRs.getDouble("instabuyprice");
                        auction = new Direct(id, user, product, price, begin, quantity, status, description, imageURL, instabuyprice);
                        //auction.addBid(getBids(id));
                        break;
                    }
                    case "standard": {
                        id = myRs.getInt("id");
                        user = userConn.getUser(myRs.getInt("sellerID"));
                        product = productConn.getProduct(myRs.getInt("productID"));
                        price = myRs.getDouble("currentprice");
                        quantity = myRs.getInt("productquantity");
                        Timestamp begin = myRs.getTimestamp("timecreated");
                        date = myRs.getTimestamp("timeend");
                        status = StatusEnum.values()[myRs.getInt("status")];
                        description = myRs.getString("description");
                        imageURL = myRs.getString("imageUrl");
                        instabuyprice = myRs.getDouble("instabuyprice");
                        auction = new Standard(id, user, product, price, quantity, begin, date, status, description, imageURL, instabuyprice);
                        //auction.addBid(getBids(id));
                        break;
                    }
                    default:
                        break;
                }

                auctions.add(auction);
                auction = null;
            }
            System.out.println("Auctions retrieved from DB");
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            System.out.println("Auctions NOT retrieved from DB");
 Logger.getLogger(Connection.class.getName()).log(Level.SEVERE, null, ex);
        }

        try {
            conn.closeConnection();
        } catch (Exception ex) {

        }

        return auctions;
    }

    /**
     * Gets a arrayList with all the bids in a specific auction.
     *
     * @param id The id of the auction.
     * @return Arraylist with all the bids from a specific auction.
     */
    public ArrayList<Bid> getBids(int id) {

        ArrayList<Bid> bids = new ArrayList<>();
        Bid bid;
        User user;
        int auctionId;
        int buyerId;
        double price;
        PreparedStatement preparedStatement;
        ResultSet resultset;
        try {
            conn.getConnection();
            pstmt = myConn.prepareStatement(GET_BID_FROM_AUCTION_ID);
            pstmt.setInt(1, id);
            resultset = pstmt.executeQuery();
            while (resultset.next()) {
                auctionId = resultset.getInt("auctionID");
                buyerId = resultset.getInt("placerID");
                price = resultset.getDouble("amount");
                user = userConn.getUser(buyerId);
                bid = new Bid(auctionId, user, price);
                bids.add(bid);
            }
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
            Logger.getLogger(Connection.class.getName()).log(Level.SEVERE, null, ex);
        }
        return bids;
    }

    /**
     * Adds a new bid to the database. Checks if the person exists and if the
     * user has enough money on his/her account.
     *
     * @param amount the amount of items the user wants to buy.
     * @param auctionID Id of the auction where the bid is place.
     * @param userID Id of the user placing the bid.
     * @param price the price the user is willing to pay for the bid.
     * @return True if did successfully is added the database, false if it
     * failed.
     * @throws SQLException if failed to insert bid into the database.
     */
    public synchronized Boolean addBid(double amount, int auctionID, int userID, double price, long ping) throws SQLException, InterruptedException {
        conn.getConnection();
        User user = userConn.getUser(userID);
        Auction auction = getAuction(auctionID);
        Bid bid = new Bid(auctionID, user, amount, ping, price);
        bids.add(bid);
        wait(200);
        if (bids.size() > 1) {
            if (bids.get(1).getAuctionID() == bids.get(2).getAuctionID() && bids.get(1).getPrice() == bids.get(2).getPrice()) {
                if (bids.get(1).getPing() < bids.get(2).getPing()) {
                    return false;
                }
            }
        }
        // Checks if User exsists
        if (user != null) {
            // Checks if Saldo is high enough
            if (user.getSaldo() >= auction.getCurrentPrice()) {
                try {
                    conn.getConnection();
                    pstmt = myConn.prepareStatement("INSERT INTO bid (amount, placerID, auctionID) VALUES (?,?,?);");
                    pstmt.setDouble(1, price);
                    pstmt.setInt(2, userID);
                    pstmt.setInt(3, auctionID);

                    pstmt.executeQuery();
                    System.out.println("GELUKT!!");
                    myStmt.close();
                    conn.closeConnection();
                    return true;
                } catch (SQLException ex) {
                    conn.closeConnection();
                    myStmt.close();
                    Logger.getLogger(Connection.class.getName()).log(Level.SEVERE, null, ex);
                    System.out.println(ex);
                    return false;
                }
                finally
                {
                    bids.clear();
                }
            } else {
                conn.closeConnection();
                System.out.println("Te weinig Saldo!");
                return false;
            }
        } else {
            System.out.println("User is Null");
            conn.closeConnection();
            return false;
        }
    }

    /**
     * Insert a new auction in the database.
     *
     * @param sellerid The id of the seller.
     * @param productid The id of the product in the auction.
     * @param currentprice The current price of the products in the auction.
     * @param instabuyprice The price to buy products in the auction right away.
     * @param instabuyable boolean to check if product can be brought right
     * away.
     * @param quantity The amount of products in the auction.
     * @param loweringamount The amount used to lower the price.
     * @param loweringdelay The delay used to lower the price.
     * @param type The type of auction.
     * @param status The status of the auction. this can be 1 or 0.
     * @param imgurl Url where the image can be found.
     * @param description Text used to describe the auction.
     * @return True if successfully inserted, false if it failed.
     */
    public Boolean insertAuction(int sellerid, int productid, double currentprice, double instabuyprice, int instabuyable, int quantity, double loweringamount, int loweringdelay, String type, int status, String imgurl, String description) {


        conn.getConnection();
  
        try {
            pstmt = conn.getMyConn().prepareStatement(SET_AUCTION_NEW);
            pstmt.setInt(1, 1);
            pstmt.setInt(2, productid);
            Timestamp created = new Timestamp(System.currentTimeMillis());
            pstmt.setTimestamp(3, created);
            pstmt.setDouble(4, currentprice);
            pstmt.setDouble(5, instabuyprice);
            pstmt.setInt(6, instabuyable);
            pstmt.setInt(7, quantity);
            Timestamp end = new Timestamp(System.currentTimeMillis() + 10000);
            pstmt.setTimestamp(8, end);
            pstmt.setDouble(9, loweringamount);
            pstmt.setInt(10, loweringdelay);
            pstmt.setString(11, type);
            pstmt.setInt(12, status);
            pstmt.setString(13, imgurl);
            pstmt.setString(14, description);

            if (pstmt.executeUpdate() > 0) {
                System.out.println("succesfully registered new queuePurchase: ");
                conn.closeConnection();
                return true;
            } else {
                System.out.println("Couldn't insert new queuePurchase. Rows are unaffected.");
                conn.closeConnection();
                return false;
            }
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
            Logger.getLogger(Connection.class.getName()).log(Level.SEVERE, null, ex);
            conn.closeConnection();
            return false;
        }
    }
    
    /**
     * Closes all Auctions where the amount is less then one.
     * @throws SQLException if connection failed
     */
    public void closeAuction() throws SQLException {
        try {
            conn.getConnection();
            pstmt = conn.getMyConn().prepareStatement(SET_CLOSE_AUCTION);
            myRs = pstmt.executeQuery();

        } catch (Exception ex) {
            System.out.println(ex);
        } finally {
            conn.closeConnection();
        }
    }

    /**
     * Updates the auction given in the parameter.
     *
     * @param auction the auction that need to be updated
     */
    public void updateAuction(Auction auction) {
    }

    /**
     * Buy a instabuy item and put it into the database
     *
     * @param amount The amount the user wants to pay.
     * @param auctionID The id of the auction.
     * @param userID The id of the user.
     * @return True if instabuy item is be brought, false if its not.
     */
    public Boolean InstabuyItem(int amount, int auctionID, int userID) {
        conn.getConnection();
        User user = userConn.getUser(userID);
        Auction auction = getAuction(auctionID);

        // Checks if User exsists
        if (user != null) {
            // Checks if Saldo is high enough
            if (user.getSaldo() >= auction.getCurrentPrice()) {
                try {
                    myConn = DriverManager.getConnection("jdbc:mysql://vserver213.axc.nl:3306/lesleya213_pts?noAccessToProcedureBodies=true", "lesleya213_pts", "wachtwoord123");
                    CallableStatement myStmt = myConn.prepareCall("{call instabuy(?,?,?)}");
                    myStmt.setInt(1, amount);
                    myStmt.setInt(2, auctionID);
                    myStmt.setInt(3, userID);

                    myStmt.execute();
                    System.out.println("GELUKT!!");
                    conn.closeConnection();
                    return true;
                } catch (SQLException ex) {
                    conn.closeConnection();
                    Logger.getLogger(Connection.class.getName()).log(Level.SEVERE, null, ex);
                    System.out.println(ex);
                    return false;
                }
            } else {
                conn.closeConnection();
                System.out.println("Te weinig Saldo!");
                return false;
            }
        } else {
            System.out.println("User is Null");
            conn.closeConnection();
            return false;
        }
    }
}
