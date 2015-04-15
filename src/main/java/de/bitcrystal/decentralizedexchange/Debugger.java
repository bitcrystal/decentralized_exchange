/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.bitcrystal.decentralizedexchange;

import java.io.PrintStream;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import javax.swing.JOptionPane;

/**
 *
 * @author ABC
 */
public final class Debugger {

    private boolean debug;
    private boolean addlines;
    private PrintStream out;
    private List<String> list;
    private boolean useJOption;
    private String startsWithCondition;
    
    public Debugger()
    {
        this.debug=true;
        this.addlines=true;
        this.out=System.out;
        this.list=new CopyOnWriteArrayList<String>();
        this.useJOption=false;
        this.startsWithCondition="";
    }
    
    public boolean getDebug()
    {
        return debug;
    }
    
    public boolean getAddLines()
    {
        return addlines;
    }
    
    public PrintStream getOut()
    {
        return out;
    }
    
    public List<String> getList()
    {
        return list;
    }
    
    public boolean getUseJOption()
    {
        return useJOption;
    }
    
    public String getStartsWithCondition()
    {
        return startsWithCondition;
    }
    
    public boolean hasStartsWithCondition()
    {
        return startsWithCondition!=null&&!startsWithCondition.isEmpty();
    }
    
    public void setDebug(boolean set)
    {
        this.debug=set;
    }

    public void setAddLines(boolean set)
    {
        this.addlines=set;
    }
    
    public void setOut(PrintStream out)
    {
        this.out=out;
    }
    
    public void setList(List<String> list)
    {
        this.list=list;
    }
    
    public void setUseJOption(boolean set)
    {
        this.useJOption=set;
    }
    
    public void setStartsWithCondition(String string)
    {
        this.startsWithCondition=string;
    }
    
    public void resetStartsWithCondition()
    {
        this.startsWithCondition="";
    }
    
    public void println(String string) {
        if(string==null||string.isEmpty())
            return;
        if(hasStartsWithCondition())
        {
            if(!string.startsWith(startsWithCondition))
            {
                return;
            }
        }
        if (debug && !useJOption) {
            out.println(string);
        } else if (debug && useJOption) {
            JOptionPane.showMessageDialog(null, string);
        }
        if (addlines) {
            list.add(string);
        }
        return;
    }

    public void println(int length) {
        this.println(""+length);
    }

    public void println(double length) {
        this.println(""+length);
    }

    public boolean containsDebugElement(String string) {
        if (string == null || string.isEmpty()) {
            return false;
        }
        return list.contains(string);
    }

    public void clearDebugElements() {
        list.clear();
    }

    public boolean lastDebugElementEquals(String string) {
        if (list.isEmpty()) {
            return false;
        }
        if (string == null || string.isEmpty()) {
            return false;
        }
        String get = list.get(list.size() - 1);
        return get.equals(string);
    }

    public boolean lastDebugElementEqualsIgnoreCase(String string) {
        if (list.isEmpty()) {
            return false;
        }
        if (string == null || string.isEmpty()) {
            return false;
        }
        String get = list.get(list.size() - 1);
        return get.equalsIgnoreCase(string);
    }

    public boolean lastDebugElementStartsWith(String string) {
        if (list.isEmpty()) {
            return false;
        }
        if (string == null || string.isEmpty()) {
            return false;
        }
        String get = list.get(list.size() - 1);
        return get.startsWith(string);
    }

    public boolean lastDebugElementEndsWith(String string) {
        if (list.isEmpty()) {
            return false;
        }
        if (string == null || string.isEmpty()) {
            return false;
        }
        String get = list.get(list.size() - 1);
        return get.endsWith(string);
    }

    public boolean lastDebugElementContains(String string) {
        if (list.isEmpty()) {
            return false;
        }
        if (string == null || string.isEmpty()) {
            return false;
        }
        String get = list.get(list.size() - 1);
        return get.contains(string);
    }

    public boolean debugElementsEquals(List<String> list2) {
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

    public String getLastDebugElement() {
        if (list == null || list.isEmpty()) {
            return "";
        }
        return list.get(list.size() - 1);
    }

    public boolean hasLastDebugElement() {
        return !getLastDebugElement().isEmpty();
    }
}
