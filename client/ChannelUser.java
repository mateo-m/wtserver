/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wtserver.client;

/**
 *
 * @author MSI
 */
public class ChannelUser {
    String id, name;
    byte level;
    public ChannelUser(String id, String name, byte level)
    {
        this.id = id;
        this.name = name;
        this.level = level;
    }
    public String getID()
    {
        return id;
    }
    public String getCallsign()
    {
        return name;
    }
    public byte getLevel()
    {
        return level;
    }
}
