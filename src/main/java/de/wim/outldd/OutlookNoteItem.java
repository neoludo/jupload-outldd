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
 * This class identifies an Outlook item as a note.
 * Beyond the file content, Outlook supports only a name for the note in the text 
 * member of the D&D data. 
 */
public class OutlookNoteItem extends OutlookFileItem {

  /**
   * Note name
   * @return property
   */
  public String getName() {
    return name;
  }

  /**
   * Constructor
   * @param name Note name
   * @param fileIndex File index
   * @param fdesc FileDescriptor object
   */
  protected OutlookNoteItem(String name, int fileIndex, FileDescriptor fdesc) {
    super(fileIndex, fdesc);
    this.name = name.trim();
  }
  
  /**
   * Creates an array of items if the <code>text</code> information defines 
   * note items.
   * @param text Text information of the underlying IDataObject
   * @param fdescs FileDescriptor objects from the underlying IDataObject
   * @return Array of items or null, if the <code>text</code> information does not 
   * fit the format of note items.
   */
  protected static ArrayList<OutlookNoteItem> makeItems(String text, ArrayList<FileDescriptor> fdescs) {
    ArrayList<OutlookNoteItem> items = null;
    
    if (text.startsWith("\t\r\n")) {
      
      items = new ArrayList<OutlookNoteItem>(fdescs.size());

      StringReader sr = new StringReader(text);
      LineNumberReader lr = new LineNumberReader(sr);
      try {
        String line = lr.readLine();
        for (int fileIndex = 0; fileIndex < fdescs.size(); fileIndex++) {
          line = lr.readLine();
          FileDescriptor fdesc = fdescs.get(fileIndex);
          items.add(new OutlookNoteItem(line, fileIndex, fdesc));
        }
      }
      catch (IOException ignored) {}
    }
    return items;
  }
  
  protected String name;

}
