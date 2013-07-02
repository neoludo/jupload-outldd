/*
    Copyright (c) 2011 Wolfgang Imig
    
    This file is part of the library "Java Drag&Drop for Microsoft Outlook".

    This file can be used by the terms of   
      
      - Wolfgang Imig "Java Drag&Drop for Microsoft Outlook" END USER LICENSE AGREEMENT TERMS AND CONDITIONS

*/
package de.wim.outldd.examples;

import java.security.AccessController;
import java.security.PrivilegedAction;

import javax.swing.JApplet;
import javax.swing.SwingUtilities;

import de.wim.outldd.OutlookDD;

public class DragDropTest_Applet extends JApplet {

  private static final long serialVersionUID = 1L;
  
  public DragDropTest_Applet() {
    AccessController.doPrivileged(new PrivilegedAction<Object>() {
      public Object run() {
        //OutlookDD.setLogFile(new File("d:\\temp\\outldd\\outldd.log"), true);
        OutlookDD.init(OutlookDD.MODE_TEMP_FILES);
        return null;
      }
    });
  }
  
  public void init() {
    try {
      SwingUtilities.invokeAndWait(new Runnable() {
        public void run() {
          setContentPane(new DragDropTest_AppletPanel());             
        }
      });
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

}
