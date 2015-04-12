/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.bitcrystal.decentralizedexchange.security;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONException;
import org.json.JSONObject;
import com.google.gson.JsonObject;

/**
 *
 * @author ABC
 */
public class Json {

    public static JSONObject toJSONObject(String string) {
        if (string == null || string.isEmpty()) {
            return null;
        }
        try {
            return new JSONObject(string);
        } catch (JSONException ex) {
            return null;
        }
    }

    public static String toJSONObjectString(JSONObject jsonObject) {
        if (jsonObject == null) {
            return "";
        }
        return jsonObject.toString();
    }
}
