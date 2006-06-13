package edu.iastate.ato.gui.wizard ;

import java.util.List ;

import java.awt.BorderLayout ;
import java.awt.Font ;
import javax.swing.JLabel ;
import javax.swing.SwingConstants ;

import edu.iastate.utils.gui.wizard.WizardPanel ;

/**
 * Show welcome
 *
 * @author Jie Bao
 * @since 2005-08-21
 */
public class P01_Welcome extends WizardPanel
{
    private P02_Host next = null ;
    JLabel jLabel1 = new JLabel() ;
    BorderLayout borderLayout1 = new BorderLayout() ;

    SeverBuilder parent ;

    public P01_Welcome(SeverBuilder parent)
    {
        try
        {
            this.parent = parent ;
            this.next = new P02_Host(parent) ;
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
        return true ;
    }

    public boolean validateNext(List list)
    {
        return true ;
    }

    public WizardPanel next()
    {
        parent.p2_host = next ;
        return next ;
    }

    public boolean canFinish()
    {
        return false ;
    }

    public boolean validateFinish(List list)
    {
        return false ;
    }

    public void finish()
    {
    }

    private void jbInit() throws Exception
    {
        this.setLayout(borderLayout1) ;
        jLabel1.setFont(new java.awt.Font("Dialog", Font.PLAIN, 15)) ;
        jLabel1.setHorizontalAlignment(SwingConstants.CENTER) ;

        String info =
            "Welcome, I will guide you to create a new ontology server\n\n" ;

        jLabel1.setText(info) ;
        this.add(jLabel1, java.awt.BorderLayout.CENTER) ;
    }
}
