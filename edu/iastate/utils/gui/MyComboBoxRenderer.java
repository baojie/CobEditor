package edu.iastate.utils.gui;

import java.awt.Component;
import javax.swing.JComboBox;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

/**
 * @author Jie Bao
 * @since 1.0
 */
public class MyComboBoxRenderer
    extends JComboBox implements TableCellRenderer
{
    public MyComboBoxRenderer(Object[] items)
    {
        super(items);
    }

    public Component getTableCellRendererComponent(JTable table,
        Object value,
        boolean isSelected, boolean hasFocus, int row, int column)
    {
        if (isSelected)
        {
            setForeground(table.getSelectionForeground());
            super.setBackground(table.getSelectionBackground());
        }
        else
        {
            setForeground(table.getForeground());
            setBackground(table.getBackground());
        }

        // Select the current value
        setSelectedItem(value);
        this.repaint();
        return this;
    }
}
