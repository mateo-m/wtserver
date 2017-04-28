/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package wtserver.database;

import java.util.ArrayList;
import java.util.StringTokenizer;

/**
 *
 * @author MSI
 */
public class Channels extends DatabaseConnection {
    
    private String weburl;
    
    public Channels()
    {
        weburl = webserver + "channels.php";
    }
    
    public class Channel {
        private String channelName;
        private short channelPort;
        public Channel(String channelName, short channelPort)
        {
            this.channelName = channelName;
            this.channelPort = channelPort;
        }
        
        public String getChannelName()
        {
            return channelName;
        }
        
        public short getChannelPort()
        {
            return channelPort;
        }
    }
    
    public ArrayList<Channel> getChannels()
    {
        ArrayList<Channel> channels = new ArrayList<Channel>();
        
        String web = weburl + "?req=get";
        
        String response = getResponse(web);
        
        StringTokenizer st = new StringTokenizer(response, ";");
        
        while(st.hasMoreTokens())
        {
            String channelName = st.nextToken();
            String channelPort = st.nextToken();
            
            Channel channel = new Channel(channelName, (short) Integer.parseInt(channelPort));
            
            channels.add(channel);
        }
        
        return channels;
    }
}
