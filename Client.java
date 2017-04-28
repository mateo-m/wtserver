/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package wtserver;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.security.SecureRandom;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import wtserver.client.ClientMsg;
import wtserver.client.ServerMsg;

/**
 *
 * @author MSI
 */
public class Client {
    private Server server;
    private Socket socket;
    private byte recv_buffer[] = new byte [8192];
    private byte send_buffer[] = new byte [8192];
    private byte session_key[] = new byte [6];//{0x31, 0x08, 0x07, (byte)0xF0, 0x18, 0x04};//new byte [6];
    DataInputStream in = null;
    DataOutputStream out = null;
    short seqNum = 0;
    int udpIp = 0;
    short udpPort = 0;
    int Ip = 0;
    short Port = 0;
    short roomNumber = -1, roomSlot = -1;
    byte roomTeam = -1, roomTeamSlot = -1, roomSlotStatus = 0;
    byte userLevel = 0;
    int user_id = 0;
    
    private String loginName, callSign;
    
    public Client(Server server, Socket socket)
    {
        Random ranGen = new SecureRandom();
        ranGen.nextBytes(session_key);
        session_key[1] = 0x08;
        session_key[2] = 0x07;
        session_key[3] = (byte)0xF0;
        session_key[4] = 0x18;
        session_key[5] = 0x04;
        
        byte ip [] = socket.getInetAddress().getAddress();
        Ip = (ip[0] & 0xff) + (ip[1] & 0xff)*0x100 + (ip[2] & 0xff)*0x10000 + (ip[3] & 0xff)*0x1000000;
        Port = (short) socket.getPort();
        this.server = server;
        this.socket = socket;
        System.out.println("Client Connected: " + this.socket.getInetAddress().toString() + " " + ip);
        Thread thread = new Thread() {
            public void run() {
                try {
                    startRecieving();
                } catch (IOException ex) {
                    System.out.println(ex);
                }
                exit();
            }
        };
        thread.start();
    }
    
    public void startRecieving() throws IOException
    {
        in = new DataInputStream(socket.getInputStream());
        out = new DataOutputStream(socket.getOutputStream());
        while(true)
        {
            byte header[] = {0,0,0,0,0,0,0,0};
            try {
                in.readFully(header, 0, 8);
            }catch(Exception e)
            {
                System.out.println(e);
                break;
            }
            
            byte random = header[0];
            short msgId = (short) ((header[1] & 0xff) + ((header[2] & 0xff)*0x100));
            short dataSize = (short) ((header[3] & 0xff) + ((header[4] & 0xff)*0x100));
            //short _packetSize = in.readShort();
            byte checksum = header[7];
            dataSize = (short) (dataSize - 8);
            //System.out.println("Msg " + msgId + " dataSize " + dataSize);
            if(dataSize > 0)
            {
                try {
                    in.readFully(recv_buffer, 0, dataSize);
                } catch (Exception e) {
                    System.out.println(e);
                    break;
                }
            }
              
            server.processMsg(this, msgId, recv_buffer);
        }
            
    }
    
    public synchronized void write(byte buffer [], short size)
    {
        try {
            out.write(buffer, 0, size);
            out.flush();
        } catch (IOException ex) {
            System.out.println(ex);
        }
    }
    
    public synchronized short getSeqNum()
    {
        short temp = seqNum;
        seqNum++;
        return temp;
    }
    
    public byte [] getSessionKey()
    {
        return session_key;
    }
    
    public void setLoginName(String loginName)
    {
        this.loginName = loginName;
    }
    
    public String getLoginName()
    {
        return loginName;
    }
    
    public String getCallsign()
    {
        return callSign;
    }
    
    public byte getUserLevel()
    {
        return userLevel;
    }
    public void setUserLevel(byte level)
    {
        userLevel = level;
    }
    public void UdpAddr(int udpIp, short udpPort)
    {
        this.udpIp = udpIp;
        this.udpPort = udpPort;
        server.processMsg(this, ClientMsg.CS_CH_UDPADDR_REQ, null);
    }
    
    public int getIp() {
        return Ip;
    }

    public short getPort() {
        return Port;
    }

    public int getUdpIp() {
        return udpIp;
    }

    public short getUdpPort() {
        return udpPort;
    }
    
    public void setRoomNumber(short roomNumber) {
        this.roomNumber = roomNumber;
    }
    public void setRoomSlot(short roomSlot) {
        this.roomSlot = roomSlot;
    }
    public void setRoomTeam(byte roomTeam) {
        this.roomTeam = roomTeam;
    }
    public void setRoomTeamSlot(byte roomTeamSlot) {
        this.roomTeamSlot = roomTeamSlot;
    }
    public void setRoomSlotStatus(byte roomSlotStatus)
    {
        this.roomSlotStatus = roomSlotStatus;
    }
    public void setUserId(int user_id)
    {
        this.user_id = user_id;
    }
    public void setCallsign(String callSign)
    {
        this.callSign = callSign;
    }
    public int getUserId()
    {
        return user_id;
    }
    public short getRoomNumber() {
        return roomNumber;
    }
    public short getRoomSlot() {
        return roomSlot;
    } 
    public byte getRoomTeam() {
        return roomTeam;
    }
    public byte getRoomTeamSlot() {
        return roomTeamSlot;
    }
    public byte getRoomSlotStatus()
    {
        return roomSlotStatus;
    }
    public short getSessionId()
    {
        return (short) ((session_key[0] & 0xff) + (session_key[1]*0x100));
    }
    
    public void exit()
    {
        server.removeClient(this);
    }
}
