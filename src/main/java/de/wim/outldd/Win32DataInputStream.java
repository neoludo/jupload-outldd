/*
    Copyright (c) 2011 Wolfgang Imig
    
    This file is part of the library "Java Drag&Drop for Microsoft Outlook".

    This file can be used by the terms of   
      
      - Wolfgang Imig "Java Drag&Drop for Microsoft Outlook" END USER LICENSE AGREEMENT TERMS AND CONDITIONS

*/
package de.wim.outldd;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * This <code>InputStream</code> implements functions like <code>DataInputStream</code>
 * but assumes the Little Endian byte order. 
 */
public class Win32DataInputStream {
  
  private DataInputStream is;

  /**
   * Constructor
   * @param is InputStream
   */
  public Win32DataInputStream(InputStream is) {
    this.is = new DataInputStream(is);
  }
  
  public void skip(long n) throws IOException {
    this.is.skip(n);
  }
  
  public int readBytes(byte[] buf) throws IOException {
    return this.is.read(buf);
  }
  
  public int readInt() throws IOException {
    int v = is.readInt();
    v = swapByteOrder(v);
    return v;
  }

  public long readLong() throws IOException {
    long v = is.readLong();
    v = swapByteOrder(v);
    return v;
  }
  
  public String readUnicodeString(int max) throws IOException {
    StringBuilder sbuf = new StringBuilder(max);
    for (int i = 0; i < max; i++) {
      short v = is.readShort();
      if (v != 0) {
        v = swapByteOrder(v);
        sbuf.append( (char)v );
      }
    }
    return sbuf.toString();
  }
  
  private short swapByteOrder(short v) {
    short v1 = 0;
    v1 |= v & 0xFF;
    v1 <<= 8; v >>= 8;
    v1 |= v & 0xFF;
    return v1;
  }
  private int swapByteOrder(int v) {
    int v1 = 0;
    v1 |= v & 0xFF;
    v1 <<= 8; v >>= 8;
    v1 |= v & 0xFF;
    v1 <<= 8; v >>= 8;
    v1 |= v & 0xFF;
    v1 <<= 8; v >>= 8;
    v1 |= v & 0xFF;
    return v1;
  }
  private long swapByteOrder(long v) {
    long v1 = 0;
    v1 |= v & 0xFF;
    v1 <<= 8; v >>= 8;
    v1 |= v & 0xFF;
    v1 <<= 8; v >>= 8;
    v1 |= v & 0xFF;
    v1 <<= 8; v >>= 8;
    v1 |= v & 0xFF;
    v1 <<= 8; v >>= 8;
    v1 |= v & 0xFF;
    v1 <<= 8; v >>= 8;
    v1 |= v & 0xFF;
    v1 <<= 8; v >>= 8;
    v1 |= v & 0xFF;
    v1 <<= 8; v >>= 8;
    v1 |= v & 0xFF;
    return v1;
  }
  
  public void close() throws IOException {
    if (is != null) is.close();
  }
}
