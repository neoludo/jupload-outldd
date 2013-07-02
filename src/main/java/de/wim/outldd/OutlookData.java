/*
    Copyright (c) 2011 Wolfgang Imig
    
    This file is part of the library "Java Drag&Drop for Microsoft Outlook".

    This file can be used by the terms of   
      
      - Wolfgang Imig "Java Drag&Drop for Microsoft Outlook" END USER LICENSE AGREEMENT TERMS AND CONDITIONS

*/
package de.wim.outldd;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * This class provides access to the Drag&Drop data provided by Outlook.
 */
public class OutlookData {

  /**
   * Return the Outlook items currently dragged. 
   * @return items Outlook items (mails, attachments, contacts, etc.)
   */
  public List<? extends OutlookItem> getItems() {
    return items;
  }
  
  protected OutlookData() {
    ArrayList<FileDescriptor> fdescs = internalReadFileGroupDescriptor();
    items = internalMakeItems(fdescs);
  }
  
  protected ArrayList<FileDescriptor> internalReadFileGroupDescriptor() {
    ArrayList<FileDescriptor> fdescs = new ArrayList<FileDescriptor>();
    if (OutlookDD.nativeLib != null) {
      InputStream ris = null;
      Win32DataInputStream is = null;
      try {
        ris = OutlookDD.nativeLib.getFileGroupDescriptor();
        is = new Win32DataInputStream( ris );
        int nbOfFiles = is.readInt();
        fdescs.ensureCapacity(nbOfFiles);
        for (int i = 0; i < nbOfFiles; i++) {
          FileDescriptor fileDesc = new FileDescriptor();
          fileDesc.read( is );
          fdescs.add(fileDesc);
        }
      }
      catch (IOException ignored) {
       fdescs = null; 
      } 
      finally {
        if (is != null) try { is.close(); } catch (IOException ignored) {}
        if (ris != null) try { ris.close(); } catch (IOException ignored) {}
      }
    }
    return fdescs; 
  }
  
  protected ArrayList<? extends OutlookItem> internalMakeItems(ArrayList<FileDescriptor> fdescs) {
    ArrayList<? extends OutlookItem> items = null;
    if (OutlookDD.nativeLib != null) {
      
      try {
        String text = OutlookDD.nativeLib.getText();
        if (text != null && text.length() != 0) {
          items = OutlookMsgItem.makeItems(text, fdescs);
          if (items == null) {
            items = OutlookContactItem.makeItems(text, fdescs);
            if (items == null) {
              items = OutlookNoteItem.makeItems(text, fdescs);
            }
          }
        }
      }
      catch (Throwable e) {
        System.err.println("OutlookDragDrop error");
        e.printStackTrace(System.err);
      }
      
      if (items == null) {
        items = OutlookFileItem.makeItems(fdescs);
      }
    }
    return items;
  }
  
  protected ArrayList<? extends OutlookItem> items;
}
