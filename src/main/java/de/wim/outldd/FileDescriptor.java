/*
    Copyright (c) 2011 Wolfgang Imig
    
    This file is part of the library "Java Drag&Drop for Microsoft Outlook".

    This file can be used by the terms of   
      
      - Wolfgang Imig "Java Drag&Drop for Microsoft Outlook" END USER LICENSE AGREEMENT TERMS AND CONDITIONS

*/
package de.wim.outldd;

import java.io.IOException;
import java.io.Serializable;

/**
 * This class contains the information about one file
 * (an email or an attachment) that is dragged from Outlook.
 */
public class FileDescriptor implements Serializable {

  /**
   * File name.
   * If <code>this</code> represents an email, this function
   * returns the mail subject without the characters that are
   * forbidden in the Windows file system.
   * If <code>this</code> represents an attachment, the function
   * returns the file name of the attachment. 
   * @return File name
   */
  public String getName() {
    return name;
  }
  
  public String toString() {
    return "[" + name + "]";
  }
  
  FileDescriptor() {
  }

  void read(Win32DataInputStream is) throws IOException {

    /*
     * C declaration
    typedef struct _FILEDESCRIPTOR {
      DWORD    dwFlags;
      CLSID    clsid;
      SIZEL    sizel;
      POINTL   pointl;
      DWORD    dwFileAttributes;
      FILETIME ftCreationTime;
      FILETIME ftLastAccessTime;
      FILETIME ftLastWriteTime;
      DWORD    nFileSizeHigh;
      DWORD    nFileSizeLow;
      TCHAR    cFileName[MAX_PATH];
    } FILEDESCRIPTOR, *LPFILEDESCRIPTOR;
    */

    // I have never seen any values for the members of the FILEDESCRIPTOR
    // except for cFileName. The Member dwFlags is always 0. Thus, skip the values.
    
    is.skip(0
        +  4   // dwFlags
        + 16   // 128bit CLSID
        +  8   // SIZEL, int32 + int32
        +  8   // POINTL, int32 + int32
        +  4    // dwAttributes
        +  8    // ftCreationTime
        +  8    // ...
        +  8    // ...
        +  8    // fileSize
    );   

//    dwFlags = is.readInt();
//    is.readBytes( clsid );
//    sizeX = is.readInt();
//    sizeY = is.readInt();
//    pointX = is.readInt();
//    pointY = is.readInt();
//    dwFileAttributes = is.readInt();
//    ftCreationTime = is.readLong();
//    ftLastAccessTime = is.readLong();
//    ftLastWriteTime = is.readLong();
//    nFileSizeHigh = is.readInt();
//    nFileSizeLow = is.readInt();
       
    name = is.readUnicodeString( 260 ); // MAX_PATH=260
  }

//  private int dwFlags;
//  private byte[] clsid = new byte[16];
//  private int sizeX;
//  private int sizeY;
//  private int pointX;
//  private int pointY;
//  private int dwFileAttributes;
//  private long ftCreationTime;
//  private long ftLastAccessTime;
//  private long ftLastWriteTime;
//  private int nFileSizeHigh;
//  private int nFileSizeLow;
  
  private String name;
  
  private static final long serialVersionUID = 1L;

}
