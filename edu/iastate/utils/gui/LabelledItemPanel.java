package edu.iastate.utils.gui;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

/**
 * This class provides a panel for laying out labelled elements neatly with
 * all the labels and elements aligned down the screen.
 *
 * @author David Fraser
 * @author Michael Harris
 */
public class LabelledItemPanel extends JPanel
{
    /** The row to add the next labelled item to */
    private int myNextItemRow = 0;

    public int itemInterval = 10; // 2005-07-23

    /**
     * This method is the default constructor.
     */
    public LabelledItemPanel()
    {
        init();
    }

    public LabelledItemPanel(int itermInterval)
    {
        this.itemInterval = itermInterval;
        init();
    }

    /**
     * This method initialises the panel and layout manager.
     */
    private void init()
    {
        setLayout(new GridBagLayout());
        myNextItemRow = 0;

        // Create a blank label to use as a vertical fill so that the
        // label/item pairs are aligned to the top of the panel and are not
        // grouped in the centre if the parent component is taller than
        // the preferred size of the panel.

        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridx = 0;
        constraints.gridy = 99;
        constraints.insets = new Insets(itemInterval, 0, 0, 0);
        constraints.weighty = 1.0;
        constraints.fill = GridBagConstraints.VERTICAL;

        JLabel verticalFillLabel = new JLabel();

        add(verticalFillLabel, constraints);
    }

    /**
     * This method adds a labelled item to the panel. The item is added to
     * the row below the last item added.
     *
     * @param labelText The label text for the item.
     * @param item      The item to be added.
     */
    public void addItem(String labelText, JComponent item)
    {
        // Create the label and its constraints

        JLabel label = new JLabel(labelText);
        addItem(label, item);
    }

    // 2005-08-22
    public void addItem(JComponent labelItem, JComponent item)
    {
        GridBagConstraints labelConstraints = new GridBagConstraints();

        labelConstraints.gridx = 0;
        labelConstraints.gridy = myNextItemRow;
        labelConstraints.insets = new Insets(itemInterval, itemInterval, 0, 0);
        labelConstraints.anchor = GridBagConstraints.NORTHEAST;
        labelConstraints.fill = GridBagConstraints.NONE;

        add(labelItem, labelConstraints);

        // Add the component with its constraints

        GridBagConstraints itemConstraints = new GridBagConstraints();

        itemConstraints.gridx = 1;
        itemConstraints.gridy = myNextItemRow;
        itemConstraints.insets = new Insets(itemInterval, itemInterval, 0,
                                            itemInterval);
        itemConstraints.weightx = 1.0;
        itemConstraints.anchor = GridBagConstraints.WEST;
        itemConstraints.fill = GridBagConstraints.HORIZONTAL;

        add(item, itemConstraints);

        myNextItemRow++;
    }

    // for test purpose
    public static void main(String[] args)
    {
        JFrame frame = new JFrame();
        LabelledItemPanel myContentPane = new LabelledItemPanel();
        myContentPane.setBorder(BorderFactory.createEtchedBorder());
        myContentPane.addItem("Customer Code", new JTextField());
        myContentPane.addItem("Name", new JTextField());
        myContentPane.addItem("Address",
                              new JScrollPane(new JTextArea(3, 20),
                                              JScrollPane.
                                              VERTICAL_SCROLLBAR_ALWAYS,
                                              JScrollPane.
                                              HORIZONTAL_SCROLLBAR_NEVER));
        frame.setContentPane(myContentPane);
        frame.pack();
        frame.setVisible(true);
    }

    /**
     * clear
     * @author Jie Bao
     * @since2005-08-22
     */
    public void clear()
    {
        this.removeAll();
        init();
    }

}
