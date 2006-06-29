package edu.iastate.ato.gui.dialog ;

import java.sql.Connection ;

import java.awt.BorderLayout ;
import java.awt.event.ActionEvent ;
import java.awt.event.KeyEvent ;
import javax.swing.BorderFactory ;
import javax.swing.JButton ;
import javax.swing.JDialog ;
import javax.swing.JPanel ;
import javax.swing.JPasswordField ;
import javax.swing.JTextField ;

import edu.iastate.ato.po.User ;
import edu.iastate.ato.po.UserManager ;

import edu.iastate.utils.Debug ;
import edu.iastate.utils.gui.GUIUtils ;
import edu.iastate.utils.gui.LabelledItemPanel ;
import edu.iastate.utils.gui.StandardDialog ;
import edu.iastate.utils.lang.MessageHandler ;
import edu.iastate.utils.lang.MessageMap ;
import edu.iastate.utils.string.Validator ;

/**
 * Panel to handle login and registration
 * <p>@author Jie Bao</p>
 * <p>@since </p>
 */
public class LoginPanel extends JPanel implements MessageHandler
{
    BorderLayout borderLayout1 = new BorderLayout() ;
    JButton btnOK = new JButton() ;
    JPanel buttonPane = new JPanel() ;
    JButton btnCancel = new JButton() ;
    JDialog dlg = new JDialog(GUIUtils.getRootFrame(this), true) ;
    public boolean ok ;
    String asUser = null ;
    Connection db ;
    private JTextField userTxt = new JTextField() ;
    private JPasswordField passTxt = new JPasswordField() ;
    JButton btnGuest = new JButton() ;
    JButton btnRegister = new JButton() ;

    public String getUser()
    {
        return userTxt.getText().trim() ;
    }

    public LoginPanel(Connection db, User u)
    {
        try
        {
            this.db = db ;
            if(u != null)
            {
                userTxt.setText(u.name) ;
                passTxt.setText(u.pass) ;
            }
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

        this.setLayout(borderLayout1) ;
        LabelledItemPanel myContentPane = new LabelledItemPanel() ;
        myContentPane.setBorder(BorderFactory.createEtchedBorder()) ;
        myContentPane.addItem("User", userTxt) ;
        myContentPane.addItem("Password", passTxt) ;

        btnOK.setText("OK") ;
        btnOK.setMnemonic(KeyEvent.VK_ENTER) ;
        btnOK.setToolTipText("Alt+Enter") ;

        btnCancel.setText("Cancel") ;

        btnGuest.setText("As Guest") ;
        btnGuest.setMnemonic(KeyEvent.VK_G);

        btnRegister.setText("Register...") ;
        btnRegister.setMnemonic(KeyEvent.VK_R);

        this.add(myContentPane, BorderLayout.CENTER) ;
        this.add(buttonPane, java.awt.BorderLayout.SOUTH) ;
        buttonPane.add(btnOK) ;
        buttonPane.add(btnGuest) ;
        buttonPane.add(btnRegister) ;
        buttonPane.add(btnCancel) ;
        dlg.getContentPane().add(this) ;
        dlg.setTitle("User Setting") ;
        dlg.setSize(400, 150) ;
        dlg.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE) ;
        GUIUtils.centerWithinParent(dlg) ;
    }

    public void showDlg()
    {
        dlg.setVisible(true) ;
    }

    public void onOK(ActionEvent e)
    {
        ok = UserManager.rightUser(db, userTxt.getText().trim(),
            new String(passTxt.getPassword()).trim()) ;
        if(!ok)
        {
            Debug.trace("incorrect user or password") ;
            return ;
        }

        dlg.setVisible(false) ;
        dlg.dispose() ;
    }

    public void onCancel(ActionEvent e)
    {
        ok = false ;
        dlg.setVisible(false) ;
        dlg.dispose() ;
    }

    public void onGuest(ActionEvent e)
    {
        userTxt.setText("guest") ;
        passTxt.setText("guest") ;
        onOK(e) ;
    }

    public void onRegister(ActionEvent e)
    {
        StandardDialog dlg = new StandardDialog() ;
        LabelledItemPanel myContentPane = new LabelledItemPanel() ;
        myContentPane.setBorder(BorderFactory.createEtchedBorder()) ;
        JTextField testUser = new JTextField() ;
        myContentPane.addItem("User id (required)", testUser) ;
        JPasswordField testPassword = new JPasswordField() ;
        myContentPane.addItem("Password (required)", testPassword) ;
        JPasswordField testPasswordRetype = new JPasswordField() ;
        myContentPane.addItem("Password Retype(required)", testPasswordRetype) ;
        JTextField testEmail = new JTextField() ;
        myContentPane.addItem("Email (required)", testEmail) ;
        JTextField textName = new JTextField() ;
        myContentPane.addItem("Name ", textName) ;
        JTextField textInstitution = new JTextField() ;
        myContentPane.addItem("Institution ", textInstitution) ;
        dlg.setContentPane(myContentPane) ;

        dlg.setTitle("Register as editor") ;
        dlg.setSize(400, 300) ;
        dlg.setVisible(true) ;
        GUIUtils.centerWithinScreen(dlg) ;

        if(!dlg.hasUserCancelled())
        {
            // if name not null

            // if the id is taken
            String id = testUser.getText().trim() ;
            boolean userExists = UserManager.ifUserExist(db, id) ;
            boolean isUserIDValid = (id != null) && (id.length() > 0) ;

            // if email valid
            String email = testEmail.getText().trim() ;
            boolean isEmailValid = Validator.isEmailAddress(email) ;

            // if password not null
            String pass = new String(testPassword.getPassword()).trim() ;
            boolean isPasswordValid =
                (pass != null) && (pass.length() > 0) ;

            // if password retype is the same
            String passRetype = new String(testPasswordRetype.getPassword()).
                trim() ;
            boolean isSame = (pass != null) && (passRetype != null)
                && pass.equals(passRetype) ;

            if(userExists || !isUserIDValid || !isEmailValid ||
                !isPasswordValid || !isSame)
            {
                String info = "Error!\n\n" ;
                if(!isUserIDValid)
                {
                    info += "* Name is null\n" ;
                }
                if(userExists)
                {
                    info += "* The name '" + id + "' has already been used\n" ;
                }
                if(!isEmailValid)
                {
                    info += "* Email address is not valid\n" ;
                }
                if(!isPasswordValid)
                {
                    info += "* Password is null" ;
                }
                if(!isSame)
                {
                    info += "* Password and its retype don't match\n" ;
                }
                Debug.trace(info) ;
                return ;
            }

            // ask the ontology server for a user id
            String name = testEmail.getText() ;
            String institution = textInstitution.getText() ;
            boolean suc = UserManager.applyForID(
                db, id, pass, email, name, institution, User.PACKAGE_ADMIN) ;
            String good = "User is successfully applied" ;
            String bad = "Application failed, please try again later" ;
            Debug.trace(suc ? good : bad) ;

            if(suc)
            {
                userTxt.setText(id) ;
                passTxt.setText(pass) ;
            }
        }
    }

    public void messageMap()
    {
        try
        {
            MessageMap.mapAction(this.btnGuest, this, "onGuest") ;
            MessageMap.mapAction(this.btnRegister, this, "onRegister") ;
            MessageMap.mapAction(this.btnCancel, this, "onCancel");
            
            MessageMap.mapAction(this.btnOK, this, "onOK") ;
            MessageMap.mapAction(this.passTxt, this, "onOK") ;
            MessageMap.mapAction(this.userTxt, this, "onOK") ;
        }
        catch(Exception ex)
        {
        }
    }
}
