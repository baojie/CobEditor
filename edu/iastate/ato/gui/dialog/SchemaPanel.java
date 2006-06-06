package edu.iastate.ato.gui.dialog ;

import java.sql.Connection ;
import java.util.Set ;
import java.util.TreeSet ;
import java.util.Vector ;

import java.awt.BorderLayout ;
import java.awt.GridLayout ;
import java.awt.event.ActionEvent ;
import java.awt.event.ActionListener ;
import javax.swing.DefaultListModel ;
import javax.swing.JButton ;
import javax.swing.JLabel ;
import javax.swing.JList ;
import javax.swing.JPanel ;
import javax.swing.JScrollPane ;
import javax.swing.JTabbedPane ;
import javax.swing.JTextArea ;
import javax.swing.event.ListSelectionEvent ;
import javax.swing.event.ListSelectionListener ;

import edu.iastate.ato.gui.MOEditor ;
import edu.iastate.ato.po.OntologySchema ;
import edu.iastate.ato.po.naming.BasicNamingPolicy ;
import edu.iastate.ato.po.naming.NamingPolicy ;
import edu.iastate.ato.po.naming.NamingPolicyFactory ;

import edu.iastate.utils.gui.LabelledItemPanel ;
import edu.iastate.utils.*;

/**
 * <p>@author Jie Bao</p>
 * <p>@since </p>
 */
public class SchemaPanel extends JPanel
{
    Connection db ;

    JTextArea textRelation = new JTextArea() ;
    JTextArea textProperty = new JTextArea() ;
    JTextArea textPolicyDetails = new JTextArea() ;

    JButton btnSave = new JButton() ;

    DefaultListModel lstNamingPolicyModel = new DefaultListModel() ;
    JList lstNamingPolicy = new JList(lstNamingPolicyModel) ;

    JPanel buttonPane = new JPanel() ;

    JTabbedPane contentPane = new JTabbedPane() ;
    JPanel relationPane = new JPanel() ;
    JPanel propertyPane = new JPanel() ;
    JPanel namingPane = new JPanel() ;
    LabelledItemPanel otherPane = new LabelledItemPanel() ;

    public SchemaPanel(Connection db)
    {
        try
        {
            this.db = db ;
            jbInit() ;
        }
        catch(Exception exception)
        {
            exception.printStackTrace() ;
        }
    }

