/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package wtserver.server.channel;

import wtserver.client.ServerMsg;
import java.nio.ByteBuffer;
import java.util.Random;

/**
 *
 * @author MSI
 */
public class UdpSuccessAck extends ServerMsg {
    private final short msgId = ServerMsg.CS_CK_UDPSUCCESS_ACK;
    
    public UdpSuccessAck()
    {
        size = 24;
        buffer = ByteBuffer.allocate(size);
    }
    
    public byte [] getData(short seqNum, int Ip, short Port, int udpIp, short udpPort)
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
        addByte((byte)0);
        addInteger(Ip);
        addShort(Port);
        addInteger(udpIp);
        addShort(udpPort);
        
        return buffer.array();
    }
}