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
public class EndRoundAck extends ServerMsg {
    private final short msgId = ServerMsg.CS_FD_ENDROUND_ACK;
    private byte roundNumber, winTeam, redWins, blueWins;
    public EndRoundAck(byte roundNumber, byte winTeam, byte redWins, byte blueWins)
    {
        this.roundNumber = roundNumber;
        this.winTeam = winTeam;
        this.redWins = redWins;
        this.blueWins = blueWins;
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
        
        addByte((byte) 0);
        addByte((byte) roundNumber);//round number
        addByte((byte) winTeam);//winner team
        addByte((byte) redWins); //Red Wins
        addByte((byte) blueWins); //Blue wins
        addByte((byte) 0x3C);
        
        for(int i = buffer.position(); i < buffer.capacity(); i++)
        {
            addByte((byte)0);
        }
        return buffer.array();
    }
    

        
}