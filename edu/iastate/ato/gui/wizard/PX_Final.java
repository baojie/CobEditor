package edu.iastate.ato.gui.wizard ;

import java.util.List ;

import java.awt.BorderLayout ;
import java.awt.Font ;
import java.awt.SystemColor ;
import javax.swing.BorderFactory ;
import javax.swing.JScrollPane ;
import javax.swing.JTextArea ;

import edu.iastate.utils.gui.wizard.WizardPanel ;

/**
 * @author Jie Bao
 * @since 2005-08-21
 */
public class PX_Final extends WizardPanel
{
    SeverBuilder parent ;
    public PX_Final(SeverBuilder parent)
    {
        try
        {
            this.parent = parent ;
            jbInit() ;
        }
        catch(Exception ex)
        {
            ex.printStackTrace() ;
        }
    }

    public void display()
    {
    }

    public boolean hasNext()
    {
        return false ;
    }

    public boolean validateNext(List list)
    {
        return false ;
    }

    public WizardPanel next()
    {
        return null ;
    }

    public boolean canFinish()
    {
        return true ;
    }

    public boolean validateFinish(List list)
    {
        return true ;
    }

    public void finish()
    {
        parent.wizardFinished(null) ;
    }

    private void jbInit() throws Exception
    {
        this.setLayout(borderLayout1) ;
        info.setBackground(SystemColor.menu) ;
        info.setFont(new java.awt.Font("Dialog", Font.PLAIN, 15)) ;
        info.setBorder(BorderFactory.createEtchedBorder()) ;
        info.setEditable(false) ;
        info.setText("Finished") ;
        this.add(jScrollPane1, java.awt.BorderLayout.CENTER) ;
        jScrollPane1.getViewport().add(info) ; }

    JTextArea info = new JTextArea() ;
    BorderLayout borderLayout1 = new BorderLayout() ;
    JScrollPane jScrollPane1 = new JScrollPane() ;
}
