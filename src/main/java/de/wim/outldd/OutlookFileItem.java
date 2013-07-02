/*
    Copyright (c) 2011 Wolfgang Imig
    
    This file is part of the library "Java Drag&Drop for Microsoft Outlook".

    This file can be used by the terms of   
      
      - Wolfgang Imig "Java Drag&Drop for Microsoft Outlook" END USER LICENSE AGREEMENT TERMS AND CONDITIONS

*/
package de.wim.outldd;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

/**
 * This class implements access to the content of an Outlook item.
 */
public class OutlookFileItem implements OutlookItem {

  /**
   * @see OutlookItem#getFileName()
   */
  public String getFileName() {
    return fdesc.getName();
  }

  /**
   * @see OutlookItem#getContent()
   */
  public InputStream getContent() throws IOException {
    return OutlookDD.nativeLib.getFileContent( fileIndex );
  }

  /**
   * @see OutlookItem#saveContent(File)
   */
  public void saveContent(File file) throws IOException {
    OutlookDD.nativeLib.saveFileContent( fileIndex, file );
  }
  
  /**
   * Constructor.
   * The <code>fileIndex</code> is used to extract the 
   * content from the underlying IDataObject.
   * The <code>fileDescriptor<code> is used in the function <code>getFileName</code> 
   * to obtain a file name conforming to the Windows file system.  
   * @param fileIndex File index
   * @param fdesc FileDescriptor object
   */
  protected OutlookFileItem(int fileIndex, FileDescriptor fdesc) {
    this.fileIndex = fileIndex;
    this.fdesc = fdesc;
  }
  
  /**
   * Creates objects of this class for each FileDescriptor object.
   * @param fdescs FileDescriptor objects
   * @return Array of OutlookFileItem
   */
  protected static ArrayList<OutlookFileItem> makeItems(ArrayList<FileDescriptor> fdescs) {
    ArrayList<OutlookFileItem> items = new ArrayList<OutlookFileItem>(fdescs.size()); 
    for (int fileIndex = 0; fileIndex < fdescs.size(); fileIndex++) {
      items.add(new OutlookFileItem(fileIndex, fdescs.get(fileIndex)));
    }
    return items;
  }
  
  /**
   * File index.
   */
  protected int fileIndex;
  
  /**
   * File descriptor
   */
  protected FileDescriptor fdesc;
}
