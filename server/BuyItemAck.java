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
import wtserver.database.Users;

/**
 *
 * @author MSI
 */
public class BuyItemAck extends ServerMsg {
    private final short msgId = ServerMsg.CS_IN_BUYITEM_ACK;
    short itemId, price;
    byte days, quantity;
    
    public BuyItemAck(byte recv_buffer [], int user_id)
    {
        size = 8;
        buffer = ByteBuffer.allocate(128);
        itemId = (short) ((recv_buffer[0] & 0xff) + (recv_buffer[1]*0x100));
        days = (byte) (recv_buffer[2] & 0xff);
        quantity = (byte) (recv_buffer[5] & 0xff);
        price = (short) ((recv_buffer[6] & 0xff) + (recv_buffer[7]*0x100));
        Users users = new Users();
        users.addItems(user_id, itemId);
        System.out.println("Buying Item " + itemId + " Days " + days + " Quantity " + quantity + " Price " + price);
    }
    
    public byte [] getData(short seqNum, byte status)
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
        addByte((byte) 0xFA);
        addByte((byte) 0);
        //TODO
        addByte((byte) 0xAA);
        addByte((byte) 0);
        addByte((byte) 0);
        addByte((byte) 0);
        
        addByte((byte) 0x11);
        addByte((byte) 0xB7);
        addByte((byte) 0);
        addByte((byte) 0);
        addByte((byte) 0);
        addByte((byte) 0);
        addByte((byte) 0);
        addByte((byte) 0);
        
        addByte((byte) 0xEE);
        addByte((byte) 0x95);
        addByte((byte) 0x24);
        addByte((byte) 0x0C);        
        addShort((short)0x2E23);
        String timestamp = "20150727170544";
        addByte((byte) 0x0E);
        addAsciiString(timestamp);
        addByte((byte) 0); //days
        addByte((byte) 0);
        addByte((byte) 0);
        addByte((byte) 2);  
        addByte((byte) 0);
        addByte((byte) 0);        
        addByte((byte) 0);
        addByte((byte) 0);
        addByte((byte) 0);
        addByte((byte) 0);
        
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
    
    public int getPrice()
    {
        return price;
    }
}