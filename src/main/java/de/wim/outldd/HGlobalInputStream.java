/*
    Copyright (c) 2011 Wolfgang Imig
    
    This file is part of the library "Java Drag&Drop for Microsoft Outlook".

    This file can be used by the terms of   
      
      - Wolfgang Imig "Java Drag&Drop for Microsoft Outlook" END USER LICENSE AGREEMENT TERMS AND CONDITIONS

*/
package de.wim.outldd;

import java.io.IOException;
import java.io.InputStream;

/**
 * This class obtains an InputStream 
 * that reads from a native memory object.
 */
public class HGlobalInputStream extends InputStream {
  
  // Pointer to a reference counted object that contains a HGLOBAL variable. 
  private long h;
  
  private long pos;
  private long markPos;
  
  // This cache optimizes subsequent calls to the <code>read()</code> function.  
  private byte[] cache = new byte[100];
  private long cachePos;
  private int cacheLen;
  
  /**
   * Constructor with initialization.
   * @param h Pointer to a native reference counted object that holds a HGLOBAL variable. 
   */
  protected HGlobalInputStream(long h) {
    this.h = h;
  }
  
  private int cacheIdxOfPos(long pos) {
    int idx = -1;
    if (cachePos <= pos && pos < cachePos + cacheLen) {
      idx = (int)(pos - cachePos);
    }
    return idx;
  }
  
  @Override
  public int read() throws IOException {
    if (h == 0L) throw new IOException("Stream closed");
    
    int v = -1;
    
    int cacheIdx = cacheIdxOfPos(pos);
    if (cacheIdx < 0) {
      cacheLen = get(h, pos, cache, 0, cache.length);
      if (cacheLen > 0) {
        cachePos = pos;
        cacheIdx = cacheIdxOfPos(pos);
        if (cacheIdx >= 0) {
          v = ((int)cache[cacheIdx]) & 0xFF;
          pos++;
        }
      }
    }
    else {
      v = ((int)cache[cacheIdx]) & 0xFF;
      pos++;
    }
      
    return v;
  }
  
  @Override
  public int read(byte[] buf, int offs, int len) throws IOException {
    if (h == 0L) throw new IOException("Stream closed");
    if (buf == null) throw new NullPointerException();
    if (offs < 0 || len < 0 || offs + len > buf.length) throw new IndexOutOfBoundsException();
    
    cacheLen = 0;
    
    int n = get(h, pos, buf, offs, len);
    if (n > 0) {
      pos += n;
    }
    return n;
  }
  
  @Override
  public int read(byte[] b) throws IOException {
    return read(b, 0, b.length);
  };
  
  @Override
  public int available() throws IOException {
    if (h == 0L) throw new IOException("Stream closed");
    long s = size(h);
    return (int)(Math.max( s - pos, 0 ) & 0xFFFFFFFF);
  };

  @Override
  public void close() throws IOException {
    if (h != 0L) {
      free(h);
      h = 0L;
    }
  };
  
  @Override
  public void mark(int readlimit) {
    markPos = pos;
  };
  
  @Override
  public boolean markSupported() {
    return true;
  };
  
  @Override
  public void reset() throws IOException {
    pos = markPos;
  };
  
  @Override
  public long skip(long n) throws IOException {
    long s = size(h);
    if (pos + n > s) {
      n = Math.max(s - pos, 0);
    }
    pos += n;
    return n;
  };

  private static native int get(long h, long pos);
  private static native int get(long h, long pos, byte[] buf, int offs, int len);
  private static native long size(long h);
  private static native void free(long h);
}
