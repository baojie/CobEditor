package edu.iastate.utils.gui;

/*
 Jie Bao
 2005-07-22

 Revised from

 Java Swing, 2nd Edition
 By Marc Loy, Robert Eckstein, Dave Wood, James Elliott, Brian Cole
 ISBN: 0-596-00408-7
 Publisher: O'Reilly
 */

// SampleDesktop.java
//Another example that shows how to do a few interesting things using
//JInternalFrames, JDesktopPane, and DesktopManager.
//

import java.beans.PropertyChangeEvent;
import java.beans.PropertyVetoException;
import java.beans.VetoableChangeListener;

import java.awt.Dimension;
import javax.swing.DefaultDesktopManager;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDesktopPane;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class HolderPanel
    extends JDesktopPane
{
    private JDesktopPane desk = this;
    private IconPolice iconPolice = new IconPolice();

    public HolderPanel()
    {
        // Install our custom desktop manager.
        desk.setDesktopManager(new SampleDesktopMgr());
    }

    public JInternalFrame addPane(JPanel p, String title)
    {
        //System.out.println("  << " + desk.getSize());
        Dimension ss = desk.getSize();
        // Create an internal frame
        boolean resizable = true;
        boolean closeable = false;
        boolean maximizable = true;
        boolean iconifiable = true;

        JInternalFrame iframe = new JInternalFrame(title, resizable, closeable,
            maximizable, iconifiable);
        iframe.addVetoableChangeListener(iconPolice);

        // Set an initial size
        int width = 200;
        int height = 50;
        iframe.setSize(width, height);

        // By default, internal frames are not visible; make it visible
        iframe.setVisible(true);

        // Add components to internal frame...
        iframe.setContentPane(p);
        desk.add(iframe, new Integer(1));

        desk.setSize(ss);
        //System.out.println(" >> " + desk.getSize());
        return iframe;
    }

    // A simple vetoable change listener that insists that there is always at
    // least one noniconified frame (just as an example of the vetoable
    // properties).
    class IconPolice
        implements VetoableChangeListener
    {
        public void vetoableChange(PropertyChangeEvent ev) throws
            PropertyVetoException
        {
            String name = ev.getPropertyName();
            if (name.equals(JInternalFrame.IS_ICON_PROPERTY)
                && (ev.getNewValue() == Boolean.TRUE))
            {
                JInternalFrame[] frames = desk.getAllFrames();
                int count = frames.length;
                int nonicons = 0; // how many are not icons?
                for (int i = 0; i < count; i++)
                {
                    if (!frames[i].isIcon())
                    {
                        nonicons++;
                    }
                }
                if (nonicons <= 1)
                {
                    throw new PropertyVetoException("Invalid Iconification!",
                        ev);
                }
            }
        }
    }

    int getPanelCount(boolean normalOnly)
    {
        JInternalFrame[] allframes = desk.getAllFrames();
        int count = 0;//allframes.length;
        if (normalOnly)
        {
            for (int i = 0; i < allframes.length; i++)
            {
                JInternalFrame f = allframes[i];
                if (!f.isIcon())
                {
                    count++;
                }
            }
        }
        return count;
    }

    private void arrange(int rows, int cols, int count)
    {
        // Define some initial values for size & location.
        Dimension size = desk.getSize();

        int w = size.width / cols;
        int h = size.height / rows;
        int x = 0;
        int y = 0;

// Iterate over the frames, deiconifying any iconified frames and then
// relocating & resizing each.
        JInternalFrame[] allframes = desk.getAllFrames();
        for (int i = 0; i < rows; i++)
        {
            for (int j = 0; j < cols && ( (i * cols) + j < count); j++)
            {
                JInternalFrame f = allframes[ (i * cols) + j];

                if (f.isIcon())
                {
                    continue;
                }

                desk.getDesktopManager().resizeFrame(f, x, y, w, h);
                x += w;
            }
            y += h; // start the next row
            x = 0;
        }

    }

    public void tile()
    {
        int count = getPanelCount(true);
        if (count == 0)
        {
            return;
        }

        // Determine the necessary grid size
        int sqrt = (int) Math.sqrt(count);
        int rows = sqrt;
        int cols = sqrt;
        if (rows * cols < count)
        {
            cols++;
            if (rows * cols < count)
            {
                rows++;
            }
        }

        arrange(rows, cols, count);
    }

    public void line()
    {
        // How many frames do we have?
        int count = getPanelCount(true);
        if (count == 0)
        {
            return;
        }

        // Determine the necessary grid size
        int rows = count;
        int cols = 1;

        //Debug.trace(count);

        arrange(rows, cols, count);
    }

    // deiconify all minimized frames
    public void showAll()
    {
        JInternalFrame[] allframes = desk.getAllFrames();
        int count = allframes.length;
        for (int i = 0; i < count; i++)
        {
            JInternalFrame f = allframes[i];
            if ( !f.isClosed() && f.isIcon())
            {
                try
                {
                    f.setIcon(false);
                }
                catch (PropertyVetoException ex)
                {
                }
            }
        }
    }

    // A simple test program.
    public static void main(String[] args)
    {
        HolderPanel td = new HolderPanel();

        // Display the desktop in a top-level frame
        JFrame frame = new JFrame();
        frame.setVisible(true);

        frame.setContentPane(td);
        frame.setSize(300, 200);

        td.setSize(GUIUtils.getClientRegionSize(frame));

        JPanel p1 = new JPanel(), p2 = new JPanel();
        p1.add(new JLabel("111"));
        p2.add(new JButton("222"));

        td.addPane(p1, "1");
        td.addPane(p2, "2");
        td.addPane(new JPanel(), "3");

        td.line();
        frame.validate();

    }
}

//SampleDesktopMgr.java
//A DesktopManager that keeps its frames inside the desktop.

class SampleDesktopMgr
    extends DefaultDesktopManager
{

    // This is called anytime a frame is moved. This
    // implementation keeps the frame from leaving the desktop.
    public void dragFrame(JComponent f, int x, int y)
    {
        if (f instanceof JInternalFrame)
        { // Deal only w/internal frames
            JInternalFrame frame = (JInternalFrame) f;
            JDesktopPane desk = frame.getDesktopPane();
            Dimension d = desk.getSize();

            // Nothing all that fancy below, just figuring out how to adjust
            // to keep the frame on the desktop.
            if (x < 0)
            { // too far left?
                x = 0; // flush against the left side
            }
            else
            {
                if (x + frame.getWidth() > d.width)
                { // too far right?
                    x = d.width - frame.getWidth(); // flush against right side
                }
            }
            if (y < 0)
            { // too high?
                y = 0; // flush against the top
            }
            else
            {
                if (y + frame.getHeight() > d.height)
                { // too low?
                    y = d.height - frame.getHeight(); // flush against the
                    // bottom
                }
            }
        }

        // Pass along the (possibly cropped) values to the normal drag handler.
        super.dragFrame(f, x, y);
    }
}
