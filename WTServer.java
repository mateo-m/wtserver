/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package wtserver;

import java.io.IOException;


/**
 *
 * @author MSI
 */
public class WTServer {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        System.out.println("WT Server v0.1");
        BrokerServer  broker = new BrokerServer(40706);
        GameServer server1 = new GameServer(40707);
        //GameServer server2 = new GameServer(40708);
        //GameServer server3 = new GameServer(40709);
        System.out.println("Press enter to exit.");
        try {
            System.in.read();
        } catch (IOException ex) {
            System.out.println(ex);
        }
        System.exit(0);
    }
    
}
