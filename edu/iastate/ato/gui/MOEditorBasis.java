package edu.iastate.ato.gui ;

import java.sql.Connection ;
import java.util.HashSet ;
import java.util.Set ;

import javax.swing.JFileChooser ;
import javax.swing.JFrame ;
import javax.swing.JPanel ;
import javax.swing.filechooser.FileFilter ;

import edu.iastate.ato.agent.MoAgent ;
import edu.iastate.ato.po.OntologySchema ;
import edu.iastate.ato.po.User ;
import edu.iastate.ato.po.naming.BasicNamingPolicy ;
import edu.iastate.ato.po.naming.NamingPolicy ;
import edu.iastate.ato.po.naming.NamingPolicyFactory ;

import edu.iastate.utils.gui.FileFilterEx ;
import edu.iastate.utils.io.FileUtils ;
import edu.iastate.utils.sql.LocalDBConnection ;

/**
 * Method and field that have nothing to do with GUI and action.
 *
 * <p>@author Jie Bao</p>
 * <p>@since 2005-08-19</p>
 */
public class MOEditorBasis extends JPanel
{
    public JFrame mainFrame = new JFrame() ;
    protected LocalDBConnection conn ; // = getConnection();
    public MoAgent messenger = new MoAgent(User.GUEST) ;

    public static LocalDBConnection getConnection(OntologyServerInfo info)
    {
        LocalDBConnection ds = new LocalDBConnection() ;

        ds.setUrl(info.url) ;
        ds.setUser(info.user) ;
        ds.setPassword(info.password) ;
        ds.setDriver(info.driver) ;

        if(!ds.connect())
        {
            //Debug.trace("Cannot connect to the server");
            //System.exit(0);
            return null ;
        }
        return ds ;
    }

    public NamingPolicy selectedNamingPolicy ;

    protected void prepareNamingPolicy(Connection db)
    {
        // naming policy
        selectedNamingPolicy = new BasicNamingPolicy() ;

        // load the system selected policy
        String policy = OntologySchema.getNamingPolicy(conn.db) ;
        if(policy != null)
        {
            selectedNamingPolicy = NamingPolicyFactory.buildPolicyFromName(
                policy, db) ;
        }

    }

    protected MOEditorConfig config = new MOEditorConfig("onteditor.xml") ;
    public Set<OntologyServerInfo> serverList = new HashSet() ;
    public OntologyServerInfo selectedServer = null ; //OntologyInfo.getAtoOntology();

    public static User user ;

    protected String getFileName(String title, String extension,
        String description, boolean isSave)
    {
        // ask for a place to save the file
        JFileChooser saveDialog = new JFileChooser() ;

        int mode = isSave ? JFileChooser.SAVE_DIALOG : JFileChooser.OPEN_DIALOG ;
        saveDialog.setDialogType(mode) ;
        saveDialog.setDialogTitle(title) ;

        FileFilterEx firstFilter = new FileFilterEx(extension, description) ;
        saveDialog.addChoosableFileFilter(firstFilter) ;

        //The "All Files" file filter is added to the dialog
        //by default. Put it at the end of the list.
        FileFilter all = saveDialog.getAcceptAllFileFilter() ;
        saveDialog.removeChoosableFileFilter(all) ;
        saveDialog.addChoosableFileFilter(all) ;
        saveDialog.setFileFilter(firstFilter) ;

        int returnVal = saveDialog.showSaveDialog(this.mainFrame) ;
        if(returnVal == JFileChooser.APPROVE_OPTION)
        {
            String newfile = saveDialog.getSelectedFile().getPath() ;

            if(isSave)
            {
                // if the name have no extenstion, append extension like "owl"
                if(FileUtils.findExtension(newfile) == "")
                {
                    newfile += "." + extension ;
                }
            }
            return newfile ;
        }
        return null ;
    }
}
