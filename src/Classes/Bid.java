package Classes;

import Classes.Auctions.Auction;
import java.io.Serializable;
import java.util.Date;

public class Bid implements Serializable {

    private double amount;
    private Date timeCreated;
    private User placer;
    private int auctionId;
    private long ping;
    private double price;
    /**
     * 
     * @param placer : may not be null
     * @param amount : must be higher than 0
     */
    public Bid(int auctionId, User placer, double amount) {
        if (placer == null || amount <= 0) {
            throw new IllegalArgumentException();
        } else {
            this.placer = placer;
            this.amount = amount;
            this.auctionId = auctionId;
            Date date = new Date();
            this.timeCreated = date;
        }
    }
    
      public Bid(int auctionId, User placer, double amount, long ping) {
        if (placer == null || amount <= 0) {
            throw new IllegalArgumentException();
        } else {
            this.placer = placer;
            this.amount = amount;
            this.auctionId = auctionId;
            Date date = new Date();
            this.timeCreated = date;
            this.ping = ping;
        }
    }

      
            public Bid(int auctionId, User placer, double amount, long ping, double price) {
        if (placer == null || amount <= 0) {
            throw new IllegalArgumentException();
        } else {
            this.placer = placer;
            this.amount = amount;
            this.auctionId = auctionId;
            Date date = new Date();
            this.timeCreated = date;
            this.ping = ping;
            this.price = price;
        }
    }
    /**
     * This method is used to get a string of the placer of this bid.
     *
     * @return User
     */
    public String getPlacerUsername() {
        return this.placer.getUsername();
    }

    /**
     * returns the bids amount
     *
     * @return double
     */
    public double getAmount() {
        return amount;
    }

//    public Auction getAuction() {
//        return auction;
//    }
public long getPing() {
        return ping;
    }

public double getPrice() {
        return price;
    }

public int getAuctionID() {
        return auctionId;
    }
}
