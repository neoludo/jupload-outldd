/*
    Copyright (c) 2011 Wolfgang Imig
    
    This file is part of the library "Java Drag&Drop for Microsoft Outlook".

    This file can be used by the terms of   
      
      - Wolfgang Imig "Java Drag&Drop for Microsoft Outlook" END USER LICENSE AGREEMENT TERMS AND CONDITIONS

*/
package de.wim.outldd;

import java.util.ArrayList;

/**
 * This class identifies an <code>OutlookItem</code> as a 
 * contact item. 
 * It receives the contact properties from the text information
 * provided by Outlook. Since Outlook does not send the 
 * contact data in a reliably format, the properties can
 * only be used as a comment but not in program logic.
 */
public class OutlookContactItem extends OutlookFileItem {
  
  public String[] getProperties() {
    return properties;
  }
  
  public String toString() {
    return "[" + properties + "]";
  }
  
  protected OutlookContactItem(String text, int fileIndex, FileDescriptor fdesc) {
    super(fileIndex, fdesc);
    properties = text.split("\r\n");
  }
  
  protected static ArrayList<OutlookContactItem> makeItems(String text, ArrayList<FileDescriptor> fdescs) {

    ArrayList<OutlookContactItem> items = null;
    
    if (!text.startsWith("\t") &&
        !text.startsWith("\r\n") && 
        text.indexOf('\t') < 0) {

      items = new ArrayList<OutlookContactItem>(fdescs.size());

      int q = -1;
      for (int fileIndex = 0; fileIndex < fdescs.size(); fileIndex++) { 
        int p = q+1;
        q = text.indexOf("\r\n\r\n", p);  
        String contactText = q >= 0 ? text.substring(p, q) : "";
        OutlookContactItem item = new OutlookContactItem(contactText, fileIndex, fdescs.get(fileIndex));
        items.add(item);
        if (q < 0) q = text.length()-1;
      }
    }
    
    return items;
  }
  
  protected String[] properties;
}
