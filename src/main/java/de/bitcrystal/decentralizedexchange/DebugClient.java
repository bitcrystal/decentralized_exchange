/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.bitcrystal.decentralizedexchange;

import java.io.PrintStream;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 *
 * @author ABC
 */
public class DebugClient {

    private static boolean debug = true;
    private static boolean addlines = false;
    private static PrintStream out = System.out;
    private static List<String> list = new CopyOnWriteArrayList<String>();

    public static void println(String string) {
        if (debug) {
            out.println(string);
        }
        if (addlines) {
            list.add(string);
        }
        return;
    }

    public static void println(int length) {
        if (debug) {
            out.println(length);
        }
        if (addlines) {
            list.add("" + length);
        }
        return;
    }

    public static void println(double length) {
        if (debug) {
            out.println(length);
        }
        if (addlines) {
            list.add("" + length);
        }
        return;
    }

    public static boolean containsDebugElement(String string) {
        if (string == null || string.isEmpty()) {
            return false;
        }
        return list.contains(string);
    }

    public static void clearDebugElements() {
        list.clear();
    }

    public static boolean lastDebugElementEquals(String string) {
        if (list.isEmpty()) {
            return false;
        }
        if (string == null || string.isEmpty()) {
            return false;
        }
        String get = list.get(list.size() - 1);
        return get.equals(string);
    }

    public static boolean lastDebugElementEqualsIgnoreCase(String string) {
        if (list.isEmpty()) {
            return false;
        }
        if (string == null || string.isEmpty()) {
            return false;
        }
        String get = list.get(list.size() - 1);
        return get.equalsIgnoreCase(string);
    }

    public static boolean lastDebugElementStartsWith(String string) {
        if (list.isEmpty()) {
            return false;
        }
        if (string == null || string.isEmpty()) {
            return false;
        }
        String get = list.get(list.size() - 1);
        return get.startsWith(string);
    }

    public static boolean lastDebugElementEndsWith(String string) {
        if (list.isEmpty()) {
            return false;
        }
        if (string == null || string.isEmpty()) {
            return false;
        }
        String get = list.get(list.size() - 1);
        return get.endsWith(string);
    }

    public static boolean lastDebugElementContains(String string) {
        if (list.isEmpty()) {
            return false;
        }
        if (string == null || string.isEmpty()) {
            return false;
        }
        String get = list.get(list.size() - 1);
        return get.contains(string);
    }

    public static boolean debugElementsEquals(List<String> list2) {
        if (list2 == null || list2.isEmpty() || list2.size() != list.size()) {
            return false;
        }
        int size = list2.size();
        boolean set = false;
        for (int i = 0; i < size; i++) {
            set = list2.get(i).equals(list.get(i));
            if (!set) {
                return false;
            }
        }
        return true;
    }

    public static String getLastDebugElement() {
        if (list == null || list.isEmpty()) {
            return "";
        }
        return list.get(list.size() - 1);
    }
    
    public static boolean hasLastDebugElement()
    {
        return !getLastDebugElement().isEmpty();
    }
}
