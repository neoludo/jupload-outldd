/*
    Copyright (c) 2011 Wolfgang Imig
    
    This file is part of the library "Java Drag&Drop for Microsoft Outlook".

    This file can be used by the terms of   
      
      - Wolfgang Imig "Java Drag&Drop for Microsoft Outlook" END USER LICENSE AGREEMENT TERMS AND CONDITIONS

*/
package de.wim.outldd.examples;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetAdapter;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;

import de.wim.outldd.OutlookDD;
import de.wim.outldd.OutlookData;
import de.wim.outldd.OutlookItem;
import de.wim.outldd.OutlookMsgItem;
import de.wim.outldd.OutlookMsgProperties;

/**
 * This example shows how to use the MODE_MEMORY_STREAM mode.
 */
public class DragDropTest_ModeMemoryStream extends JFrame {
  
  
  public DragDropTest_ModeMemoryStream() throws Exception {
    this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    this.setBounds(0, 0, 400, 300);
    this.setVisible(true);
    this.setTitle("Example for MODE_MEMORY_STREAM");
    
    JLabel txt = new JLabel("Drop here");
    this.getContentPane().add(txt);
    txt.setVisible(true);
    
    JButton button = new JButton("Paste from clipboard");
    this.getContentPane().add(button, "South");

    DropTarget dropTarget = new DropTarget();
    dropTarget.setComponent(this);
    dropTarget.addDropTargetListener(new DropTargetAdapter() {
      @Override
      public void dragEnter(java.awt.dnd.DropTargetDragEvent dtde) {
        boolean dropFiles = dtde.getTransferable().isDataFlavorSupported(DataFlavor.javaFileListFlavor);
        dropFiles |= OutlookDD.isDataAvail(); // true, if this is a D&D operation from Outlook 
        System.out.println("enter: file list avail=" + dropFiles);
        super.dragEnter(dtde);
      };

      @Override
      public void dragOver(java.awt.dnd.DropTargetDragEvent dtde) {
        boolean dropFiles = dtde.getTransferable().isDataFlavorSupported(DataFlavor.javaFileListFlavor);
        dropFiles |= OutlookDD.isDataAvail(); // true, if this is a D&D operation from Outlook
        System.out.println("over: file list avail=" + dropFiles);

        int action = dtde.getDropAction();
        if (!dropFiles) action = DnDConstants.ACTION_NONE;
        
        // Un-comment this line, if you do not want to remove the Email from Outlook:
        //if (action == DnDConstants.ACTION_MOVE) action = DnDConstants.ACTION_COPY;
        
        dtde.acceptDrag(action);
        
        super.dragOver(dtde);
      };

      public void drop(DropTargetDropEvent dtde) {
        try {
          
          int action = dtde.getDropAction();
          
          // Un-comment this line, if you do not want to remove the Email from Outlook:
          //if (action == DnDConstants.ACTION_MOVE) action = DnDConstants.ACTION_COPY;

          dtde.acceptDrop(action);

          
          Transferable t = dtde.getTransferable();
          if (OutlookDD.isDataAvail()) { // true, if this is a D&D operation from Outlook
            try {
              OutlookData outlData = OutlookDD.getData();
              handleOutlookData(outlData);
            } finally {
              OutlookDD.release();
            }
            
//          // Test: other heavyweight components do not disturb the D&D functionality
//          SwingUtilities.invokeLater(new Runnable() {
//            public void run() {
//              for (int i = 0; i < 3; i++) {
//                 JDialog dlg = new JDialog();
//                 dlg.setBounds(10,10, 300, 300);
//                 dlg.setTitle("Dialog");
//                 JTextField txt = new JTextField();
//                 dlg.add(txt);
//                 dlg.setVisible(true);
//              }
//            }
//           });
            
          }
          else if (t.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
              
              List<?> list = (List<?>) t.getTransferData(DataFlavor.javaFileListFlavor);
              for (Object o : list) {
                System.out.println("file=" + o);
              }
          }
          
        } catch (Exception e) {
          e.printStackTrace();
        } finally {
          dtde.dropComplete(true);
        }
      }

    });

    // On-click-handler for button "Paste..."
    button.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent evt) {

        // First check for Outlook clipboard data
        if (OutlookDD.isClipboardDataAvail()) { 
          try {
            OutlookData outlData = OutlookDD.getClipboardData();
            handleOutlookData(outlData);
          } catch (Exception e) {
            e.printStackTrace();
          } finally {
            OutlookDD.release();
          }
        }
        
        // Handle other clipboard formats using the 
        // AWT clipboard functions.
        else {
          String text = getTextFromSystemClipboard();
          System.out.println("text from clipboard=" + text);
        }
      }
    });
    
  }

  private String getTextFromSystemClipboard() {
    String result = "";
    Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
    Transferable contents = clipboard.getContents(null);
    boolean hasTransferableText = (contents != null) && contents.isDataFlavorSupported(DataFlavor.stringFlavor);
    if (hasTransferableText) {
      try {
        result = (String)contents.getTransferData(DataFlavor.stringFlavor);
      }
      catch (Exception ex){
        ex.printStackTrace();
      }
    }
    return result;
  }

  private void handleOutlookData(OutlookData outlData) throws IOException {
    List<? extends OutlookItem> items = outlData.getItems();
    if (items.size() != 0) {
      for (OutlookItem item : items) {

        
        File file = new File(System.getProperty("java.io.tmpdir"), item.getFileName());
        System.out.println("oitem.class=" + item.getClass().getName() + ", value=" + item + ", file=" + file);
        
        // Save the Outlook item to a temporary file
        item.saveContent(file);
        
        // ... Or call fitem.getContent() to get an InputStream
//        {
//	        FileOutputStream fos = null;
//	        InputStream is = null;
//	        try {
//	        	fos = new FileOutputStream(file);
//	        	is = item.getContent();
//		        byte[] buf = new byte[100*1000];
//		        int n = 0;
//		        while ((n = is.read(buf)) >= 0) {
//		        	fos.write(buf, 0, n);
//		        }
//	        }
//	        finally {
//	        	if (is != null) try { is.close(); } catch (Exception ignored) {}
//	        	if (fos != null) try { fos.close(); } catch (Exception ignored) {}
//	        }
//        }
   
        // print mail properties
        if (item instanceof OutlookMsgItem) {
          OutlookMsgItem msg = (OutlookMsgItem)item;
          OutlookMsgProperties props = msg.getProperties();
          System.out.println("from=" + props.getFrom()
              + ", subject=" + props.getSubject()
              + ", received=" + props.getReceived());
        }
      }
    }
  }

  public static void main(String[] args) throws Exception {
    
    //Optional: set a log file
    OutlookDD.setLogFile(new File("d:\\temp\\outldd\\outldd.log"), true);
    
    OutlookDD.init(OutlookDD.MODE_MEMORY_STREAMS);

    new DragDropTest_ModeMemoryStream();
  }

  private static final long serialVersionUID = 1L;


}