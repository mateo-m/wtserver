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
public class CharItemListAck extends ServerMsg {
    private final short msgId = ServerMsg.CS_IN_CHARITEMLIST_ACK;
    
    public CharItemListAck()
    {
        size = 8;
        buffer = ByteBuffer.allocate(1024);
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
        /*addByte((byte) 0);
        addByte((byte) 1);
        addByte((byte) 0);
        addByte((byte) 0);
        addByte((byte) 0);
        int nItems = 2;
        addByte((byte) nItems);
        addByte((byte) 0);
        addByte((byte) 0);
        addByte((byte) 0);
        size += 9;
        int Unique[] = {6, 7};
        int Items[] = {30006, 30005};
        for (int i = 0; i < nItems; i++) {
            size += 25;
            addInteger(Unique[i] + Items[i]*7);
            addShort((short) Items[i]);
            addByte((byte) 0xE);
            addAsciiString("2015071222320" + Unique[i]);
            addShort((short) 1);
            addShort((short) 2);
        }*/
        byte b[] = {0x00, 0x01, 0x00, 0x00, 0x00, 0x02, 0x00, 0x00, 0x00, 0x2B, 0x61, (byte)0xC1, 0x0A, 0x36, 0x75, 0x0E, 0x32, 0x30, 0x31, 0x34, 0x30, 0x37, 0x31, 0x32, 0x32, 0x32, 0x33, 0x32, 0x31, 0x31, (byte)0xFF, (byte)0xFF, 0x00, 0x00, 0x2C, 0x61, (byte)0xC1, 0x0A, 0x35, 0x75, 0x0E, 0x32, 0x30, 0x31, 0x34, 0x30, 0x37, 0x31, 0x32, 0x32, 0x32, 0x33, 0x32, 0x31, 0x31, (byte)0xFF, (byte)0xFF, 0x00, 0x00};
        
        for(int i = 0; i < b.length; i++)
        {
            addByte(b[i]);
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
        for(int i = 0; i < 7; i++)
        {
            checksum += buffer.get(i);
        }
        addByte(checksum);
        return buffer.array();
    }
}
