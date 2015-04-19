/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.bitcrystal.decentralizedexchange;

import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.jxta.impl.util.BASE64InputStream;

/**
 *
 * @author ABC
 */
public class BitCrystalInputStreamEx extends InputStream {

    private InputStream inputStream;
    private int available;

    public BitCrystalInputStreamEx(InputStream inputStream) {
        this.available = 0;
        this.inputStream = inputStream;
    }

    @Override
    public int available() {
        return available;
    }
    
    public String readBuffer(String data, int buffer)
    {
        if (buffer <= 0) {
            buffer = 50;
        }
        if (buffer / 2 <= 0 || buffer % 2 != 0) {
            buffer = 50;
        } else {
            buffer = buffer / 2;
        }
        try {
            InputStream in = this;
            int len = buffer;
            byte[] b = new byte[buffer];
            int off = 0;
            int read = 0;
            String string = "";
            do {
                read = in.read(b, off, len);
                if (read == buffer) {
                    string += new String(b, "UTF-8");
                } else if (read > 0) {
                    byte[] bytes = new byte[read];
                    for (int i = 0; i < read; i++) {
                        bytes[i] = b[i];
                    }
                    string += new String(bytes, "UTF-8");
                    if(in.available()>0)
                        read=buffer;
                }
            } while (read == buffer);
            return string;
        } catch (IOException ex) {
            Logger.getLogger(TCPClientSecurity.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    @Override
    public int read(byte[] b, int off, int len) {
        byte temp = 0;
        int rl = 0;
        int readx = 0;
        for (int i = 0; i < len; i++) {
            try {
                temp = (byte) this.read();
                if (temp == -1) {
                    readx++;
                    if (readx == 165) {
                        this.available = 0;
                        return rl;
                    }
                    continue;
                }
                readx=0;
                available = 1;
                b[i + off] = temp;
                rl++;
            } catch (IOException ex) {
                Logger.getLogger(BitCrystalInputStreamEx.class.getName()).log(Level.SEVERE, null, ex);
                return rl;
            }
        }
        return rl;
    }

    @Override
    public int read(byte[] b) {
        return this.read(b, 0, b.length);
    }

    @Override
    public int read() throws IOException {

        return this.inputStream.read();
    }

    @Override
    public void close() throws IOException {
        this.inputStream.close();
    }
}
