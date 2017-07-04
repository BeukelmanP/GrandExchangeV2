/*
 * This project is for PTS3 Fontys Eindhoven
 * Jorian Vas, Kyle van Raaij, Pieter Beukelman, Sam Dirkx, Lesley Peeters, Robin Welten
 * �2016-2017
 */
package Classes.Auctions;

import fontyspublisher.IRemotePropertyListener;
import java.rmi.RemoteException;

/**
 *
 * @author SwekLord420
 */
public interface IAuctionInfo {
    
    public void subscribe(IRemotePropertyListener listener, String property) throws RemoteException;
    public void unSubscribe(IRemotePropertyListener listener, String property) throws RemoteException;
    public String[] getImageURLs() throws RemoteException;
    public StatusEnum getStatus() throws RemoteException;
    public String getDescription() throws RemoteException;
    public String getProductDescription() throws RemoteException;
    public String getType() throws RemoteException;
    public String getSellerName() throws RemoteException;
    public Double getCurrentPrice() throws RemoteException;
    public double getInstabuyPrice() throws RemoteException;
    public int getProductQuantity() throws RemoteException;
    
    
    
}
