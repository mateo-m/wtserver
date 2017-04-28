/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package wtserver.server;

import java.nio.ByteBuffer;
import java.util.Random;
import wtserver.Client;
import wtserver.client.ServerMsg;

/**
 *
 * @author MSI
 */
public class ChangeRuleAck extends ServerMsg {
    private final short msgId = ServerMsg.CS_FD_CHANGERULE_ACK;
    private String roomTitle = "";
    private String roomPassword = "";
    private byte map, mode, wins, points, capacity;
    private short time;
    
    public ChangeRuleAck(byte recv_buffer [])
    {
        size = 8;
        buffer = ByteBuffer.allocate(512);
        short b = recv_buffer[0];
        for(int i = 0; i < b; i+=2)
        {
            char c = (char) recv_buffer[1 + i];
            roomTitle += c;
        }
        short b1 = recv_buffer[1 + b];
        for(int i = 0; i < b1; i+=2)
        {
            char c = (char) recv_buffer[2 + b + i];
            roomPassword += c;
        }
        int index = b1 + b + 2;
        map = recv_buffer[index];
        index += 1;
        mode = recv_buffer[index];
        //unks
        index += 4;
        wins = recv_buffer[index];
        //index += 1; // 5
        index += 2;
        time = (short) ( (recv_buffer[index] & 0xff) + ((recv_buffer[index + 1] & 0xff) * 0x100));
        index += 2;
        points = recv_buffer[index];
        recv_buffer[index + 8] = points;
        recv_buffer[index + 9] = 0;
        index += 2;
        index += 4; //02 00 C0 00
        capacity = recv_buffer[index];
        System.out.println("Map " + map + " Mode " + mode + " Time " + time + " Pt/Capacity " + points + " " + capacity );
        
        buffer.position(9);
        for(int i = 0; i < 240 && i < recv_buffer.length; i++)
            addByte(recv_buffer[i]);
        size = (short) buffer.position();
    }
    
    public byte [] getData(short seqNum)
    {
        buffer.position(0);
        byte random = (byte) new Random().nextInt();
        addByte(random);
        addShort(msgId);
        addShort(seqNum);
        addShort((short)0);
        byte checksum = 0;
        addByte(checksum);
        
        addByte((byte) 0);
        
        
        
        
        if((size - 8) % 16 > 0)
        {
            size -= (size - 8) % 16;
            size += 16;
        }
        short pSize = (short) ((size - 8) / 16);
        buffer.position(5);
        addShort(pSize);
        for(int i = 0; i < 7; i++)
        {
            checksum += buffer.get(i);
        }
        addByte(checksum);
        return buffer.array();
    }
 
    public String getRoomTitle()
    {
        return roomTitle;
    }
    
    public String getRoomPassword()
    {
        return roomPassword;
    }
    
    public byte getMap()
    {
        return map;
    }
    
    public byte getMode()
    {
        return mode;
    }

    public byte getWins()
    {
        return wins;
    }
    
    public byte getPoints()
    {
        return points;
    }
        
    public byte getCapacity()
    {
        return capacity;
    }
            
    public short getTime()
    {
        return time;
    }
}