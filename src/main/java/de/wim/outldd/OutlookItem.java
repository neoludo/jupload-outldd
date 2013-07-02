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

/**
 * This interface defines the operations on an Outlook item that is being dragged.  
 */
public interface OutlookItem {
  /**
   * Returns the name of the Outlook item in a format that conforms to the Windows file system.
   * This name includes an extension like ".msg" or ".xnk".   
   * @return Item name
   */
  public String getFileName();
  
  /**
   * Returns an InputStream to read the Outlook item content.
   * The entire item content is read into a native memory block.
   * This memory block is returned wrapped into an 
   * <code>HGlobalInputStream</code> object.  
   * @return InputStream
   */
  public InputStream getContent() throws IOException ;
  
  /**
   * Saves the contents of the Outlook item into the given file.
   * The entire item content is read into a native memory block
   * and is saved into the file.
   * @param file File object
   */
  public void saveContent(File file) throws IOException ;
}
