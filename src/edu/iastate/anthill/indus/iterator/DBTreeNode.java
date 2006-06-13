package edu.iastate.anthill.indus.iterator;

import edu.iastate.ato.po.DataSourceNode;

/**
 * @author Jie Bao
 * @since 1.0 2005-03-05
 */
public class DBTreeNode
    extends DataSourceNode
{
    public DBTreeNode(Object id, Object externalText)
    {
        super(id);
        this.comment = externalText;
        setType(DataSourceNode.AVH);
    }

    public String toString()
    {
        String more = (comment == null) ? "" : (String) comment;
        return getUserObject() + " : " + more;
    }
}
