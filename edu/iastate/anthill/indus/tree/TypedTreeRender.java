package edu.iastate.anthill.indus.tree;

import java.util.HashMap;
import java.util.Map;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JTree;
import javax.swing.tree.TreeCellRenderer;

import edu.iastate.ato.shared.IndusConstants;

public class TypedTreeRender extends JLabel implements TreeCellRenderer
{
    public TypedTreeRender()
    {
        icons.put(TypedNode.ROOT + "", IndusConstants.iconRoot);
        icons.put(TypedNode.PACKAGE + "", IndusConstants.iconPackage);

        icons.put(TypedNode.CLASS + "", IndusConstants.iconClass);
        icons.put(TypedNode.PROPERTY + "", IndusConstants.iconProperty);
        icons.put(TypedNode.INSTANCE + "", IndusConstants.iconInstance);

        icons.put(TypedNode.ALL_CLASSES + "", IndusConstants.iconAllClasses);
        icons.put(TypedNode.ALL_PROPERTIES + "",
                  IndusConstants.iconAllProperties);
        icons.put(TypedNode.ALL_INSTANCES + "",
                  IndusConstants.iconAllInstances);

        icons.put(TypedNode.ATTRIBUTE + "", IndusConstants.iconSchema);
        icons.put(TypedNode.AVH + "", IndusConstants.iconAVHValue);
        icons.put(TypedNode.DB + "", IndusConstants.iconDB);
    }

    // static protected Font defaultFont;
    static public Map<String, Icon> icons = new HashMap();

    /** Color to use for the background when selected. */
    static protected final Color SelectedBackgroundColor = Color.yellow;

    /** Whether or not the item that was last configured is selected. */
    protected boolean selected;

    /**
     * This is messaged from JTree whenever it needs to get the size
     * of the component or it wants to draw it.
     * This attempts to set the font based on value, which will be
     * a TreeNode.
     */

    public Component getTreeCellRendererComponent(JTree tree,
                                                  Object value,
                                                  boolean selected,
                                                  boolean expanded,
                                                  boolean leaf, int row,
                                                  boolean hasFocus)
    {
        String stringValue = tree.convertValueToText(value, selected,
            expanded, leaf, row, hasFocus);
        // Set the text.
        setText(stringValue);

// Tooltips used by the tree.
        setToolTipText(stringValue);

// Set the image.
        // 2004-10-01 Jie Bao debug: should may sure the type of node
        if (value instanceof TypedNode)
        {
            TypedNode theNode = (TypedNode) value;

            // 2005-08-23: node may has it's own icon definition
            Icon icon = theNode.getIcon();
            if (icon != null)
            {
                setIcon(icon);
            }
            else
            {
                setIcon( (Icon) icons.get(theNode.getType() + ""));
            }

            Color color = theNode.getColor();
            if (color != null)
            {
                setForeground( (Color) color);
            }
            else
            {
                setForeground(Color.black);
            }
        }

        // Update the selected flag for the next paint.
//  }
        this.selected = selected;

        return this;
    }

    /**
      /**
      * paint is subclassed to draw the background correctly.  JLabel
      * currently does not allow backgrounds other than white, and it
      * will also fill behind the icon.  Something that isn't desirable.
      */
     public void paint(Graphics g)
     {
         Color bColor;
         Icon currentI = getIcon();

         if (selected)
         {
             bColor = SelectedBackgroundColor;
         }
         else if (getParent() != null)
         {

             /* Pick background color up from parent (which will come from
                the JTree we're contained in). */
             bColor = getParent().getBackground();
         }
         else
         {
             bColor = getBackground();
         }
         g.setColor(bColor);
         if (currentI != null && getText() != null)
         {
             int offset = (currentI.getIconWidth() + getIconTextGap());

             if (getComponentOrientation().isLeftToRight())
             {
                 g.fillRect(offset, 0, getWidth() - 1 - offset,
                            getHeight() - 1);
             }
             else
             {
                 g.fillRect(0, 0, getWidth() - 1 - offset,
                            getHeight() - 1);
             }
         }
         else
         {
             g.fillRect(0, 0, getWidth() - 1, getHeight() - 1);
         }
         super.paint(g);
     }

}
