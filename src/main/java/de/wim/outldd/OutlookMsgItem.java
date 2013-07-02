/*
    Copyright (c) 2011 Wolfgang Imig
    
    This file is part of the library "Java Drag&Drop for Microsoft Outlook".

    This file can be used by the terms of   
      
      - Wolfgang Imig "Java Drag&Drop for Microsoft Outlook" END USER LICENSE AGREEMENT TERMS AND CONDITIONS

*/
package de.wim.outldd;

import java.io.IOException;
import java.io.LineNumberReader;
import java.io.StringReader;
import java.util.ArrayList;

/**
 * This class identifies an <code>OutlookItem</code> as a mail or task item.
 * Beyond the content information, Outlook sends some properties (subject, receive data, etc.) 
 * for mail and task items formatted as a data table in the text information of the D&D data. 
 * This properties are parsed and the function <code>getProperties</code>
 * provides access to them. 
 * @see OutlookMsgProperties#init(java.util.ResourceBundle) 
 */
public class OutlookMsgItem extends OutlookFileItem {
  
  /**
   * Return the properties of this Outlook item. 
   * @return Properties
   */
  public OutlookMsgProperties getProperties() {
    return properties;
  }
  
  public String toString() {
    return "[" + properties + "]";
  }
  
  /**
   * Constructor.
   * @param propNames Array of property names. This is the header row of the D&D text data split in columns. 
   * @param propLine This is a row in the D&D text data that contains the properties for this item separated by tabulator characters.  
   * @param fileIndex File index
   * @param fdesc FileDescriptor object
   */
  protected OutlookMsgItem(String[] propNames, String propLine, int fileIndex, FileDescriptor fdesc) {
    super(fileIndex, fdesc);
    readPropertiesFromLine(propNames, propLine);
  }

  /**
   * Parses the row of property values.
   * @param propNames Property names
   * @param propLine Property row
   * @see OutlookMsgItem#OutlookMsgItem(String[], String, int, FileDescriptor)
   */
  protected void readPropertiesFromLine(String[] propNames, String propLine) {
    properties = new OutlookMsgProperties();
    int q = -1;
    for (int i = 0; i < propNames.length; i++) {
      int p = q+1;
      q = propLine.indexOf('\t', p); // cannot use String.split because it skips \t\t - I need an empty String in this case. 
      String propValue = q >= 0 ? propLine.substring(p, q) : "";
      properties.put(propNames[i], propValue);
    }
  }

  /**
   * Creates an array of items if the <code>text</code> information defines 
   * mail or task items.
   * @param text Text information of the underlying IDataObject
   * @param fdescs FileDescriptor objects from the underlying IDataObject
   * @return Array of items or null, if the <code>text</code> information does not 
   * fit the format of mail or task items.
   */
  protected static ArrayList<OutlookMsgItem> makeItems(String text, ArrayList<FileDescriptor> fdescs) {
    ArrayList<OutlookMsgItem> items = null;
    
    if (!text.startsWith("\t") && 
        !text.startsWith("\r\n") && 
        text.indexOf('\t') > 0) {
      
      items = new ArrayList<OutlookMsgItem>(fdescs.size());

      StringReader sr = new StringReader(text);
      LineNumberReader lr = new LineNumberReader(sr);
      try {
        String line = lr.readLine();
        String[] propNames = line.split("\t");
        for (int fileIndex = 0; fileIndex < fdescs.size(); fileIndex++) {
          line = lr.readLine();
          FileDescriptor fdesc = fdescs.get(fileIndex);
          items.add(new OutlookMsgItem(propNames, line, fileIndex, fdesc));
        }
      }
      catch (IOException ignored) {}
    }
    return items;
  }

  protected OutlookMsgProperties properties;
  
  
}
