/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.bitcrystal.decentralizedexchange;

import java.io.IOException;
import java.io.OutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author ABC
 */
public class BitCrystalOutputStreamEx extends OutputStream {

    private OutputStream outputStream;
    private byte[] buffer;
    private int bufferLength;

    public BitCrystalOutputStreamEx(OutputStream outputStream) {
        this.outputStream = outputStream;
    }

    public void writeBuffer(String data, int bufferL) {
        if (bufferL <= 0) {
            bufferL = 100;
        }
        if (bufferL % 2 != 0) {
            bufferL = 100;
        }
        try {

            OutputStream outputStream = this;
            byte[] b = data.getBytes("UTF-8");
            int length = b.length;
            int maxi = length / bufferL;
            int off = 0;
            for (int i = 0; i < maxi; i++) {
                outputStream.write(b, off, bufferL);
                outputStream.flush();
                off += bufferL;
            }
            maxi = length % bufferL;
            if (maxi != 0) {
                outputStream.write(b, off, maxi);
                outputStream.flush();
            }
        } catch (Exception ex) {
            Logger.getLogger(TCPClientSecurity.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void write(byte[] b) {
        this.write(b, 0, b.length);
    }

    @Override
    public void write(byte[] b, int off, int len) {
        buffer = new byte[len + 165];
        for (int i = 0; i < len; i++) {
            buffer[i] = b[i + off];
        }
        int sl = len + 165;
        for (int i = len; i < sl; i++) {
            buffer[i] = (byte) -1;
        }
        bufferLength = len + 165;
    }

    @Override
    public void flush() {
        int i = 0;
        int trys = 15;
        int trys_ = 0;
        boolean write = false;
        while (i < bufferLength) {
            write = this.write(buffer[i]);
            if (!write) {
                trys_++;
                if (trys_ >= trys) {
                    break;
                }
                continue;
            }
            trys_ = 0;
            i++;
        }
    }

    private boolean write(byte b) {
        try {
            this.write(b & 0xFF);
            return true;
        } catch (IOException ex) {
            return false;
        }
    }

    @Override
    public void write(int b) throws IOException {
        this.outputStream.write(b);
    }

    @Override
    public void close() throws IOException {
        this.outputStream.close();
    }
}
