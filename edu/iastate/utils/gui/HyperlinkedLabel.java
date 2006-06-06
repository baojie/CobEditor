package edu.iastate.utils.gui;

import java.awt.AWTEventMulticaster;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Label;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

/**
 * A HyperlinkedLabel object is a component for placing a hyperlink
 * into a container. A HyperlinkedLabel displays a single line of read-only text,
 * which is underlined to give the visual impression of a hyperlink.
 *
 * Clicking on the hyperlink will produce no result however. It is up
 * to the application developer to provide this functionality,
 * such as performing an action, displaying a web page, or activating a menu.
 *
 * <p>
 * Applications can catch hyperlink clicks by adding registering
 * an <code>ActionListener</code>, using the addActionListener method.
 *
 * @version 1.00 10/5/99
 * @author  David Reilly
 * @see         java.awt.event.ActionEvent
 * @see         java.awt.event.ActionListener
 */
public class HyperlinkedLabel
    extends Label implements MouseListener
{
    // URL
    private String URL = null;
    // Color for the hyperlink (not the text)
    private Color hyperlinkColor;

    private Color originalTextColor;
    transient boolean clicked = false;
    transient ActionListener actionListener = null;

    /**
     * Creates a hyperlinked label with no specific hyperlink
     *
     * @param label	Text of label
     */
    public HyperlinkedLabel(String label)
    {
        this(label, null);
    }

    /**
     * Creates a hyperlinked label hyperlinked to the specified URL.
     *
     * @param label	Text of label
     * @param URL		Universal Resource Locator (URL)
     */
    public HyperlinkedLabel(String label, String URL)
    {
        this(label, URL, Color.blue);
    }

    /**
     * Creates a hyperlinked label hyperlinked to the specified URL,
     * in the specific color.
     *
     * @param label	Text of label
     * @param URL		Universal Resource Locator (URL)
     * @param color    Color of hyperlink
     */
    public HyperlinkedLabel(String label, String URL, Color color)
    {
        super(label);

        setURL(URL);
        setHyperlinkColor(color);

        addMouseListener(this);
    }

    /**
     * Sets the URL destination for this hyperlink.
     *
     * @param URL		Universal Resource Locator (URL)
     */
    public void setURL(String URL)
    {
        this.URL = URL;
    }

    /**
     * Sets the color for this hyperlink.
     *
     * @param color	Color of hyperlink
     */
    public void setHyperlinkColor(Color color)
    {
        this.hyperlinkColor = color;
    }

    /**
     * Returns the color for this hyperlink.
     *
     * @return Color of hyperlink
     */
    public Color getHyperlinkColor()
    {
        return hyperlinkColor;
    }

    /**
     * Returns the URL for this hyperlink.
     *
     * @return Universal Resource Locator (URL) as a String
     */
    public String getURL()
    {
        return URL;
    }

    /**
     * Paints the hyperlink
     *
     * @param g    Graphics object for painting
     */
    public void paint(Graphics g)
    {
        // Call label's paint method
        super.paint(g);

        // Check that a hyperlink was specified
        if (URL == null)
        {
            return;
        }

        // Set color for hyperlink
        g.setColor(hyperlinkColor);

        // Get width of hyperlink label component
        Dimension d = getSize();
        int width = d.width;
        int height = d.height;

        // Get width of label text
        FontMetrics metrics = getFontMetrics(getFont());
        int strWidth = metrics.stringWidth(getText());

        int offset = width - strWidth;

        int align = getAlignment();

        switch (align)
        {
            case LEFT:

                // Safety factor of 2
                g.drawLine(2, height - 5, 2 + strWidth, height - 5);
                break;
            case CENTER:

                // Safety factor of 1
                g.drawLine(offset / 2 + 1, height - 5, width - offset / 2 - 1,
                           height - 5);
                break;
            case RIGHT:

                // Safety factor of 2
                g.drawLine(offset - 2, height - 5, width, height - 5);
                break;

        }
    }

    /**
     * Adds the specified action listener to receive action events from
     * this hyperlink.
     *
     * @param listener	ActionListener to add to event queue
     */
    public synchronized void addActionListener(ActionListener listener)
    {
        actionListener = AWTEventMulticaster.add(actionListener, listener);
    }

    /**
     * Removes the specified action listener from the list of action listeners
     * associated with this hyperlink.
     *
     * @param listener	ActionListener to add to event queue
     */
    public synchronized void removeActionListener(ActionListener listener)
    {
        actionListener = AWTEventMulticaster.remove(actionListener, listener);
    }

    public void mouseClicked(MouseEvent e)
    {
        if (actionListener != null)
        {
            ActionEvent actionE = new ActionEvent(this,
                                                  ActionEvent.ACTION_PERFORMED,
                                                  "Clicked");
            actionListener.actionPerformed(actionE);
        }
    }

    /**
     * Event handler for when the cursor enters the hyperlink
     * @param MouseEvent Event
     */
    public void mouseEntered(MouseEvent e)
    {
        setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    /**
     * Event handler for when the cursor leaves the hyperlink
     * @param MouseEvent Event
     */
    public void mouseExited(MouseEvent e)
    {
        setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
    }

    public void mousePressed(MouseEvent e)
    {
        clicked = true;
        originalTextColor = getForeground();
        setForeground(hyperlinkColor);
        repaint();
    }

    public void mouseReleased(MouseEvent e)
    {
        clicked = false;
        setForeground(originalTextColor);
        repaint();
    }

    /**
     * Returns the preferred size of the hyperlink.
     * @return Preferred dimensions
     */
    public Dimension getPreferredSize()
    {
        // Shave a little off original width
        FontMetrics metrics = getFontMetrics(getFont());
        int strWidth = metrics.stringWidth(getText());

        Dimension original = super.getPreferredSize();
        return new Dimension(strWidth + 2, original.height);
    }

    // This is just for testing ...
    /*  public static void main(String args[]) {
        Frame f = new Frame();
        HyperlinkedLabel label = new HyperlinkedLabel("Test",
     "http://www.davidreilly.com/");

        // Creating two dummy action listeners.
        ActionListener listener1 = new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            System.out.println("action listener 1 received event: " + e);
          }
        };

        ActionListener listener2 = new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            System.out.println("action listener 2 received event: " + e);
          }
        };

        label.addActionListener(listener1);
        label.addActionListener(listener2);
        label.setHyperlinkColor(new Color(200, 40, 0));
        f.setLayout(new FlowLayout());
        f.add(new HyperlinkedLabel("hello there", "abc"));
        f.add(label);
        label.setAlignment(Label.CENTER);
        f.add(new Button("Ok"));
        Label tmp = new HyperlinkedLabel("hi", "bk");
        tmp.setForeground(Color.orange);
        tmp.setAlignment(Label.CENTER);
        f.add(tmp);
        f.pack();
        f.setVisible(true);
      }
     */
}
