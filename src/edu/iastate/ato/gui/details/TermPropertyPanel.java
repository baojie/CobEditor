package edu.iastate.ato.gui.details ;

import java.sql.Connection ;
import java.util.HashMap ;
import java.util.HashSet;
import java.util.Map ;
import java.util.Vector ;

import java.awt.BorderLayout ;
import java.awt.Color;
import java.awt.Insets;
import java.awt.event.ActionEvent ;

import javax.swing.BorderFactory;
import javax.swing.JButton ;
import javax.swing.JComboBox ;
import javax.swing.JComponent ;
import javax.swing.JFrame ;
import javax.swing.JLabel ;
import javax.swing.JPanel ;
import javax.swing.JScrollPane ;
import javax.swing.JTextArea ;

import edu.iastate.ato.gui.MOEditor ;
import edu.iastate.ato.gui.OntologyServerInfo ;
import edu.iastate.ato.po.OntologyEdit ;
import edu.iastate.ato.po.OntologyQuerier ;
import edu.iastate.ato.po.OntologySchema ;
import edu.iastate.ato.po.TagValuePair ;
import edu.iastate.ato.shared.IconLib ;
import edu.iastate.ato.tree.DbTermNode ;
import edu.iastate.ato.tree.PackageNode;

import edu.iastate.utils.Debug ;
import edu.iastate.utils.gui.LabelledItemPanel ;
import edu.iastate.utils.lang.MessageHandler ;
import edu.iastate.utils.lang.MessageMap ;
import edu.iastate.utils.sql.LocalDBConnection ;

/**
 * <p>@author Jie Bao</p>
 * <p>@since 2005-07-22</p>
 */
public class TermPropertyPanel extends JPanel implements MessageHandler
{

    public DbTermNode selectedNode ;
    Connection db ;

    // BUTTONS
    JButton btnConfirm = new JButton() ;
    JButton btnCancel = new JButton() ;
    JButton btnAdd = new JButton("Add") ;

    LabelledItemPanel mainPane = new LabelledItemPanel(0) ;
    JScrollPane jScrollPane1 = new JScrollPane() ;

    JPanel paneButton = new JPanel() ;
    Vector<String> tp ;
    Map<JTextArea, JComponent> allItem =
        new HashMap<JTextArea, JComponent>() ;

    HashSet<JTextArea> newItems = new HashSet<JTextArea>() ;
    HashSet<JTextArea> deletedItems = new HashSet<JTextArea>() ;

    // map from the property name to its JTextField
    //Map<String, JTextField> attributeEditors = new HashMap();

    public JComboBox makeComboBox()
    {
        return new JComboBox(tp) ;
    }

    // 2005-08-22
    public JTextArea addNewProperty(String attr)
    {
        JComboBox cb = makeComboBox() ;
        cb.setEditable(false) ;
        if(attr != null)
        {
            cb.setSelectedItem(attr) ;
        }
        else if(cb.getItemCount() > 0)
        {
            cb.setSelectedIndex(0) ;
        }
        JTextArea v = new JTextArea() ;
        
        JPanel p = new JPanel();
        p.add(createDeleteButton(v));
        p.add(cb);
        
        mainPane.addItem(p, v) ;
        allItem.put(v, cb);
        newItems.add(v);
        return v ;
    }

    public class OnDeletionHandler implements MessageHandler{
    	public JTextArea vref;
    	public OnDeletionHandler(JTextArea v){
    		vref = v;
    	}
    	
    	public void onDelete(ActionEvent e){
    		vref.setEnabled(false);
    		deletedItems.add(vref);
    	}
    	
        public void messageMap(){
        	
        }
    }
    
    private JButton createDeleteButton(JTextArea vref){
    	JButton deleteButton = new JButton();
		deleteButton.setMargin(new Insets(0,0,0,0));
		deleteButton.setIcon(IconLib.iconDelete_sm);   
		deleteButton.setSelectedIcon(IconLib.iconAddSubPackage);   
		deleteButton.setBorder(BorderFactory.createEtchedBorder());
		deleteButton.setBackground(new Color(238,238,238));
		deleteButton.setToolTipText("Delete");
		
		try{
    		MessageMap.mapAction(deleteButton, new OnDeletionHandler(vref), "onDelete") ;
	    }
	    catch(Exception ex){}
	    
		return deleteButton;
    }
    
    public JTextArea addProperty(String attr, String value)
    {
        JLabel label = new JLabel(attr) ;
        JTextArea v = new JTextArea(value) ;

        JPanel p = new JPanel();
        p.add(createDeleteButton(v));
        JComboBox cb = makeComboBox();
        cb.setEnabled(false);
        p.add(cb);
        
        
        mainPane.addItem(p, v) ;
        allItem.put(v, label) ;
        return v ;
    }

