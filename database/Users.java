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
public class Users extends DatabaseConnection {
    
    private String weburl;
    
    public Users()
    {
        weburl = webserver + "users.php";
    }
    
    public int getLogin(String user, String password)
    {
        String web = weburl + "?req=login&name=" + user + "&password=" + password;
        
        String response = getResponse(web);
        
        return Integer.parseInt(response);
    }
    
    public class UserInfo {

        public String callsign;
        public int level, gp, gold, wc, cash, kill, death;

        public UserInfo(String callsign, int level, int gp, int gold, int wc, int cash, int kill, int death) {
            this.callsign = callsign;
            this.level = level;
            this.gp = gp;
            this.gold = gold;
            this.wc = wc;
            this.cash = cash;
            this.kill = kill;
            this.death = death;
        }
    }
    
    public UserInfo getUserInfo(int user_id)
    {
        String web = weburl + "?req=get&id=" + user_id;
        
        String response = getResponse(web);
        StringTokenizer st = new StringTokenizer(response, ";");
        
        while(st.hasMoreTokens())
        {
            String callsign = st.nextToken();
            int level = Integer.parseInt(st.nextToken());
            int gp = Integer.parseInt(st.nextToken());
            int gold = Integer.parseInt(st.nextToken());
            int wc = Integer.parseInt(st.nextToken());
            int cash = Integer.parseInt(st.nextToken());
            int kill = Integer.parseInt(st.nextToken());
            int death = Integer.parseInt(st.nextToken());
            
            UserInfo userInfo = new UserInfo(callsign, level, gp, gold, wc, cash, kill, death);
            
            return userInfo;
        }
        
        return null;
    }  
    
        public class Item {
        public int id;
        public int type;
        public int upgrade;
        public String expiry;

        public Item(int id, int type, int upgrade, String expiry) {
            this.id = id;
            this.type = type;
            this.upgrade = upgrade;
            this.expiry = expiry;
        }
    }
    public ArrayList<Item> getItems(int user_id)
    {
        String web = weburl + "?req=getitem&id=" + user_id;
        
        String response = getResponse(web);
        ArrayList<Item> items = new ArrayList<Item>();
        if(response == null)
            return items;
        StringTokenizer st = new StringTokenizer(response, ";");
        while(st.hasMoreTokens())
        {
            int id = Integer.parseInt(st.nextToken());
            int type = Integer.parseInt(st.nextToken());
            int upgrade = Integer.parseInt(st.nextToken());
            String expiry = st.nextToken();

            items.add(new Item(id, type, upgrade, expiry));
        }
        
        return items;
    } 
    
    public void addItems(int user_id, int item_id)
    {
        String web = weburl + "?req=additem&id=" + user_id + "&type=" + item_id;
        
        String response = getResponse(web);
    }  
    
    public void setEquip(String field, int value, int user_id)
    {
        String web = weburl + "?req=equip&id=" + user_id + "&field=" + field + "&value=" + value;
        
        String response = getResponse(web);
    }
    
    public int [] getEquipList(int user_id)
    {
        int equips [] = new int [30];
        
        String web = weburl + "?req=equiplist&id=" + user_id;
        
        String response = getResponse(web);

        if(response == null)
            return null;
        StringTokenizer st = new StringTokenizer(response, ";");
        int i = 0;
        while(st.hasMoreTokens() && i < 30)
        {
            equips[i] = Integer.parseInt(st.nextToken());
            i++;
        }
        
        return equips;
    }
    
    public void registerKill(int killer_id, int dead_id) {
        String web = weburl + "?req=kill&id=" + dead_id + "&value=" + killer_id;

        String response = getResponse(web);
    }
    
    public void addPoints(int user_id, int gold, int gp)
    {
        String web = weburl + "?req=addpoints&id=" + user_id + "&gold=" + gold + "&gp=" + gp;

        String response = getResponse(web);
    }
}
