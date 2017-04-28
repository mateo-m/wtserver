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
public class ChannelChatAck extends ServerMsg {
    private final short msgId = ServerMsg.CS_CH_CHAT_ACK;
    String callsign, text;
    
    public ChannelChatAck(byte recv_buffer [], String callsign)
    {
        size = 512;
        buffer = ByteBuffer.allocate(size);
        this.callsign = callsign;
        this.text = "";
        short b = recv_buffer[6];
        for(int i = 6; i < (b+6); i+=2)
        {
            char c = (char) recv_buffer[1 + i];
            text += c;
        }
        System.out.println("Chat " + text);
    }
    
    public byte [] getData(short seqNum)
    {
        buffer.position(0);
        byte random = (byte) new Random().nextInt();
        addByte(random);
        addShort(msgId);
        addShort(seqNum);
        addShort((short) 0);
        byte checksum = 0;
        addByte(checksum);
        
        addByte((byte) 0);
        
        addInteger(0);
        addShort((short) 0);
        
        addByte((byte) (callsign.length() * 2));
        addUnicodeString(callsign);
        addByte((byte) 0);
        addByte((byte) (text.length() * 2));
        addUnicodeString(text);
        
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
    
    public String getText()
    {
        return text;
    }
}
