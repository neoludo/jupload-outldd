/*
    Copyright (c) 2011 Wolfgang Imig
    
    This file is part of the library "Java Drag&Drop for Microsoft Outlook".

    This file can be used by the terms of   
      
      - Wolfgang Imig "Java Drag&Drop for Microsoft Outlook" END USER LICENSE AGREEMENT TERMS AND CONDITIONS

*/
package de.wim.outldd;

import java.io.File;


/**
 * This class is used to initialize the Drag&Drop support for Outlook
 * and obtains access to the information stored in the IDataObject 
 * used by Windows.
 * <p>   
 * The Drag&Drop support can be used in one or both of the two operation modes: 
 * MODE_TEMP_FILES and MODE_MEMORY_STREAMS.
 * </p>
 * <p>
 * MODE_TEMP_FILES: This mode replaces the original IDataObject sent
 * from Windows by an IDataObject that the Java AWT is able to evaluate.
 * When the AWT calls <code>DropTargetAdapter.dragEnter</code>, the new 
 * IDataObject is created and a temporary file name is supplied for each
 * Outlook item (mail, contact, etc.) contained in the original IDataObject.
 * At this time, the temporary files do not exist. Only when the function  
 * <code>DropTargetAdapter.drop</code> is called, the temporary
 * files are created.<br> 
 * The advantage of this mode is that existing programs 
 * only have to call the function OutlookDD.init() in order to receive Drag&Drop 
 * information from Outlook.<br>
 * The disadvantage is that the Outlook items have to 
 * be stored into a temporary directory. This is done before 
 * DropTargetAdapter.drop() is called by the AWT and it might need some seconds. 
 * During this time, the user interface seems to hang.
 * </p>
 * <p>
 * MODE_MEMORY_STREAMS: This mode provides access to the original IDataObject created
 * by Outlook. It does not create any temporary files. The advantage of 
 * this mode is that the programmer can inform the user about the progress
 * of data extraction from the IDataObject. The disadvantage is that 
 * the DropTargetAdapter functions of existing applications must
 * handle Outlook items separately.
 * </p>
 */
public class OutlookDD {

  /**
   * Mode MODE_MEMORY_STREAMS
   * @see #init(int)
   */
	public static int MODE_MEMORY_STREAMS = 1;
	
  /**
   * Mode MODE_MEMORY_STREAMS
   * @see #init(int)
   */
	public static int MODE_TEMP_FILES = 2;

  /**
   * Initialize the Outlook D&D functionality. 
   * This function loads the native library OutlDD.dll. 
   * @param mode one of the MODE_* constants
   * @see #MODE_TEMP_FILES
   * @see #MODE_MEMORY_STREAMS
   * @throws java.lang.UnsatisfiedLinkError ... if the OutlDD.dll cannot be found.
   */
  public static void init(int mode) {
    nativeLib = new OutlDDNativeLib(); 
    if (logFile != null) {
      nativeLib.setLogFile(logFile, appendLog);
    }
    if (tempDir != null) {
      nativeLib.setTempDir(tempDir);
    }
    nativeLib.enableDragDrop(mode);
  }
  
  /**
   * This function has to be called to stop Outlook D&D.
   */
  public static void done() {
  	if (nativeLib == null) return; 
    nativeLib.enableDragDrop(0);
  }

  /**
   * Call this function to check whether the current D&D operation 
   * contains data from Outlook.  
   * @return If Outlook data is dragged, the function returns <code>true</code>. 
   * If something else is dragged or if the native library could not be loaded, this function returns <code>false</code>.  
   */
  public static boolean isDataAvail() {
    if (nativeLib == null) return false; 
    return nativeLib.isOutlookDataAvail();
  }
  
  /**
   * Call this function to check whether the system clipboard 
   * contains data from Outlook.  
   * @return If Outlook data is found in the clipboard, the function returns <code>true</code>. 
   * If no Outlook data is found or if the native library could not be loaded, this function returns <code>false</code>.  
   */
  public static boolean isClipboardDataAvail() {
    if (nativeLib == null) return false; 
    return nativeLib.isOutlookClipboardDataAvail();
  }
  
  /**
   * Returns access to the Outlook data currently being dragged.
   * This function returns null, if the current drag operation does not 
   * contain data from Outlook.
   * @return If Outlook data is dragged, the function returns an <code>OutlookData</code> object. 
   * If something else is dragged or if the native library could not be loaded, this function returns <code>null</code>.  
   */
  public static OutlookData getData() {
    if (!OutlookDD.isDataAvail()) return null;
    return new OutlookData();
  }

  /**
   * Returns access to the Outlook data in the system clipboard.
   * This function returns null, if the system clipboard does not 
   * contain data from Outlook.
   * @return If Outlook data is contained in the clipboard, the function returns an <code>OutlookData</code> object. 
   * If no Outlook data is found or if the native library could not be loaded, this function returns <code>false</code>.  
   */
  public static OutlookData getClipboardData() {
    if (!OutlookDD.isClipboardDataAvail()) return null;
    return new OutlookData();
  }

  /**
   * Call this function after the drop operation has processed the current Outlook data.
   */
  public static void release() {
    if (nativeLib == null) return; 
    nativeLib.releaseOutlookData();
  }

  /**
   * This function sets a log file for the native library.
   * It can be called before <code>init()</code> is invoked.
   * @param file File object inside an existing directory.
   * @param append true, if existing log file should be appended. 
   */
  public static void setLogFile(File file, boolean append) {
	  logFile = file;
	  appendLog = append;
	  if (nativeLib != null) {
		  nativeLib.setLogFile(file, append);
	  }
  }

  /**
   * This function sets the directory internally used for temporary files.
   * A new directory is created  
   * @param dir Directory
   */
  public static void setTempDir(File dir) {
    tempDir = dir;
    if (nativeLib != null) {
      nativeLib.setTempDir(tempDir);
    }
  }
  
  /**
   * Read and check the license information.
   * @param rValid An array of size one. rValid[0] is true if the license is valid
   * @return License text.
   */ 
  public static String getLicense(boolean[] rValid) {
    String lic = null;
    rValid[0] = false;
    if (nativeLib != null) {
      lic = nativeLib.getLicense(rValid);
    }
    return lic;
  }
  
  /**
   * Native library wrapper class.
   */
  protected static OutlDDNativeLib nativeLib;
  /**
   * Log file supplied in <code>setLogFile</code>.
   */
  protected static File logFile;
  /**
   * Variable <code>append</code> that previously has been supplied in <code>setLogFile</code>.
   */
  protected static boolean appendLog;
  
  protected static File tempDir;
}
