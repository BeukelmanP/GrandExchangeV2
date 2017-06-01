/*
 * This project is for PTS3 Fontys Eindhoven
 * Jorian Vas, Kyle van Raaij, Pieter Beukelman, Sam Dirkx, Lesley Peeters, Robin Welten
 * �2016-2017
 */
package Interfaces;

import Classes.User;
import Database.Connection;
import Database.UserConnection;
import grandexchange.RegistryManager;
import java.rmi.RemoteException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Savepoint;
import java.sql.Statement;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Gebruiker
 */
public class IAuthorizationTest {

    private User user;
    private RegistryManager RM;
    private IAuthorized authorization;

    Connection conn;
    java.sql.Connection myConn;
    UserConnection userConn;

    public IAuthorizationTest() {
        RM = new RegistryManager();
        RM.getAuthorizationInterface();
        authorization = RM.getAuthorization();

        conn = new Connection();
        conn.getConnection();
        myConn = conn.getMyConn();
        userConn = new UserConnection();
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    // TODO add test methods here.
    // The methods must be annotated with annotation @Test. For example:
    //
    // @Test
    // public void hello() {}
    public boolean createSavePoint(String name) throws SQLException {
        try {
            Statement stmt = conn.getMyConn().createStatement();
            return stmt.execute("SAVEPOINT " + name);
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }

    public boolean rollBackRelease(String name) throws SQLException {
        boolean success = false;
        boolean successRollback = false;
        boolean successRelease = false;
        Statement stmt = conn.getMyConn().createStatement();

        successRollback = stmt.execute("rollback to savepoint " + name);
        return successRollback;
//        if (successRollback)
//        {
//            successRelease = stmt.execute("release savepoint " + name);       
//        }
//        
//        if (successRollback && successRelease)
//        {
//            success = true;
//        }
//        return success;
    }

//    /**
//     * tests if it's possible to create a savepoint.
//     * note: doesn't work
//     * @throws SQLException 
//     */
//    @Test
//    public void createSavepoint() throws SQLException {
//        assertTrue(this.createSavePoint("testSave2"));
//    }
//
//    /**
//     * tests if it's possible to rollback a savepoint.
//     * note: doesn't work
//     * @throws SQLException 
//     */
//    @Test
//    public void rollbackSavepoint() throws SQLException {
//        assertTrue(this.rollBackRelease("testSave2"));
//    }


    @Test
    public void checkAuthorizationNotNull() {
        assertNotNull(RM.getAuthorization());
    }

    /**
     * login test met correcte gegevens
     *
     * @throws RemoteException
     * @throws SQLException
     */
    @Test
    public void loginCorrect() throws RemoteException, SQLException {
        //       this.createSavePoint("testSave");

        String username = "test1111";
        String password = "password";

        user = this.authorization.login(username, password);

        assertNotNull(user);

        //       this.rollBackRelease("testSave");
    }

    /**
     * login test met incorrecte gegevens
     *
     * @throws RemoteException
     * @throws SQLException
     */
    @Test
    public void loginIncorrect() throws RemoteException, SQLException {
        //       this.createSavePoint("testSave");

        String username = "username";
        String password = "password";
        assertNull(this.authorization.login(username, password));

        //       this.rollBackRelease("testSave");
    }

    /**
     * logs user with specified username out of the application. boolean
     * isAuthorized of this user is set to false.
     *
     * @param username
     * @return success of the operation
     * @throws RemoteException
     */
    @Test
    public void logout() throws RemoteException, SQLException {
        //       this.createSavePoint("testSave");

        String username = "test1111";
        String password = "password";
        User user = this.authorization.login(username, password);

        assertNotNull("user specified didn't login, therefore also can't logout", user);
        assertTrue(this.authorization.logout(user.getUsername()));

        //       this.rollBackRelease("testSave");
    }

    /**
     * registers a new user to the application.
     *
     * @throws RemoteException
     * @throws java.sql.SQLException
     */
    @Test
    public void registerUserCorrect() throws RemoteException, SQLException {
        //      this.createSavePoint("testSave");

        String username = "testRegister";
        String password = "password";
        String alias = "xTestRegister";
        String email = "testRegister@email.com";

        assertNull("user is allready registered in the database", userConn.getUser(username, password));
        assertEquals("Succesfully registered new user!", this.authorization.registerUser(username, password, alias, email));

        //      this.rollBackRelease("testSave");
    }

    /**
     * doesn't register a new user that has no fields specified.
     *
     * @throws RemoteException
     * @throws java.sql.SQLException
     */
    @Test
    public void registerUserEmptyFields() throws RemoteException, SQLException {
        //      this.createSavePoint("testSave");

        String username = "";
        String password = "";
        String alias = "";
        String email = "";

        assertNotEquals("Succesfully registered new user!", this.authorization.registerUser(username, password, alias, email));

        //      this.rollBackRelease("testSave");
    }

    /**
     * registers a new user to the application.
     *
     * @throws RemoteException
     * @throws java.sql.SQLException
     */
    @Test
    public void registerUserAlreadyRegistered() throws RemoteException, SQLException {
        //      this.createSavePoint("testSave");

        String username = "testRegister";
        String password = "password";
        String alias = "xTest5555";
        String email = "test5555@email.com";

        try {
            this.authorization.registerUser(username, password, alias, email);
        } catch (Exception ex) {
        }

        assertNotNull("user is not yet registered in the database", userConn.getUser(username, password));
        assertNotEquals("Succesfully registered new user!", this.authorization.registerUser(username, password, alias, email));

        //       this.rollBackRelease("testSave");
    }

    /**
     * authorizes a user
     *
     * @throws RemoteException
     */
    @Test
    public void setIsAuthorizedToTrue() throws RemoteException, SQLException {
        //       this.createSavePoint("testSave");

        String username = "testAuthorized";
        String password = "password";
        boolean isAuthorized = true;

        assertNotNull("user is not yet registered in the database", userConn.getUser(username, password));
        assertNotNull("can't login user", this.authorization.login(username, password));
        assertTrue(this.authorization.setIsAuthorized(username, isAuthorized));

        //      this.rollBackRelease("testSave");
    }

    /**
     * unauthorizes a user
     *
     * @throws RemoteException
     */
    @Test
    public void setIsAuthorizedToFalse() throws RemoteException, SQLException {
//        this.createSavePoint("testSave");

        String username = "testAuthorized";
        String password = "password";
        boolean isAuthorized = false;

        assertNotNull("user is not yet registered in the database", userConn.getUser(username, password));
        assertNotNull("can't login user", this.authorization.login(username, password));
        assertTrue(this.authorization.setIsAuthorized(username, isAuthorized));

        //      this.rollBackRelease("testSave");
    }

    /**
     * Get the current logged in user.
     *
     * @return User that is logged in at this moment
     * @throws RemoteException
     */
    @Test
    public void getLoggedInUser() throws RemoteException, SQLException {
//        this.createSavePoint("testSave");

        String username = "testAuthorized";
        String password = "password";

        assertNotNull("user is not yet registered in the database", userConn.getUser(username, password));
        assertNotNull("can't login user", this.authorization.login(username, password));
        assertNotNull(this.authorization.getLoggedInUser());

        //      this.rollBackRelease("testSave");
    }
}
