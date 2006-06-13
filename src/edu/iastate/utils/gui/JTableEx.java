package edu.iastate.utils.gui;

import java.util.Collections;
import java.util.Vector;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;

public class JTableEx
    extends JTable
{
    public JTableEx()
    {
        super();
        jbInit();
    }

    public JTableEx(TableModel dm)
    {
        super(dm);
        jbInit();
    }

    private JTableHeader header;
    DefaultTableModel tableModel;

    public void jbInit()
    {
        header = getTableHeader();
        header.addMouseListener(new ColumnHeaderListener());
        tableModel = (DefaultTableModel)this.getModel();
        sortAscending = new boolean[tableModel.getColumnCount()];
        for (int i = 0; i < tableModel.getColumnCount(); i++)
        {

            sortAscending[i] = true;
        }

    }

    public class ColumnHeaderListener
        extends MouseAdapter
    {
        public void mouseClicked(MouseEvent evt)
        {
            JTable table = ( (JTableHeader) evt.getSource()).getTable();
            TableColumnModel colModel = table.getColumnModel();

            // The index of the column whose header was clicked
            int vColIndex = colModel.getColumnIndexAtX(evt.getX());
            int mColIndex = table.convertColumnIndexToModel(vColIndex);

            // Return if not clicked on any column header
            if (vColIndex == -1)
            {
                return;
            }
            sortAllRowsBy(mColIndex, sortAscending[mColIndex]);
            sortAscending[mColIndex] = !sortAscending[mColIndex];
        }
    }

    public void removeAll()
    {
        int rows = tableModel.getRowCount();
        for (int i = 0; i < rows; i++)
        {
            tableModel.removeRow(0);
        }
    }

    public void addRow(Object strList[])
    {
//        Debug.trace( "string lenth" + strList.length ) ;
        setRowSelectionAllowed(true);
        tableModel.addRow(strList);
    }

    public void sortAllRowsBy(int colIndex,
                              boolean ascending)
    {
        Vector data = tableModel.getDataVector();
        TableColumnSorter sorter = new TableColumnSorter(colIndex, ascending);
        Collections.sort(data, sorter);
        tableModel.fireTableStructureChanged();
    }

    protected boolean[] sortAscending;
}
