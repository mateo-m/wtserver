/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package wtserver.database;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

/**
 *
 * @author MSI
 */
public class DatabaseConnection {
    
    protected final String webserver = "http://localhost/wolfteam/";
    
    public DatabaseConnection()
    {
        
    }
    
    public String getResponse(String weburl)
    {
        String result = "";
        System.out.println("Webcall " + weburl);
        try {
            URL url = new URL(weburl);
            URLConnection conn = url.openConnection();
            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            
            result = br.readLine();
            
            br.close();
        } catch (Exception ex) {
            System.out.println(ex);
        }
        
        return result;
    }
}
