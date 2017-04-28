/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package wtserver.server;

import java.nio.ByteBuffer;
import java.util.Random;
import wtserver.client.ServerMsg;

/**
 *
 * @author MSI
 */
public class CreateAck extends ServerMsg {
    private final short msgId = ServerMsg.CS_FD_CREATE_ACK;
    private String roomTitle = "";
    private String roomPassword = "";
    private byte map, mode, wins, points, capacity;
    private short time;
    
    public CreateAck(byte recv_buffer [])
    {
        size = 24;
        buffer = ByteBuffer.allocate(size);
        String hash1 = "";
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
        index += 2;
        index += 4; //02 00 C0 00
        capacity = recv_buffer[index];
        System.out.println("Map " + map + " Mode " + mode + " Time " + time + " Pt/Capacity " + points + " " + capacity );
    }
    
    public byte [] getData(short seqNum)
    {
        buffer.position(0);
        byte random = (byte) new Random().nextInt();
        addByte(random);
        addShort(msgId);
        addShort(seqNum);
        short pSize = (short) ((size - 8) / 16);
        addShort(pSize);
        byte checksum = 0;
        for(int i = 0; i < 7; i++)
        {
            checksum += buffer.get(i);
        }
        addByte(checksum);

        addByte((byte) 0);
        addByte((byte) 0xB0);
        addByte((byte) 0xEC);
        addByte((byte) 0x8E);
        addByte((byte) 0x6);
        addByte((byte) 0);
        addByte((byte) 0);
        addByte((byte) 0);
        addByte((byte) 0x3D);
        addByte((byte) 0);
        
        for(int i = buffer.position(); i < buffer.capacity(); i++)
        {
            addByte((byte)0);
        }
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