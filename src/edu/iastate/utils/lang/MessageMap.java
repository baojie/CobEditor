package edu.iastate.utils.lang;

import java.lang.reflect.Method;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JOptionPane;
import java.lang.reflect.*;

/**
 * eg.    JMenuItem menuImportAmazonOnlineList = new JMenuItem();
 *        mapAction(menuImportAmazonOnlineList, this, "onImportAmazonOnlineList");
 *        public void onImportAmazonOnlineList(ActionEvent e)
          {
           ....
          }
 *
 * @author Jie Bao
 * @since 1.0 2005-03-05
 */
public class MessageMap
{

    /**
     * build an action map from a button to obj.methodName
     * @param source AbstractButton - the btn where the message from
     * @param obj Object - where the action will be taken
     * @param methodName String - the method of obj as message handler,
     *    NOTE: method should be public
     *
     * @author JieBao
     * @since 2005-03-05
     */
    static public void mapAction(Object source, final Object obj,
                                 final String methodName) throws Exception
    {
        // Make sure source class has "addActionListener" method
        Class sourceCls = source.getClass();
        Method addActionListener = sourceCls.getMethod
            ("addActionListener", new Class[]
             {ActionListener.class});

        if (addActionListener != null)
        {
            addActionListener.invoke(source, new Object[]
                                     {new ActionListener()
            {
                public void actionPerformed(ActionEvent e)
                {
                    try
                    {
                        Class cls = obj.getClass();
                        //System.out.println(cls);
                        Method m = cls.getMethod(methodName, new Class[]
                                                 {ActionEvent.class});
                        //System.out.println(m);
                        m.invoke(obj, new Object[]
                                 {e});

                    }
                    catch (NoSuchMethodException ex)
                    {
                        String cls = obj.getClass().getName();
                        JOptionPane.showMessageDialog(null,
                            "No handler is defined for command '" +
                            e.getActionCommand() + "'\n" +
                            "Please add : public " + cls + "." + methodName +
                            "(ActionEvent e)" +
                            "\n   Make sure the method is public!"
                            );
                        ex.printStackTrace();
                    }
                    catch (InvocationTargetException ex1)
                    {
                        ex1.printStackTrace();
                    }
                    catch (IllegalArgumentException ex1)
                    {
                        ex1.printStackTrace();
                    }
                    catch (IllegalAccessException ex1)
                    {
                        ex1.printStackTrace();
                    }
                }
            }
            });
        }
        else //if ...
        {
            throw new Exception(
                "No mapping for such message source implemented!");
        }

    }

}