    public String getLabel(JTextArea v)
    {
        JComponent label = allItem.get(v) ;
        if(label instanceof JLabel)
        {
            return((JLabel)label).getText() ;
        }
        else if(label instanceof JComboBox)
        {
            return((JComboBox)label).getSelectedItem().toString() ;
        }
        return null ;
    }

    public TermPropertyPanel(Connection db)
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
        messageMap() ;

        this.setLayout(new BorderLayout()) ;

        // get all property
        tp = OntologySchema.getTermProperties(db) ;

        btnAdd.setToolTipText("Add New Property") ;
        paneButton.add(btnAdd) ;
        btnConfirm.setIcon(IconLib.iconOK) ;
        btnConfirm.setToolTipText("Confirm") ;
        paneButton.add(btnConfirm) ;
        btnCancel.setIcon(IconLib.iconCancel) ;
        btnCancel.setToolTipText("Cancel") ;
        paneButton.add(btnCancel) ;

        jScrollPane1.getViewport().add(mainPane) ;
        this.add(paneButton, java.awt.BorderLayout.SOUTH) ;
        this.add(jScrollPane1, java.awt.BorderLayout.CENTER) ;
        //this.add(mainPane, java.awt.BorderLayout.CENTER);
    }

    public void updatePanel(DbTermNode selectedNode)
    {
        mainPane.clear() ;
        allItem.clear() ;

        if(selectedNode != null)
        {
            this.selectedNode = selectedNode ;
            String term_oid = selectedNode.getOid() ;

            // if it is read only?
            boolean readonly = selectedNode.isReadOnly() ;

            // get available properties
            Vector<TagValuePair>
                all = OntologyQuerier.getTermAllProperty(db, term_oid) ;
            for(TagValuePair pair : all)
            {
                addProperty(pair.tag, pair.value) ;
            }
                        
            enableEditing(!readonly && ((PackageNode)selectedNode.getHomePackageNode()).editing ) ;
        }else{
        	enableEditing(false) ;
        }
        mainPane.validate() ;
        mainPane.invalidate() ;
        mainPane.repaint() ;
    }
    
    public void enableEditing(boolean enabled)
    {
        for(JTextArea item : allItem.keySet())
        {
            item.setEditable(enabled) ;
        }
        // Added these 2
        btnAdd.setEnabled(enabled);
    	btnCancel.setEnabled(enabled);
    	
    	btnConfirm.setEnabled(enabled);
    }

    public void messageMap()
    {
        try
        {
        	MessageMap.mapAction(this.btnConfirm, this, "onConfirm") ;
            MessageMap.mapAction(this.btnCancel, this, "onCancel") ;
            MessageMap.mapAction(this.btnAdd, this, "onAdd") ;
        }
        catch(Exception ex)
        {
        }
    }

    // add a new property
    // 2005-08-22
    public void onAdd(ActionEvent e)
    {
        if(tp.size() == 0)
        {
            Debug.trace("No property available, please edit ontology schema" +
                "\nMenu: " +
                MOEditor.theInstance.menuOntologySchema.getText()) ;
            return ;
        }
        else
        {
            addNewProperty(null) ;
            mainPane.invalidate() ;
            mainPane.validate() ;
            System.out.println("add new") ;
        }
    }

    public void onConfirm(ActionEvent e)
    {
        // save it
        String term_oid = selectedNode.getOid() ;

        for(JTextArea item : allItem.keySet())
        {
            // read the property
            String value = item.getText() ;
            if(value != null && value.length() > 0)
            {
                // JDBCUtils:insertOrUpdateDatabase() -  insert with null primary key
            	if(deletedItems.contains(item)){
            		if(!newItems.contains(item)){
	        			// Delete Item
	        			OntologyEdit.deleteTermProperty(db,term_oid,getLabel(item));
            		}
            		
            	}else{
            	    String attr = getLabel(item) ;
	                OntologyEdit.addTermProperty(db, term_oid, attr, value,
	                    MOEditor.user.name) ;
	                allItem.get(item).setEnabled(false) ;
            	}
            }
        }
    }
    
    public void deleteTermProperty(){
    	   	
    }

    public void onCancel(ActionEvent e)
    {
        updatePanel(selectedNode) ;
    }

    // unit test
    public void test()
    {
        // show it
        JFrame frame = new JFrame() ;
        frame.setSize(800, 600) ;
        JScrollPane scr = new JScrollPane(this) ;
        frame.getContentPane().add(scr) ;
        frame.setVisible(true) ;
    }

    public static void main(String[] args)
    {
        LocalDBConnection conn =
            MOEditor.getConnection(OntologyServerInfo.getAtoOntology()) ;
        if(conn.connect())
        {
            TermPropertyPanel p = new TermPropertyPanel(conn.db) ;
            p.test() ;
        }
        else
        {
            Debug.trace("Cannot connect to database") ;
        }
    }
}
