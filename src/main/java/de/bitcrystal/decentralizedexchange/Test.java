/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.bitcrystal.decentralizedexchange;

import de.bitcrystal.decentralizedexchange.security.BitCrystalJSON;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.eclipse.jetty.util.ajax.JSON;
import org.json.JSONObject;

/**
 *
 * @author ABC
 */
public class Test {
    public static void main(String[] args) {
        try {
            JSONObject json = new JSONObject();
            json.put("test", "alter");
            boolean saveJSONObject = BitCrystalJSON.saveJSONObject(json, "key", "", "my.json");
            JSONObject loadJSONObject = BitCrystalJSON.loadJSONObject("key", "", "my.json");
            System.out.println(loadJSONObject);
        } catch (Exception ex) {
            Logger.getLogger(Test.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
