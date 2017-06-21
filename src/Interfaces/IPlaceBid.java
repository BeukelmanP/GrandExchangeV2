/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Interfaces;

import Exceptions.NotEnoughMoneyException;
import java.rmi.Remote;



/**
 *
 * @author lesley
 */
 public interface IPlaceBid extends Remote {
    /**
    * Returns a boolean if the bid is placed or not.
    * The amount argument specifies the amount of money the user wants to spend for this bid.
    * The userID argument is the id of the user that places this bid
    * the AuctionID argument is the id of the auction the user wants to place a bid on
    * <p>
    * This method always returns immediately, wheter or not the bid is placed.
    * When the return value is true, the bid is placed.
    * When the return value is false, the user will get an error message why the bid isn't placed and will have to try it again.
    *
    * @param amount    The amount the user wants to bid on an auction
    * @param userID    The id of the user that wants to place a bid
    * @param AuctionID the id of the auction the user wants to place a bid on
    * @return  true if the bid is placed or false if the bid isn't placed
    * @exception java.rmi.RemoteException  this method can throw a RemoteException since it uses RMI
    * @exception NotEnoughMoneyException   Can throw a NotEnoughMoneyException when the user's money balance < amount
    */
    public boolean placeBid(double amount,String userName, int AuctionID, double price, long ping)
            throws java.rmi.RemoteException, NotEnoughMoneyException;
    
    public boolean placeBuy(int amount,String userName, int AuctionID, double price)
            throws java.rmi.RemoteException, NotEnoughMoneyException;
}