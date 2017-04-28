/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package wtserver;

import java.util.ArrayList;
import java.util.Arrays;
import wtserver.client.ChannelUser;
/**
 *
 * @author MSI
 */
public class Server {
    private ArrayList<Client> clientList = new ArrayList<Client>();
    public int port;
    public Server(int port)
    {
        this.port = port;
        Thread thread = new Thread() {
            public void run() {
                startServer();
            }
        };
        thread.start();
    }
    
    public synchronized void addClient(Client c)
    {
        clientList.add(c);
    }
    public synchronized void removeClient(Client c)
    {
        clientList.remove(c);
    }
    
    public synchronized Client getClient(byte session_key[])
    {
        for(int i = 0; i < clientList.size(); i++)
        {
            Client c = clientList.get(i);
            if(Arrays.equals(c.getSessionKey(), session_key))
                return c;
        }
        return null;
    }
    
    public synchronized ArrayList<Client> getClientList()
    {
        return clientList;
    }
    
    public synchronized ArrayList<ChannelUser> getChannelUsers()
    {
        ArrayList<ChannelUser> users = new ArrayList<ChannelUser>();
        for(int i = 0; i < clientList.size(); i++)
        {
            Client c = clientList.get(i);
            if(c.roomNumber == -1)
            {
                ChannelUser u = new ChannelUser(c.getLoginName(), c.getCallsign(), c.getUserLevel());
                users.add(u);
            }
        }
        return users;
    }
    
    public void startServer(){
    }
    
    public void processMsg(Client client, int msgId, byte buffer [])
    {
        
    }
}
