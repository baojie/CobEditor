package edu.iastate.anthill.indus.tree;

import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.Icon;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.KeyStroke;
import javax.swing.tree.TreePath;

public abstract class TreePopupMenuListener
    extends MouseAdapter
{
    public JPanel fatherPanel;
    public JPopupMenu popup = new JPopupMenu();
    public TypedTree tree;

    public TreePopupMenuListener()
    {
        super();
    }

    protected void addMenuItem(String text, Icon icon, ActionListener listener,
                               JPopupMenu menu, String shortKey)
    {
        JMenuItem jMenuItem = (icon == null) ?
            new JMenuItem(text) : new JMenuItem(text, icon);
        jMenuItem.addActionListener(listener);
        if (shortKey != null)
        {
            jMenuItem.setAccelerator(KeyStroke.getKeyStroke(shortKey));
        }
        menu.add(jMenuItem);
    }

    /**
     * Simplified addMenuItem
     * @param text String
     * @param listener ActionListener
     * @author Jie Bao
     * @since 2005-03-30
     */
    protected void addMenuItem(String text, ActionListener listener)
    {
        addMenuItem(text, null, listener, popup, null);
    }

    protected void addMenuItem(String text, Icon icon, ActionListener listener)
    {
        addMenuItem(text, icon, listener, popup, null);
    }

    public void mouseReleased(MouseEvent e)
    {
        showMenu(e);
    }

    public void showMenu(MouseEvent e)
    {
        TreePath selPath = tree.getPathForLocation(e.getX(), e.getY());
        if (e.isPopupTrigger() && selPath != null)
        {
            tree.setSelectionPath(selPath);

            final TypedNode selectedNode = (TypedNode)
                selPath.getLastPathComponent();

            boolean showpopup = false;
            popup = new JPopupMenu();

            buildContextMenu(selectedNode);

            // show the menu
            showpopup = true;
            if (showpopup)
            {
                popup.show(e.getComponent(), e.getX(), e.getY());
            }
        }
    }

    abstract protected void buildContextMenu(TypedNode selectedNode);

}
