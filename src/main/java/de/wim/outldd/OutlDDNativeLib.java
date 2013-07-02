/*
    Copyright (c) 2011 Wolfgang Imig
    
    This file is part of the library "Java Drag&Drop for Microsoft Outlook".

    This file can be used by the terms of   
      
      - Wolfgang Imig "Java Drag&Drop for Microsoft Outlook" END USER LICENSE AGREEMENT TERMS AND CONDITIONS

*/

package de.wim.outldd;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.net.URL;

/**
 * This class defines the native functions that are implemented in 
 * the JNI library OutlDD.dll.
 * This functions should not be called directly, use OutlookDD instead.  
 * @see de.wim.outldd.OutlookDD
 */
class OutlDDNativeLib {

  /**
   * Sub directory in TEMP used for native libs. 
   * Is appended by _x64 or _x86.
   */
  private static final String OUTLDD_TEMP_LIBDIR = "de_wim_outldd_libs_2_0_5_0";
  
  private static final String DEBUG_LIB = null;
  
  // Load native libs
  static {
    try {
      
      if (DEBUG_LIB != null) {
        System.loadLibrary(DEBUG_LIB);
      }
      else {
        boolean x64 = is64();
        String subdir = "mingw" + "/" + (x64 ? "x64" : "x86");
        String suffix = (x64 ? "x64" : "x86");
        loadDll( subdir, "outldd_"+suffix );
      }
    }
    catch (UnsatisfiedLinkError e) {
      throw e;
    }
  }
  
  /**
   * Is 64bit JVM?
   * @return true, if 64bit
   */
  private static boolean is64() {
    String s = System.getProperty("os.arch");
    return s != null && s.indexOf("64") >= 0;
  }
    
  /**
   * Copy the DLL from the JAR file into the TEMP directory and load it.
   * @param subdir Subpackage of this.package that contains the DLL. 
   * @param lname DLL name without extension
   * @throws UnsatisfiedLinkError
   */
  private static void loadDll(String subdir, String lname) throws UnsatisfiedLinkError {
    final int retries = 10;
    try {
      String inst = "";
      for (int i = 1; i <= retries; i++) {
        try {
          loadDllFromResource(subdir + inst, subdir, lname);
          break;
        }
        catch (UnsatisfiedLinkError e) {
          // Maybe e="...native lib... already loaded in another class loader..."
          // -> retry in another temp dir
          if (i == retries) throw e;
          inst = "_" + i;
        }
      }
    }
    catch (IOException e) {
      System.err.println("OUTLDD: loadDllFromResource failed");
      e.printStackTrace();
      throw new UnsatisfiedLinkError(e.toString());
    }
  }
  
  /**
   * Copy library from JAR into TEMP directory and load it.
   * @param tempSubdir Subpackage of this.package that contains the DLL.
   * @param packageDir Load DLL from this subpackage 
   * @param lname DLL name without extension
   * @throws UnsatisfiedLinkError
   * @throws SecurityException Thrown, if the JVM cannot create a directory or file in TEMP
   */
  private static void loadDllFromResource(String tempSubdir, String packageDir, String lname) throws UnsatisfiedLinkError, IOException {
    String dllName = lname + ".dll";
    InputStream is = null;
    try {
      
      String tmpdir = System.getProperty("java.io.tmpdir");
      File libdir = new File(new File(tmpdir, OUTLDD_TEMP_LIBDIR), tempSubdir);
      libdir.mkdirs();
      
      File dll = new File(libdir, dllName);
      if (!dll.exists()) {

        String resPath = packageDir + "/" + dllName;
        
        URL res = OutlDDNativeLib.class.getResource(resPath);
        if (res == null) {
          throw new UnsatisfiedLinkError("DLL " + resPath + " not found in JAR");
        }
        
        is = res.openStream();

        FileOutputStream fos = null;
        try {
          fos = new FileOutputStream(dll);
    
          byte[] buf = new byte[1024 * 8];
          int n = 0;
          while ((n = is.read(buf)) > 0) {
              fos.write(buf, 0, n);
          }
        }
        catch (IOException ignored) {
          // DLL may be in use
        }
        finally {
          if (is != null) try { is.close(); is = null; } catch (IOException ignored) {}
          if (fos != null) try { fos.close(); fos = null; } catch (IOException ignored) {}
        }

      }
      
      final File pdll = dll;
      System.load(pdll.getAbsolutePath());
    }
    catch (IOException e) {
      System.err.println("OUTLDD: load library failed: " + e);
      throw e;
    }
    finally {
      if (is != null) try { is.close(); } catch (IOException ignored) {}
    }
  }
  
