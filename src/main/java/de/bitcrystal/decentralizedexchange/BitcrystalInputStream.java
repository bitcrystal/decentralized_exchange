package de.bitcrystal.decentralizedexchange;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author ABC
 */
public class BitcrystalInputStream extends InputStream {

    private InputStream inputStream;
    private int buffer;

    public BitcrystalInputStream(InputStream inputStream, int buffer) {
        this.inputStream = inputStream;
        this.buffer = buffer;
    }
    public BitcrystalInputStream(InputStream inputStream) {
        this(inputStream, 40000);
    }

    public String readString() {
        String readBuffer = readBuffer(buffer);
        return readBuffer;
    }

    public String readBuffer(int bufferL) {
        if (bufferL <= 0) {
            bufferL = 50;
        }
        if (bufferL / 2 <= 0 || bufferL % 2 != 0) {
            bufferL = 50;
        } else {
            bufferL = bufferL / 2;
        }

        try {
            int len = bufferL;
            byte[] b = new byte[bufferL];
            int off = 0;
            int read = 0;
            String string = "";
            do {
                read = this.read(b, off, len);
                if (read == bufferL) {
                    string += new String(b, "UTF-8");
                } else if (read > 0) {
                    byte[] bytes = new byte[read];
                    for (int i = 0; i < read; i++) {
                        bytes[i] = b[i];
                    }
                    string += new String(bytes, "UTF-8");
                }
            } while (read == bufferL);
            return string;
        } catch (IOException ex) {
            Logger.getLogger(BitcrystalInputStream.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    @Override
    public int read(byte[] b, int off, int len) {
        if (len <= 0) {
            len = 1;
        }
        if (off < 0 || off >= len) {
            off = 0;
        }
        if (b == null || b.length < off + len) {
            b = new byte[len + off];
        }
        int rl = 0;
        int trys_ = 0;
        while (rl < len) {
            byte read = readByte();
            if (read == -1) {
                trys_++;
                if (trys_ >= 3) {
                    break;
                }
                continue;
            }
            trys_ = 0;
            b[rl + off] = read;
            rl++;
        }
        return rl;
    }

    public byte readByte() {
        try {
            int read = this.read();
            if (read == -1) {
                return -1;
            }
            return (byte) read;
        } catch (IOException ex) {
            return -1;
        }
    }

    @Override
    public int read(byte[] b) {
        if (b == null || b.length == 0) {
            b = new byte[1];
        }
        return read(b, 0, b.length);
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
