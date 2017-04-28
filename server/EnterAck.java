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
public class EnterAck extends ServerMsg {
    private final short msgId = ServerMsg.CS_FD_ENTER_ACK;
    short roomNumber;
    public static byte ROOM_OK = 0, ROOM_FULL = 0x7D, WRONG_PASS = 0x7A, DOES_NOT_EXIST = 0x7E, ALMOST_OVER = (byte) 0x8B;
    
    public EnterAck(byte recv_buffer [])
    {
        size = 8;
        buffer = ByteBuffer.allocate(512);
        roomNumber = (short) ((recv_buffer[0] & 0xff) + (recv_buffer[1]*0x100));
        System.out.println("Joining " + roomNumber);
    }
    
    public byte [] getData(short seqNum, Client c, byte status)
    {
        buffer.position(0);
        byte random = (byte) new Random().nextInt();
        addByte(random);
        addShort(msgId);
        addShort(seqNum);
        addShort((short)0);
        byte checksum = 0;
        addByte(checksum);
        
        addByte((byte) status);
        addByte((byte) 0xB0);
        addByte((byte) 0xEC);
        addByte((byte) 0x8E);
        addByte((byte) 0x6);
        
        addShort((short) 0);
        addShort((short) c.getRoomSlot()); //room Number
        addShort((short) 0);
        addByte((byte) 0);
        addByte((byte) c.getRoomTeam()); //Team
        addByte((byte) c.getRoomTeamSlot()); //Slot
        //addShort((short) 2); //Team
        addShort(c.getSessionId());//addInteger(2096);
        addShort((short) 0);
        addByte((byte) c.getUserLevel()); //level
        addInteger(0);
        addInteger(0);
        
        String id = c.getLoginName();
        int n = id.length();
        addByte((byte) n);
        addAsciiString(id);
        addByte((byte) (n*2));
        addUnicodeString(id);
        addShort((short) 0);
        addInteger(4);
        addInteger(0);
        addInteger(0);
        addInteger(0);
        addInteger(0);
        addInteger(0);
        addInteger(0);
        addInteger(0);
        addInteger(0x000107D1);
        addInteger(0);
        addInteger(0);
        addInteger(0);
        addInteger(0);
        addInteger(0);
        addInteger(0);
        addInteger(0);
        addByte((byte) 0);
        addInteger(0x000207D1);
        addInteger(0);
        addInteger(0);
        addInteger(0);
        addInteger(0);
        addInteger(0);
        addInteger(0);
        addInteger(0);
        addByte((byte) 0);
        addInteger(0x000307D1);
        addInteger(0);
        addInteger(0);
        addInteger(0);
        addInteger(0);
        addInteger(0);
        addInteger(0);
        addInteger(0);
        addByte((byte) 0);
        addShort((short)0x7D1);
        addByte((byte) 2);
        addShort((short)30006);
        addShort((short)30006);
        addByte((byte) 2);
        addShort((short)30005);
        addShort((short)30005);
        addShort((short)0);
        addShort((short)0x33);
        addByte((byte) 0);
        addInteger(c.getIp()); //global ip
        addShort((short)c.getUdpPort()); //global port
        addInteger(c.getUdpIp()); //local ip
        addShort((short)c.getUdpPort()); //local port
        addByte((byte) 3);
        addShort((short)0);
        addByte((byte) 0);
        addShort((short)1);
        addByte((byte) 0);
        addShort((short)2);
        addByte((byte) 0);
        addInteger(0);
        
        size = (short) buffer.position();
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
    
    public short getRoomNumber()
    {
        return roomNumber;
    }
}