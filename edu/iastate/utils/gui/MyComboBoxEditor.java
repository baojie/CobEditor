package edu.iastate.utils.gui;

import javax.swing.DefaultCellEditor;
import javax.swing.JComboBox;

/**
 * @author Jie Bao
 * @since 1.0
 */
public class MyComboBoxEditor
    extends DefaultCellEditor
{
    public MyComboBoxEditor(Object[] items)
    {
        super(new JComboBox(items));
    }

    public MyComboBoxEditor(JComboBox box)
    {
        super(box);
    }

}
