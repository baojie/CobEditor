package edu.iastate.ato.po;

import edu.iastate.anthill.indus.tree.TypedNode;

/**
 *
 * @author Jie Bao
 * @since 1.0
 */
public class DataSourceNode
    extends TypedNode
{
    private String datatype;

    public Object clone()
    {
        DataSourceNode t = new DataSourceNode(this.getUserObject());
        t.type = this.type;
        t.comment = this.comment;
        t.color = this.color;
        t.datatype = this.datatype;

        return t;
    }

    public DataSourceNode(Object userObject)
    {
        super(userObject);
    }

    public DataSourceNode(String name, short type, String datatype)
    {
        super(name);
        this.type = type;
        this.datatype = datatype;
    }

    public DataSourceNode(String name, short type, String datatype,
                          String comment)
    {
        super(name);
        this.type = type;
        this.datatype = datatype;
        this.comment = comment;
    }

    public void propertySetup()
    {}

    public String getNodeInformation(boolean inHTML)
    {
        return toString();
    }

    public String toString()
    {
        if (datatype != null && type != AVH)
        {
            return getUserObject().toString() + ":" + datatype;
        }
        else
        {
            if (comment != null)
            {
                //System.out.println(getUserObject().toString() + ":" + comment);
                return getUserObject().toString() + ":" + comment;
            }
            else
            {
                //System.out.println(getUserObject().toString() + ":" + comment);
                return getUserObject().toString();
            }
        }
    }

    public String getDatatype()
    {
        return datatype;
    }

    public void setDatatype(String datatype)
    {
        this.datatype = datatype;
    }
}
