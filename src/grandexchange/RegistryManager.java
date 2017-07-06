/*
 * This project is for PTS3 Fontys Eindhoven
 * Jorian Vas, Kyle van Raaij, Pieter Beukelman, Sam Dirkx, Lesley Peeters, Robin Welten
 * �2016-2017
 */
package grandexchange;

import Classes.User;
import Controllers.LoginController;
import Database.DatabaseListener;
import Interfaces.IAuction;
import Interfaces.IAuthorized;
import Interfaces.ICreateProduct;
import Interfaces.ICreateQueuePurchase;
import Interfaces.IPlaceBid;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Jorian
 */
public class RegistryManager {

    //User
    User user;

    //Interfaces
    private IAuthorized authorization;
    private IAuction auction;
    private IPlaceBid bid;
    private ICreateProduct product;
    private ICreateQueuePurchase queuePurchase;

    // Set port number
    private static final int portNumber = 1099;

    // Set binding name for Grand Exchange
    private static final String bindingName = "GrandExchangeServer";

    // References to registry and Grand Exchange
    private Registry registry = null;
    private InetAddress localhost;
    private String ipAddress = "169.254.91.174";

    public RegistryManager() {
        //getLocalHostIp();
        setupRegistry();
    }

    public IPlaceBid getBid() {
        return bid;
    }

    public ICreateProduct getProduct() {
        return product;
    }
public String getIp() {
        return ipAddress;
    }
    public ICreateQueuePurchase getQueuePurchase() {
        return queuePurchase;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public IAuthorized getAuthorization() {
        return authorization;
    }

    public IAuction getAuction() {
        return auction;
    }

    public void getAuthorizationInterface() {
        if (registry != null) {
            try {
                System.out.println("Trying to lookup Authorization Interface...");
                authorization = (IAuthorized) registry.lookup(bindingName);
                System.out.println("Interface reference IS bound.");

                this.authorization = authorization;

            } catch (RemoteException | NotBoundException ex) {
                                                       Logger.getLogger(RegistryManager.class.getName()).log(Level.SEVERE, null, ex);
 System.out.println("Client: Cannot bind Autherization interface");
                System.out.println("Client: RemoteException: " + ex.getMessage());
                System.out.println("Interface reference is NOT bound");
                authorization = null;

            }
        }
    }

    public void getPlaceBidInterface() {
        if (registry != null) {
            try {
                System.out.println("Trying to lookup Bid Interface...");
                this.bid = (IPlaceBid) registry.lookup(bindingName);
                System.out.println("Interface reference IS bound.");

            } catch (RemoteException | NotBoundException ex) {
                                                        Logger.getLogger(RegistryManager.class.getName()).log(Level.SEVERE, null, ex);
System.out.println("Client: Cannot bind Bid Interface");
                System.out.println("Client: RemoteException: " + ex.getMessage());
                System.out.println("Interface reference is NOT bound");
                this.bid = null;

            }
        }
    }

    public void getAuctionInterface() {
        if (auction == null) {
            if (registry != null) {
                try {
                    System.out.println("Trying to lookup Auction Interface...");
                    auction = (IAuction) registry.lookup(bindingName);
                    System.out.println("Interface reference IS bound.");

                } catch (RemoteException | NotBoundException ex) {
                                                            Logger.getLogger(RegistryManager.class.getName()).log(Level.SEVERE, null, ex);
System.out.println("Client: Cannot bind Auction Interface");
                    System.out.println("Client: RemoteException: " + ex.getMessage());
                    System.out.println("Interface reference is NOT bound");
                    this.auction = null;
                }
            }
        }
    }

    public void getProductInterface() {
        if (product == null) {
            if (registry != null) {
                try {
                    System.out.println("Trying to lookup Product Interface...");
                    this.product = (ICreateProduct) registry.lookup(bindingName);
                    System.out.println("Interface reference IS bound.");

                } catch (RemoteException | NotBoundException ex) {
                                                            Logger.getLogger(RegistryManager.class.getName()).log(Level.SEVERE, null, ex);
System.out.println("Client: Cannot bind Product Interface");
                    System.out.println("Client: RemoteException: " + ex.getMessage());
                    System.out.println("Interface reference is NOT bound");
                    this.product = null;
                }
            }
        }
    }

    public void getQueuePurchaseInterface() {
        if (registry != null) {
            try {
                System.out.println("Trying to lookup QueuePurchase Interface...");
                this.queuePurchase = (ICreateQueuePurchase) registry.lookup(bindingName);
                System.out.println("Interface reference IS bound.");

            } catch (RemoteException | NotBoundException ex) {
                                                        Logger.getLogger(RegistryManager.class.getName()).log(Level.SEVERE, null, ex);
System.out.println("Client: Cannot bind QueuePurchase Interface");
                System.out.println("Client: RemoteException: " + ex.getMessage());
                System.out.println("Interface reference is NOT bound");
                this.queuePurchase = null;
            }
        }
    }

    public void getLocalHostIp() {

        try {
            localhost = InetAddress.getLocalHost();
            ipAddress = localhost.getHostAddress();
        } catch (UnknownHostException ex) {
            Logger.getLogger(LoginController.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public void setupRegistry() {

        // Locate registry at IP address and port number
        try {
            registry = LocateRegistry.getRegistry("169.254.91.174", portNumber);

            if (registry != null) {
                System.out.println("Client: Registry located");
            }

        } catch (RemoteException ex) {
                                        Logger.getLogger(RegistryManager.class.getName()).log(Level.SEVERE, null, ex);
System.out.println("Client: Cannot locate registry");
            System.out.println("Client: RemoteException: " + ex.getMessage());
            registry = null;
            System.out.println("Client reference = null");
        }

    }

}
