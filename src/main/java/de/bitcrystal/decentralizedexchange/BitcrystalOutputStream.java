/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.bitcrystal.decentralizedexchange;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author ABC
 */
public class BitcrystalOutputStream extends OutputStream {

    private OutputStream outputSteam;
    private byte[] buffer;
    private int bufferLength;
    private int buffersL;

    public BitcrystalOutputStream(OutputStream outputStream, int buffersL)
    {
        this.outputSteam=outputStream;
        this.buffersL=buffersL;
    }
            
    public BitcrystalOutputStream(OutputStream outputStream) {
        this.outputSteam = outputStream;
        this.buffersL=40000;
    }

    @Override
    public void write(byte[] b, int off, int len)
    {
        this.writeToBuffer(b, off, len);
    }
    
    @Override
    public void write(byte[] b)
    {
        this.writeToBuffer(b, 0, b.length);
    }
    
    @Override
    public void flush()
    {
       this.flushToBuffer();
    }
    
    
    public void writeBuffer(String data, int bufferL) {
        if (bufferL <= 0) {
            bufferL = 100;
        }
        if (bufferL % 2 != 0) {
            bufferL = 100;
        }
        try {
            byte[] b = data.getBytes("UTF-8");
            int length = b.length;
            int maxi = length / bufferL;
            int off = 0;
            for (int i = 0; i < maxi; i++) {
                this.writeBuffer(b, off, bufferL);
                this.flushBuffer(b, off, bufferL);
                off += bufferL;
            }
            maxi = length % bufferL;
            if (maxi != 0) {
                this.writeBuffer(b, off, maxi);
                this.flushBuffer(b, off, maxi);
            }
        } catch (Exception ex) {
            Logger.getLogger(BitcrystalOutputStream.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void writeString(String data) {
        this.writeBuffer(data, buffersL);
    }

    public void writeToBuffer(byte[] b) {
        this.writeToBuffer(b, 0, b.length);
    }

    public void writeToBuffer(byte[] b, int off, int len) {
        bufferLength = len;
        buffer = new byte[bufferLength];
        for (int i = 0; i < bufferLength; i++) {
            buffer[i] = b[i + off];
        }
    }
    
    public void writeBuffer(byte[] b, int off, int len) {
        byte[] buff = new byte[len];
        for (int i = 0; i < len; i++) {
            buff[i] = b[i + off];
        }
        flushBuffer(buff, 0, buff.length);
    }
    
    public void flushBuffer(byte[] b, int off, int len)
    {
        if (b == null) {
            return;
        }
        if (len <= 0) {
            return;
        }
        if(off<0)
        {
            return;
        }
        int i = 0;
        int trys_ = 0;
        while (i < len) {

            boolean trys = this.write(b[i+off]);
            if (trys) {
                i++;
                trys_ = 0;
                continue;
            } else {
                trys_++;
            }
            if (trys_ >= 3) {
                return;
            }
        }
    }

    public void flushToBuffer() {
        flushBuffer(buffer, 0, bufferLength);
        buffer = null;
        bufferLength = 0;
    }

    public boolean write(byte b) {
        try {
            this.write((int) (b & 0xFF));
            return true;
        } catch (IOException ex) {
            return false;
        }
    }

    @Override
    public void write(int b) throws IOException {
        this.outputSteam.write(b);
    }
    
    @Override
    public void close() throws IOException
    {
        this.outputSteam.close();
    }
}
