/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.bitcrystal.decentralizedexchange;

import java.io.PrintStream;
import java.util.List;

/**
 *
 * @author ABC
 */
public class DebugServer {

    private static boolean debug = true;
    private static boolean addlines = true;
    private static PrintStream out = System.out;
    private static boolean useJOption = false;
    private static String startsWithCondition = "@@@";
    private static boolean isInit = false;
    private static Debugger debugger = null;
    private static long count = 0L;
    
    public static void println(String string) {
        init();
        debugger.println(string);
    }

    public static void println(int length) {
        init();
        debugger.println(length);
    }

    public static void println(double length) {
        init();
        debugger.println(length);
    }

    public static void printList() {
        init();
        debugger.printList();
    }

    public static void printListToFile() {
        init();
        debugger.printListToFile("server" + "."+count + ".log");
        count++;
    }

    public static void setStartsWithCondition(String string) {
        init();
        debugger.setStartsWithCondition(string);
    }

    public static void resetStartsWithCondition() {
        init();
        debugger.resetStartsWithCondition();
    }

    public static String getStartsWithCondition() {
        init();
        return debugger.getStartsWithCondition();
    }

    public static boolean containsDebugElement(String string) {
        init();
        return debugger.containsDebugElement(string);
    }

    public static void clearDebugElements() {
        init();
        debugger.clearDebugElements();
    }

    public static boolean lastDebugElementEquals(String string) {
        init();
        return debugger.lastDebugElementEquals(string);
    }

    public static boolean lastDebugElementEqualsIgnoreCase(String string) {
        init();
        return debugger.lastDebugElementEqualsIgnoreCase(string);
    }

    public static boolean lastDebugElementStartsWith(String string) {
        init();
        return debugger.lastDebugElementStartsWith(string);
    }

    public static boolean lastDebugElementEndsWith(String string) {
        init();
        return debugger.lastDebugElementEndsWith(string);
    }

    public static boolean lastDebugElementContains(String string) {
        init();
        return debugger.lastDebugElementContains(string);
    }

    public static boolean debugElementsEquals(List<String> list2) {
        init();
        return debugger.debugElementsEquals(list2);
    }

    public static String getLastDebugElement() {
        init();
        return debugger.getLastDebugElement();
    }

    public static boolean hasLastDebugElement() {
        init();
        return debugger.hasLastDebugElement();
    }

    private static void init() {
        if (isInit) {
            return;
        }
        debugger = new Debugger();
        debugger.setAddLines(addlines);
        debugger.setDebug(debug);
        debugger.setOut(out);
        debugger.setUseJOption(useJOption);
        debugger.setStartsWithCondition(startsWithCondition);
        isInit = true;
    }
}
