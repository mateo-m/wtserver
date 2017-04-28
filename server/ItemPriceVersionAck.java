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
public class ItemPriceVersionAck extends ServerMsg {
    private final short msgId = ServerMsg.CS_IN_ITEMPRICE_VERSION_ACK;
    
    public ItemPriceVersionAck()
    {
        size = 24;
        buffer = ByteBuffer.allocate(size);
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
        byte b[] = {0x00, (byte)0x81, 0x02, 0x00, 0x00, (byte)0xDF, (byte)0xB4, (byte)0xD6, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00};
        for(int i = 0; i < b.length; i++)
        {
            addByte(b[i]);
        }
        return buffer.array();
    }
}