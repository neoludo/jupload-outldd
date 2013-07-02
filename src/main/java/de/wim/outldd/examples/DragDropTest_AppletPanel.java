package de.wim.outldd.examples;

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
import java.util.TooManyListenersException;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import de.wim.outldd.OutlookDD;
import de.wim.outldd.OutlookData;
import de.wim.outldd.OutlookItem;

public class DragDropTest_AppletPanel extends JPanel {

  private static final long serialVersionUID = 1L;

  DragDropTest_AppletPanel() {
    
    JLabel txt = new JLabel("<html>Drop Outlook mails,<br/>calendar items,<br/>contacts,<br/>mail attachments...<br/>&nbsp;</html>");
    this.add(txt);
    
    JButton button = new JButton("...or paste from clipboard");
    this.add(button);
    
    DropTarget dropTarget = new DropTarget();
    dropTarget.setComponent(this);

    try {
      dropTarget.addDropTargetListener(new DropTargetAdapter() {
        @Override
        public void dragEnter(java.awt.dnd.DropTargetDragEvent dtde) {
          boolean succ = dtde.getTransferable().isDataFlavorSupported(DataFlavor.javaFileListFlavor);
          System.out.println("enter: avail=" + succ);
          super.dragEnter(dtde);
        };

        @Override
        public void dragOver(java.awt.dnd.DropTargetDragEvent dtde) {
          boolean dropFiles = dtde.getTransferable().isDataFlavorSupported(DataFlavor.javaFileListFlavor);
          System.out.println("over: file list avail=" + dropFiles);
          
          int action = dtde.getDropAction();
          if (!dropFiles) action = DnDConstants.ACTION_NONE;
          
          if (action == DnDConstants.ACTION_MOVE) action = DnDConstants.ACTION_COPY;
          
          dtde.acceptDrag(action);

          super.dragOver(dtde);
        };

        public void drop(DropTargetDropEvent dtde) {
          try {
            int action = dtde.getDropAction();
            if (action == DnDConstants.ACTION_MOVE) action = DnDConstants.ACTION_COPY;
            dtde.acceptDrop(action);

            Transferable t = dtde.getTransferable();
            if (t.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
              
              List<?> list = (List<?>) t.getTransferData(DataFlavor.javaFileListFlavor);
              for (final Object o : list) {
                System.out.println("file=" + o);
                showMessageBox("You dropped the file=\n" + o);
              }
            }
          } catch (Exception e) {
            e.printStackTrace();
          } finally {
            dtde.dropComplete(true);
          }

//        // Test: other heavyweight components do not disturb the D&D functionality
//        SwingUtilities.invokeLater(new Runnable() {
//          public void run() {
//            for (int i = 0; i < 3; i++) {
//               JDialog dlg = new JDialog();
//               dlg.setBounds(10,10, 300, 300);
//               dlg.setTitle("Dialog");
//               JTextField txt = new JTextField();
//               dlg.add(txt);
//               dlg.setVisible(true);
//            }
//          }
//         });
          
        }
      });
      
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
          else {
            showMessageBox("Clipboard does not contain Outlook items");
          }
        }
      });
      
    } catch (TooManyListenersException e) {
      e.printStackTrace();
    }
  }
  
  private void handleOutlookData(OutlookData outlData) throws IOException {
    List<? extends OutlookItem> items = outlData.getItems();
    if (items.size() != 0) {
      for (final OutlookItem item : items) {

        // save the Outlook item to a temporary file
        // or call fitem.getContent() to get an InputStream 
        
        final File file = new File(System.getProperty("java.io.tmpdir"), item.getFileName());
        item.saveContent(file);
        System.out.println("oitem.class=" + item.getClass().getName() + ", value=" + item + ", file=" + file);
   
        showMessageBox("You dropped the item=\n" + item + "\ntemp.file=" + file);
      }
    }
  }

  private void showMessageBox(final String msg) {
    SwingUtilities.invokeLater(new Runnable() {
      public void run() {
        JOptionPane.showMessageDialog(null, msg, "Applet", JOptionPane.INFORMATION_MESSAGE);
      }
    });

  }
}
