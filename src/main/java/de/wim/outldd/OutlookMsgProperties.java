/*
    Copyright (c) 2011 Wolfgang Imig
    
    This file is part of the library "Java Drag&Drop for Microsoft Outlook".

    This file can be used by the terms of   
      
      - Wolfgang Imig "Java Drag&Drop for Microsoft Outlook" END USER LICENSE AGREEMENT TERMS AND CONDITIONS

*/
package de.wim.outldd;

import java.util.Properties;
import java.util.ResourceBundle;

/**
 * An object of this class provides access to the properties of a 
 * mail or task item sent by Outlook in the text information of the D&D data 
 * (IDataObject).
 * Unfortunately, the keys of the properties provided by Outlook are language specific. 
 * In order to provide some language independent getter functions for 
 * properties like "subject" and "from", a transformation of language independent keys
 * to language dependent keys is required. This transformation can be set as a
 * ResourceBundle in the init() function.
 */
public class OutlookMsgProperties extends Properties {

  /**
   * Language independent key for "subject".
   * Value = outldd.msg.prop.subject
   */
  public static String SUBJECT = "outldd.msg.prop.subject";
  /**
   * Language independent key for "from".
   * Value = outldd.msg.prop.from
   */
  public static String FROM = "outldd.msg.prop.from";
  /**
   * Language independent key for "received".
   * Value = outldd.msg.prop.received
   */
  public static String RECEIVED = "outldd.msg.prop.received";

  /**
   * Return the mail or task subject.
   * @return property
   */
  public String getSubject() {
    return this.getProperty(c_res.getString(SUBJECT));
  }
  
  /**
   * Return the sender of the mail.
   * @return property
   */
  public String getFrom() {
    return this.getProperty(c_res.getString(FROM));
  }
  
  /**
   * Return the date when the mail has been received.
   * @return property
   */
  public String getReceived() {
    return this.getProperty(c_res.getString(RECEIVED));
  }
  
  /**
   * Initialize the transformation of language independent 
   * property keys.
   * @param res ResourceBundle object
   */
  public static void init(ResourceBundle res) {
    c_res = res;
  }

  protected static ResourceBundle c_res = ResourceBundle.getBundle("de.wim.outldd.Res");
  private static final long serialVersionUID = 1L;
}
