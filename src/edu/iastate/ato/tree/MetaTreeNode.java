package edu.iastate.ato.tree ;

import java.sql.Connection ;

/**
 * <p>@author Jie Bao</p>
 *
 * <p>@since 2005-07-27</p>
 *
 * <p> </p>
 *
 * <p> </p> not attributable
 */
public class MetaTreeNode extends ATOTreeNode
{
    public String getViewMode()
    {
        return null ;
    }

    public MetaTreeNode(Object id, Object externalText)
    {
        super(id, externalText, META) ;
        //this.setReadOnly(false);
    }

    public void save(Connection db)
    {
    }

}
