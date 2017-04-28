/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package wtserver.server;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Random;
import wtserver.GameServer;
import wtserver.client.ServerMsg;

/**
 *
 * @author MSI
 */
public class FieldListAck extends ServerMsg {
    private final short msgId = ServerMsg.CS_FD_FIELDLIST_ACK;
    private ArrayList<GameServer.Room> rooms;
    
    public FieldListAck(ArrayList<GameServer.Room> rooms)
    {
        size = 8;
        buffer = ByteBuffer.allocate(8192);
        this.rooms = rooms;
    }

    public byte [] getData(short seqNum)
    {
        buffer.position(0);
        byte random = (byte) new Random().nextInt();
        addByte(random);
        addShort(msgId);
        addShort(seqNum);
        addShort((short)0);
        addByte((byte)0);

        addByte((byte) 0);
        addByte((byte) 0);
        addByte((byte) 0);
        addByte((byte) rooms.size());//n rooms
        for(int i = 0; i < rooms.size(); i++)
        {
            GameServer.Room room = rooms.get(i);
            addShort((short) room.getRoomNumber());//Room Number
            String title = room.getRoomTitle();
            addByte((byte) (title.length() * 2));
            addUnicodeString(title);
            addByte((byte) 0);
            addByte((byte) 0);//Waiting / Playing
            addByte((byte) room.getMap());//Map
            addByte((byte) room.getMode());//Mode
            addByte((byte) 0);
            addByte((byte) 0);
            addByte((byte) 0);
            addByte((byte) 5);
            addShort((short) room.getTime()); //Time in seconds
            addByte((byte) room.getPoints()); //Points
            addByte((byte) 0);
            addByte((byte) 2);
            addByte((byte) 0);
            addByte((byte) 0xC0);
            addByte((byte) 0);
            addByte((byte) room.getCapacity());//Max Players
            addByte((byte) room.getNumberOfUsers());//Current Players
            addInteger(0);
            addByte((byte) 0x3D);
            
        }
        
        
        size = (short) buffer.position();
        if((size - 8) % 16 > 0)
        {
            size -= (size - 8) % 16;
            size += 16;
        }
        short pSize = (short) ((size - 8) / 16);
        buffer.position(5);
        addShort(pSize);
        byte checksum = 0;
        for(int i = 0; i < 7; i++)
        {
            checksum += buffer.get(i);
        }
        addByte(checksum);
        
        
        return buffer.array();
    }
}
