/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bric.utils;

import org.bric.core.model.ImportedImage;

import javax.swing.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.util.ArrayList;

/**
 *
 * @author vasilis
 */
public class ArrayListTransferHandler extends TransferHandler {

    DataFlavor localArrayListFlavor, serialArrayListFlavor;
    String localArrayListType = DataFlavor.javaJVMLocalObjectMimeType
            + ";class=java.util.ArrayList";
    JList source = null;
    int[] indices = null;
    int addIndex = -1; //Location where items were added
    int addCount = 0; //Number of items added

    public ArrayListTransferHandler() {
        try {
            localArrayListFlavor = new DataFlavor(localArrayListType);
        } catch (ClassNotFoundException e) {
            System.out
                    .println("ArrayListTransferHandler: unable to create data flavor");
        }
        serialArrayListFlavor = new DataFlavor(ArrayList.class, "ArrayList");
    }

    @Override
    public boolean importData(JComponent c, Transferable t) {
        JList target = null;
        ArrayList alist = null;
        if (!canImport(c, t.getTransferDataFlavors())) {
            return false;
        }
        try {
            target = (JList) c;
            if (hasLocalArrayListFlavor(t.getTransferDataFlavors())) {
                alist = (ArrayList) t.getTransferData(localArrayListFlavor);
            } else if (hasSerialArrayListFlavor(t.getTransferDataFlavors())) {
                alist = (ArrayList) t.getTransferData(serialArrayListFlavor);
            } else {
                return false;
            }
        } catch (UnsupportedFlavorException ufe) {
            System.out.println("importData: unsupported data flavor");
            return false;
        } catch (IOException ioe) {
            System.out.println("importData: I/O exception");
            return false;
        }

        //At this point we use the same code to retrieve the data
        //locally or serially.

        //We'll drop at the current selected index.
        int index = target.getSelectedIndex();

        //Prevent the user from dropping data back on itself.
        //For example, if the user is moving items #4,#5,#6 and #7 and
        //attempts to insert the items after item #5, this would
        //be problematic when removing the original items.
        //This is interpreted as dropping the same data on itself
        //and has no effect.
        if (source.equals(target)) {
            if (indices != null && index >= indices[0] - 1
                    && index <= indices[indices.length - 1]) {
                indices = null;
                return true;
            }
        }

        DefaultListModel listModel = (DefaultListModel) target.getModel();
        int max = listModel.getSize();
        if (index < 0) {
            index = max;
        } else {
            index++;
            if (index > max) {
                index = max;
            }
        }
        addIndex = index;
        addCount = alist.size();
        for (int i = 0; i < alist.size(); i++) {
            listModel.add(index++, alist.get(i));
        }
        return true;
    }

    @Override
    protected void exportDone(JComponent c, Transferable data, int action) {
        if ((action == MOVE) && (indices != null)) {
            DefaultListModel model = (DefaultListModel) source.getModel();

            //If we are moving items around in the same list, we
            //need to adjust the indices accordingly since those
            //after the insertion point have moved.
            if (addCount > 0) {
                for (int i = 0; i < indices.length; i++) {
                    if (indices[i] > addIndex-1) {
                        indices[i] += addCount;
                    }
                }
            }
            for (int i = indices.length - 1; i >= 0; i--) {
                model.remove(indices[i]);
            }
        }
        indices = null;
        addIndex = -1;
        addCount = 0;
    }

    private boolean hasLocalArrayListFlavor(DataFlavor[] flavors) {
        if (localArrayListFlavor == null) {
            return false;
        }

        for (int i = 0; i < flavors.length; i++) {
            if (flavors[i].equals(localArrayListFlavor)) {
                return true;
            }
        }
        return false;
    }

    private boolean hasSerialArrayListFlavor(DataFlavor[] flavors) {
        if (serialArrayListFlavor == null) {
            return false;
        }

        for (int i = 0; i < flavors.length; i++) {
            if (flavors[i].equals(serialArrayListFlavor)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean canImport(JComponent c, DataFlavor[] flavors) {
        if (hasLocalArrayListFlavor(flavors)) {
            return true;
        }
        if (hasSerialArrayListFlavor(flavors)) {
            return true;
        }
        return false;
    }

    @Override
    protected Transferable createTransferable(JComponent c) {
        if (c instanceof JList) {
            source = (JList) c;
            indices = source.getSelectedIndices();
            int length = source.getSelectedIndices().length;
            DefaultListModel tranferableModel = (DefaultListModel) source.getModel();
            ImportedImage[] values = new ImportedImage[length];
            for (int i = 0; i < length; i++) {
                values[i] = (ImportedImage) tranferableModel.get(indices[i]);
            }
            if (values == null || values.length == 0) {
                return null;
            }
            ArrayList<ImportedImage> alist = new ArrayList<ImportedImage>(values.length);
            for (int i = 0; i < values.length; i++) {
                ImportedImage o = values[i];
                alist.add(o);
            }
            return new ArrayListTransferable(alist);
        }
        return null;
    }

    @Override
    public int getSourceActions(JComponent c) {
        return COPY_OR_MOVE;
    }

    public class ArrayListTransferable implements Transferable {

        ArrayList data;

        public ArrayListTransferable(ArrayList alist) {
            data = alist;
        }

        @Override
        public Object getTransferData(DataFlavor flavor)
                throws UnsupportedFlavorException {
            if (!isDataFlavorSupported(flavor)) {
                throw new UnsupportedFlavorException(flavor);
            }
            return data;
        }

        @Override
        public DataFlavor[] getTransferDataFlavors() {
            return new DataFlavor[]{localArrayListFlavor,
                        serialArrayListFlavor};
        }

        @Override
        public boolean isDataFlavorSupported(DataFlavor flavor) {
            if (localArrayListFlavor.equals(flavor)) {
                return true;
            }
            if (serialArrayListFlavor.equals(flavor)) {
                return true;
            }
            return false;
        }
    }
}