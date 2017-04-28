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
public class UdpReadyAck extends ServerMsg {
    private final short msgId = ServerMsg.CS_FD_UDPREADY_ACK;
    byte slot, ready;
    public static byte SHOULD_BE_EVEN = (byte) 0xC0;
    
    public UdpReadyAck(byte slot, byte ready)
    {
        this.slot = slot;
        this.ready = ready;
        size = 24;
        buffer = ByteBuffer.allocate(size);
    }
    
    public UdpReadyAck(byte slot, byte [] recv_buffer)
    {
        this.slot = slot;
        this.ready = recv_buffer[0];
        size = 24;
        buffer = ByteBuffer.allocate(size);
    }
    
    public byte [] getData(short seqNum)
    {
        System.out.println("Send Ready " + slot + " " + ready);
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
        addByte((byte)0);
        addByte((byte)slot);
        addByte((byte)ready);
        
        for(int i = buffer.position(); i < buffer.capacity(); i++)
        {
            addByte((byte)0);
        }
        return buffer.array();
    }
    
    public byte userReady()
    {
        return ready;
    }
}