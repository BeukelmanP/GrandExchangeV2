/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Interfaces;

import java.rmi.RemoteException;

/**
 *
 * @author piete
 */
public interface IAuthorized {
    boolean login (String username, String password) throws RemoteException;
}