  OutlDDNativeLib() {
    getLicense(new boolean[1]);
  }
  
  /**
   * Read and pass the license information to the DLL. 
   * Read the license.properties file in the root directory of the JAR 
   * and call the checkLicense function.
   * If the license file is not found or it does not include a valid 
   * license string, an error message is printed on System.err.
   * In this case the DLL does not handle D&D data from Outlook.
   * This function is called in the constructor of OutlDDNativeLib. 
   * @param rValid An array of size one. rValid[0] is true if the license is valid
   * @return License text.
   */
  String getLicense(boolean[] rValid) {
    
    final String licenseProperties = "outldd.license";
    String lic = null;
    LineNumberReader lr = null;
    
    try {
      URL res = getClass().getResource(licenseProperties);
      if (res != null) {
        InputStream is = new BufferedInputStream(res.openStream(), 3);
        is.mark(4);
        InputStreamReader rd = null;
        
        byte[] bom = new byte[3];
        int bomlen = is.read(bom);
        if (bomlen >= 2 && bom[0] == (byte)0xFF && bom[1] == (byte)0xFE) {
          is.reset(); is.skip(2);
          rd = new InputStreamReader(is, "UTF-16LE");
        }
        else if (bomlen >= 2 && bom[0] == (byte)0xFE && bom[1] == (byte)0xFF) {
          is.reset(); is.skip(2);
          rd = new InputStreamReader(is, "UTF-16BE");
        }
        else if (bomlen >= 3 && bom[0] == (byte)0xEF && bom[1] == (byte)0xBB && bom[2] == (byte)0xBF) {
          rd = new InputStreamReader(is, "UTF-8");
        }
        else {
          is.reset();
          rd = new InputStreamReader(is);
        }
        
        lr = new LineNumberReader(rd);
        lic = lr.readLine();
        if (lic == null) {
          System.err.println("OUTLDD: empty license file " + licenseProperties + " in outldd.jar.");
        }
      }
      else {
        String dir = getClass().getPackage().getName();
        dir = dir.replaceAll("[\\.]", "/");
        String fpath = dir + "/" + licenseProperties;
        System.err.println("OUTLDD: license file not found. " +
            "Make sure to have a license file \"" + fpath + "\" in outldd.jar.");
      }
    }
    catch (IOException ignored) {}
    finally {
      if (lr != null) {
        try { lr.close(); } catch (IOException ignored) {}
      }
    }

    if (lic != null) {
      lic = lic.trim();
      boolean succ = rValid[0] = checkLicense(lic);
      if (!succ) System.err.println("OUTLDD: license check failed.");
    }
    
    return lic;
  }
  
  native boolean enableDragDrop(int mode);
  
  native void setLogFile(File file, boolean append);
  
  native void setTempDir(File dir);
  
  native boolean isOutlookDataAvail();

  native boolean isOutlookClipboardDataAvail();

  native void releaseOutlookData(); 
  
  native InputStream getFileGroupDescriptor();

  native InputStream getFileContent(int fileIndex) throws IOException;
  
  native void saveFileContent(int fileIndex, File file) throws IOException;

  native String getText();
  
  native boolean checkLicense(String lic);
}

