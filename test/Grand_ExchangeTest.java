/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Classes;

import Classes.Auctions.Auction;
import Classes.Auctions.StatusEnum;
import Database.AuctionConnection;
import Database.ProductConnection;
import Exceptions.NotEnoughMoneyException;
import java.rmi.RemoteException;
import java.util.Collection;
import junit.framework.Assert;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Robin
 */
public class Grand_ExchangeTest {

    User TestUser;
    Grand_Exchange GE;
    Product p;
    Auction a;
    Transaction trans;
    ProductConnection productCon = new ProductConnection();
    AuctionConnection aucCon = new AuctionConnection();

    @Before
    public void setUp() {
        //gebruiker wachtwoord
        TestUser = new User("Robin", "test", "welten", "robin.welten@planet.nl", true, 10000, "imagelink");

    }
/** 
   * A user will be added
   * the name can't be empty
   */
    @Test
    public void TestaddUser() {
        GE.addUser(TestUser);
        assertEquals(TestUser.getUsername(), "Robin");
    }
/** 
   * A user will be removed.
   * The name can't be empty
   */
    @Test
    public void TestremoveUser() {
        GE.addUser(TestUser);
        assertEquals(TestUser.getUsername(), "Robin");
        GE.removeUser(TestUser);
        assertNull(TestUser.getUsername());

    }
/** 
   * a product will be added.
   * 
   */
    @Test
    public void TestAddProduct() {
        p = new Product(1,"1234", "testprodcut", "gewoon een test");
        GE.addProduct(p);
        assertEquals(p.getName(), "testproduct");

    }
/** 
   * a product will be removed.
   *the product must exist.
   */
    @Test
    public void TestRemoveProduct() {
        p = new Product(1, "1234", "testprodcut", "gewoon een test");
        assertEquals(p.getName(), "testproduct");
        GE.removeProduct(p);
        assertNull(p.getName());

    }
/** 
   * a auction will be added.
   * 
   */
    @Test
    public void TestAddAuction() {
        p = new Product(1,"1234", "testprodcut", "gewoon een test");
        a = new Auction(TestUser, p, 15, 2, StatusEnum.New, "test",10);
        GE.addAuction(a);
        Collection<Auction> auc;
        auc = GE.getAuctions();
        assertEquals(auc.size(), 1);

    }
/** 
   * a auction will be removed.
   *
   */
    @Test
    public void TestRemoveAuction() {
        Collection<Auction> auc;
        auc = GE.getAuctions();
        int i = auc.size();
        a = new Auction(TestUser, p, 15, 2, StatusEnum.GoodAsNew, "test",10);
        GE.addAuction(a);
        assertEquals(auc.size(), i);
        GE.removeAuction(a);
        assertEquals(auc.size(), i-1);

    }
/** 
   * a transaction will be handled
   * 
   */
    @Test
    public void TesthandleTransaction() {
        trans = new Transaction(a);
        GE.handleTransaction(trans);
        Collection<Transaction> col = TestUser.getTransactionHistory();
        assertEquals(col.size(), 1);
    }
    
    @Test
    public void TestaddProductToDB()
    {
        p = new Product(666, "1234", "testprodcut", "gewoon een test");
        GE.addProductToDB(p.getName(), p.getDescription(), p.getId());
        assertEquals(productCon.getProduct(666).getId(), 666);
        
    }
   
    
    @Test
    public void TestaddAuctionToDB()
    {
        a = new Auction(TestUser, p, 15, 2, StatusEnum.GoodAsNew, "test",10);
        GE.addAuctionToDB(TestUser.getUserID(), p.getId(), 500, 300, 1,5, 20, 200, "nieuw", 1, "imgurl", "test Acitopm");
        assertEquals(aucCon.getAuction(a.getId()), 500);
    }
    
    @Test
    public void TestLogin() throws RemoteException
    {
   
             assertEquals(GE.login("Robin", "test").getUserEmail(), "Robin.welten@planet.nl");
    }
    @Test
    public void TestgetUser() throws RemoteException
    {
        User r = GE.getUser("robin");
        assertEquals(r.getUserEmail(), "Robin.welten@planet.nl");
    }
    
    @Test
    public void TestregisterUser()
    {
        assertEquals(GE.registerUser("moob", "test1234", "robin", "robin@weltendienstverlening.nl"), "Succesfully registered new user!");
    }
    
    @Test 
    public void TestRegisterUserWrong()
    {
     assertEquals(GE.registerUser("robin", "test1234", "robin", "robin@weltendienstverlening.nl"), "\n -Username is already used");

    }
    
    @Test
    public void TestplaceBid() throws RemoteException, NotEnoughMoneyException
    {
       assertTrue(GE.placeBid(1, "Robin", 1, 500, 20)); 
       assertFalse(GE.placeBid(1, "moob", 1, 500, 10));
    }
    
    @Test
    public void TestLogout() throws RemoteException
    {
        GE.login("robin", "1234");
        assertTrue(GE.logout("robin"));
    }
    
    @Test 
    public void TestLogoutFail() throws RemoteException
    {
        assertFalse(GE.logout("robin"));
    }
        
    
    @Test
    public void TestgenerateAndSendEmail()
    {
        
    }

    
    public void TestsetIsAuthorized() throws RemoteException
    {
        assertTrue(GE.setIsAuthorized("robin", true));
    }

}

