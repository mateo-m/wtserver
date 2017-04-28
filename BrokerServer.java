/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package wtserver;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import wtserver.client.ClientMsg;
import wtserver.server.broker.*;

/**
 *
 * @author MSI
 */
public class BrokerServer extends Server {

    public BrokerServer(int port)
    {
        super(port);
    }
    
    @Override
    public void startServer() {
        try {
            ServerSocket socket = new ServerSocket(port);
            System.out.println("Broker Server Started on port " + port);
            while (true) {
                Socket connectionSocket = socket.accept();
                addClient(new Client(this, connectionSocket));
            }
        } catch (IOException ex) {
            System.out.println(ex);
        }
    }
    
    @Override
    public void processMsg(Client client, int msgId, byte buffer [])
    {
        if(msgId == ClientMsg.CS_BR_CHAINLIST_REQ)
        {
            ChainListAck chainListAck = new ChainListAck();
            client.write(chainListAck.getData(client.getSeqNum()), chainListAck.getSize());
        }else if(msgId == ClientMsg.CS_BR_WORLDLIST_REQ)
        {
            WorldListAck worldListAck = new WorldListAck();
            client.write(worldListAck.getData(client.getSeqNum()), worldListAck.getSize());
        }else if(msgId == ClientMsg.CS_BR_WORLDINFO_REQ)
        {
            WorldInfoAck worldInfoAck = new WorldInfoAck();
            client.write(worldInfoAck.getData(client.getSeqNum()), worldInfoAck.getSize());
        }else if(msgId == ClientMsg.CS_BR_RELAYLIST_REQ)
        {
            RelayListAck relayListAck = new RelayListAck();
            client.write(relayListAck.getData(client.getSeqNum()), relayListAck.getSize());
        }
    }
}
