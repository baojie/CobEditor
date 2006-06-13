package edu.iastate.ato.gui;

/*
 * Copyright (C) 2001-2003 Colin Bell
 * colbell@users.sourceforge.net
 * Jie Bao modified baojie@gmail.com 2003-2005
 */

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.Timer;

import edu.iastate.utils.Utility;
import edu.iastate.utils.gui.GUIUtils;
import edu.iastate.utils.gui.LabelledItemPanel;

/**
 * About box dialog.
 *
 * @author <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
public class AboutBoxDialog
    extends JDialog

{
    static String info;

    /** The tabbed panel. */
    private JTabbedPane _tabPnl;

    /** System panel. */
    private SystemPanel _systemPnl;

    /** Close button for dialog. */
    private final JButton _closeBtn = new JButton("Close");

    public AboutBoxDialog(String info, String title)
    {
        this.info = info;
        createGUI();
        setTitle(title);
    }

    public AboutBoxDialog(JFrame frame, String info, String title)
    {
        super(frame);
        this.info = info;
        createGUI();
        setTitle(title);
    }

    /**
     * Show the About Box.
     *
     */
    public void showAboutBox() throws IllegalArgumentException
    {
        setVisible(true);
    }

    private void createGUI()
    {
        final JPanel contentPane = new JPanel(new BorderLayout());
        setContentPane(contentPane);
        contentPane.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));

        long start = 0;

        _tabPnl = new JTabbedPane();

        _tabPnl.add("About", new AboutPanel());

        _systemPnl = new SystemPanel();
        _tabPnl.add("System",
                    _systemPnl);

        contentPane.add(_tabPnl, BorderLayout.CENTER);
        contentPane.add(createButtonBar(), BorderLayout.SOUTH);

        getRootPane().setDefaultButton(_closeBtn);

        addWindowListener(new WindowAdapter()
        {
            public void windowActivated(WindowEvent evt)
            {
                _systemPnl._memoryPnl.startTimer();
            }

            public void windowDeactivated(WindowEvent evt)
            {
                _systemPnl._memoryPnl.stopTimer();
            }
        });

        setResizable(true);
        setSize(400, 400);
        pack();
        GUIUtils.centerWithinParent(this);

    }

    private JPanel createButtonBar()
    {
        _closeBtn.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent evt)
            {
                setVisible(false);
            }
        });

        JPanel p = new JPanel();
        p.add(_closeBtn);

        return p;
    }

    private static final class AboutPanel
        extends JPanel
    {
        AboutPanel()
        {
            super();
            setLayout(new BorderLayout());
//            Icon image = new ImageIcon("resource/bcb.gif");
//            System.out.println( "icon width = " + image.getIconWidth() +
//                                " height = " + image.getIconHeight() ) ;
            //JLabel label = new JLabel();
//            label.setIcon(image);
            //add(label, BorderLayout.CENTER);

            setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
            JButton infoArea = new JButton(info);
            infoArea.setBackground(Color.yellow);
            infoArea.setEnabled(false);

            this.setBackground(Color.white);
            add(infoArea, BorderLayout.CENTER);

        }
    }

    private static final class SystemPanel
        extends JPanel
    {
        private MemoryPanel _memoryPnl;

        SystemPanel()
        {
            super();
            setLayout(new BorderLayout());

            _memoryPnl = new MemoryPanel();
            add(_memoryPnl, BorderLayout.CENTER);

            JButton gcBtn = new JButton(new String(
                "Garbge Collect"));
            gcBtn.addActionListener(new ActionListener()
            {
                public void actionPerformed(ActionEvent evt)
                {
                    System.gc();
                }
            });
            JPanel buttonPanel = new JPanel();
            buttonPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0,
                0));
            buttonPanel.add(gcBtn);
            add(buttonPanel, BorderLayout.SOUTH);
        }
    }

    private static class MemoryPanel
        extends LabelledItemPanel implements ActionListener
    {
        private final JTextField _totalMemoryLbl = new JTextField();
        private final JTextField _usedMemoryLbl = new JTextField();
        private final JTextField _freeMemoryLbl = new JTextField();
        private Timer _timer;

        MemoryPanel()
        {
            setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

            addItem("Heap Size", _totalMemoryLbl);
            addItem("Used Heap", _usedMemoryLbl);
            addItem("Free Heap", _freeMemoryLbl);

            _totalMemoryLbl.setEditable(false);
            _usedMemoryLbl.setEditable(false);
            _freeMemoryLbl.setEditable(false);

        }

        public void removeNotify()
        {
            super.removeNotify();
            stopTimer();
        }

        /**
         * Update component with the current memory status.
         *
         * @param	evt		The current event.
         */
        public void actionPerformed(ActionEvent evt)
        {
            updateMemoryStatus();
        }

        synchronized void startTimer()
        {
            if (_timer == null)
            {
                //_thread = new Thread(new MemoryTimer());
                //_thread.start();
                updateMemoryStatus();
                _timer = new Timer(2000, this);
                _timer.start();
            }
        }

        synchronized void stopTimer()
        {
            if (_timer != null)
            {
                _timer.stop();
                _timer = null;
            }
        }

        private void updateMemoryStatus()
        {
            Runtime rt = Runtime.getRuntime();
            final long totalMemory = rt.totalMemory();
            final long freeMemory = rt.freeMemory();
            final long usedMemory = totalMemory - freeMemory;
            _totalMemoryLbl.setText(Utility.formatSize(totalMemory, 2));
            _usedMemoryLbl.setText(Utility.formatSize(usedMemory, 2));
            _freeMemoryLbl.setText(Utility.formatSize(freeMemory, 2));
        }
    }

    // for test purpose
    public static void main(String[] args)
    {
        AboutBoxDialog dlg = new AboutBoxDialog("<html>Test <b>Dlg<b>,\n"+
                                                "<li>You can use HTML</li>"+
                                                "<li>Customize info and title</li>"+
                                                "</html>","Test");
        dlg.showAboutBox();
    }
}