    private void jbInit() throws Exception
    {
        JScrollPane jScrollPane1 = new JScrollPane() ;
        JScrollPane jScrollPane2 = new JScrollPane() ;
        JScrollPane jScrollPane3 = new JScrollPane() ;
        JScrollPane jScrollPane4 = new JScrollPane() ;

        JLabel jLabel1 = new JLabel() ;
        JLabel jLabel2 = new JLabel() ;
        JLabel jLabel3 = new JLabel() ;
        JPanel jPanel7 = new JPanel() ;

        this.setLayout(new BorderLayout()) ;
        jLabel1.setToolTipText("") ;
        jLabel1.setText("Possible relations in the hierarchy (one per line)") ;
        jLabel2.setText("Possible properties for terms (one per line)") ;
        relationPane.setLayout(new BorderLayout()) ;
        textRelation.setText("") ;
        propertyPane.setLayout(new BorderLayout()) ;
        textProperty.setText("") ;
        btnSave.setText("Save") ;
        btnSave.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                onSave(e) ;
            }
        }) ;
        namingPane.setLayout(new BorderLayout()) ;
        jLabel3.setText("Choose Naming Policy") ;
        jPanel7.setLayout(new GridLayout()) ;
        textPolicyDetails.setEditable(false) ;
        textPolicyDetails.setText("") ;
        btnImportOBO.setText("Import OBO Properties") ;
        btnImportOBO.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent actionEvent)
            {
                onImportOBO(actionEvent) ;
            }
        }) ;
        relationPane.add(jLabel1, BorderLayout.NORTH) ;
        relationPane.add(jScrollPane1, BorderLayout.CENTER) ;
        jScrollPane1.getViewport().add(textRelation) ;

        buttonPane.add(btnSave) ;
        buttonPane.add(btnImportOBO) ;

        propertyPane.add(jScrollPane2, BorderLayout.CENTER) ;
        propertyPane.add(jLabel2, BorderLayout.NORTH) ;

        jScrollPane2.getViewport().add(textProperty) ;

        this.add(buttonPane, java.awt.BorderLayout.SOUTH) ;
        namingPane.add(jLabel3, BorderLayout.NORTH) ;
        namingPane.add(jPanel7, BorderLayout.CENTER) ;
        jPanel7.add(jScrollPane3) ;
        jPanel7.add(jScrollPane4) ;
        jScrollPane4.getViewport().add(textPolicyDetails) ;
        jScrollPane3.getViewport().add(lstNamingPolicy) ;
        this.add(contentPane, BorderLayout.CENTER) ;
        contentPane.add(namingPane, "Name Policy") ;
        contentPane.add(propertyPane, "Term Properties") ;
        contentPane.add(relationPane, "Relations") ;
        contentPane.add(otherPane, "Other") ;

        //prefix
        otherPane.addItem("Ontology prefix", prefix) ;

        // naming policy
        for(NamingPolicy policy : NamingPolicyFactory.getAllNamingPolicy(db))
        {
            lstNamingPolicyModel.addElement(policy) ;
        }

        //lstNamingPolicy.setSelectedIndex(0);
        lstNamingPolicy.addListSelectionListener(new MyListSelectionListener()) ;

        onLoad(null) ;
    }

    JTextArea prefix = new JTextArea() ;
    JButton btnImportOBO = new JButton() ;

    class MyListSelectionListener
        implements ListSelectionListener
    {
        public void valueChanged(ListSelectionEvent evt)
        {
            if(!evt.getValueIsAdjusting())
            {
                JList list = (JList)evt.getSource() ;
                NamingPolicy policy = (NamingPolicy)list.getSelectedValue() ;
                textPolicyDetails.setText(policy.getExplanation()) ;
            }
        }
    }

    public void onLoad(ActionEvent e)
    {
        // prefix
        prefix.setText(OntologySchema.getPrefix(db)) ;

        // partial order relations
        Vector<String> po = OntologySchema.getPredicate(
            db, OntologySchema.PARTIAL_ORDER) ;
        String str = "" ;
        for(String po_str : po)
        {
            str += po_str + "\n" ;
        }
        textRelation.setText(str) ;

        // term properties
        Vector<String> tp = OntologySchema.getPredicate(
            db, OntologySchema.TERM_PROPERTY) ;
        str = "" ;
        for(String tp_str : tp)
        {
            str += tp_str + "\n" ;
        }
        textProperty.setText(str) ;

        // naming policy
        String policy = OntologySchema.getNamingPolicy(db) ;
        System.out.println(policy) ;
        if(policy == null)
        {
            policy = BasicNamingPolicy.policyName ;
        }

        // select it in the list
        int size = lstNamingPolicyModel.getSize() ;
        System.out.println(size) ;
        for(int i = 0 ; i < size ; i++)
        {
            NamingPolicy item = (NamingPolicy)lstNamingPolicyModel.
                getElementAt(i) ;
            System.out.println(item) ;
            if(item.getPolicyName().trim().equals(policy))
            {
                lstNamingPolicy.setSelectedIndex(i) ;
                break ;
            }
        }
    }

    public void onSave(ActionEvent e)
    {
        if (!MOEditor.theInstance.user.isAdmin())
        {
            Debug.trace("You should be admin to change the schema");
            return;
        }

        // prefix
        String pre = prefix.getText() ;
        if(pre == null || pre.trim().length() == 0)
        {
            pre = "" ;
        }
        OntologySchema.setPrefix(db, pre) ;

        // partial-order relations
        OntologySchema.clear(db, OntologySchema.PARTIAL_ORDER) ;
        String[] po = textRelation.getText().split("\n") ;
        for(int i = 0 ; i < po.length ; i++)
        {
            if(po[i].trim().length() > 0)
            {
                OntologySchema.addPredicate(db, OntologySchema.PARTIAL_ORDER,
                    po[i]) ;
            }
        }

        // term properties
        OntologySchema.clear(db, OntologySchema.TERM_PROPERTY) ;
        String[] tp = textProperty.getText().split("\n") ;
        for(int i = 0 ; i < tp.length ; i++)
        {
            if(tp[i].trim().length() > 0)
            {
                OntologySchema.addPredicate(db, OntologySchema.TERM_PROPERTY,
                    tp[i]) ;
            }
        }

        // naming policy
        OntologySchema.clear(db, OntologySchema.NAMING_POLICY) ;

        NamingPolicy p = (NamingPolicy)lstNamingPolicy.getSelectedValue() ;
        String selectedPolicy = p.getPolicyName() ;
        MOEditor.theInstance.selectedNamingPolicy = p ;

        OntologySchema.addPredicate(db, OntologySchema.NAMING_POLICY,
            selectedPolicy) ;
    }

    // 2005-08-25
    public void onImportOBO(ActionEvent actionEvent)
    {
        String[] current = textProperty.getText().split("\n") ;
        String[] obo = OntologySchema.obo_properties ;
        // make union
        Set<String> all = new TreeSet() ;
        for(int i = 0 ; i < current.length ; i++)
        {
            all.add(current[i]) ;
        }
        for(int i = 0 ; i < obo.length ; i++)
        {
            all.add(obo[i]) ;
        }
        // write it back
        String str = "" ;
        for(String tp : all)
        {
            if(tp.trim().length() > 0)
            {
                str += tp + "\n" ;
            }
        }
        this.textProperty.setText(str) ;
    }
}
