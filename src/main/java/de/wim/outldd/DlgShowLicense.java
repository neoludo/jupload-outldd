/*
    Copyright (c) 2011 Wolfgang Imig
    
    This file is part of the library "Java Drag&Drop for Microsoft Outlook".

    This file can be used by the terms of   
      
      - Wolfgang Imig "Java Drag&Drop for Microsoft Outlook" END USER LICENSE AGREEMENT TERMS AND CONDITIONS

*/
package de.wim.outldd;

import javax.swing.JOptionPane;

/**
 * This class shows the license information in a message box.
 * [1][OUTLDD DEMO LICENSE][30 DAYS][0][1265187410]
 */
public class DlgShowLicense {
  public static void main(String[] args) {

    OutlookDD.init(OutlookDD.MODE_MEMORY_STREAMS);
    
    boolean[] rValid = new boolean[1]; 
    String msg = OutlookDD.getLicense(rValid);

    if (msg == null) {
      msg = "License not found, missing /de/wim/outldd/outldd.license in JAR";
    }
    else {
      msg = (rValid[0] ?  "Valid license: " : "Invalid license or license expired: ") + msg;
    }
    int type = rValid[0] ? JOptionPane.INFORMATION_MESSAGE : JOptionPane.ERROR_MESSAGE;
    String title = rValid[0] ? "Valid License" : "Invalid License";
    
    JOptionPane.showMessageDialog(null, msg, title, type);    
  }
}
