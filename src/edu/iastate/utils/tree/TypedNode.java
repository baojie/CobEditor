package edu.iastate.utils.tree;

import java.awt.Color;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.Icon;

/**
 * Node with a type
 * @author Jie Bao
 * @since 1.0 2004-10-02
 */
public class TypedNode
    extends DefaultMutableTreeNode implements Comparable
{

    // type of the node
    protected short type;
    protected Object comment; // 2004-10-13
    protected Color color; // 2005-04-07

    public Object clone()
    {
        TypedNode t = new TypedNode(this.getUserObject());
        t.type = this.type;
        t.comment = this.comment;
        t.color = this.color;
        return t;
    }

    public short getType()
    {
        return type;
    }

    public void setType(short type)
    {
        this.type = type;
    }

    public TypedNode(Object userObject)
    {
        super(userObject);
    }

    /**
     * Icon of the node
     * This method should be overloaded in your class
     * @return Icon
     * @see TypedTreeRender
     * @author Jie Bao
     * @since 2005-08-23
     */
    public Icon getIcon()
    {
        return null;
    }

    /**
     * @param userObject Object
     * @param type short
     * @param comment String
     * @since 2004-10-18
     */
    public TypedNode(Object userObject, short type, String comment)
    {
        super(userObject);
        setType(type);
        setComment(comment);
    }

    public boolean equals(Object node)
    {
        if (node instanceof TypedNode)
        {
            return getLocalName().equals( ( (TypedNode) node).getLocalName());
        }
        else
        {
            return false;
        }
    }

    /**
     * Get name of the node
     * @since 2004-10-13
     */
    public String getLocalName()
    {
        return getUserObject().toString();
    }

    public Object getComment()
    {
        return comment;
    }

    public Color getColor()
    {
        return color;
    }

    public void setComment(Object comment)
    {
        this.comment = comment;
    }

    public void setColor(Color color)
    {
        this.color = color;
    }

    /**
     * toXML - plain text only YET
     *
     * @return String
     * @since 2004-10-18
     */
    public String toXML()
    {
        return getUserObject().toString();
    }

    /**
     * read from XML  - plain text only YET
     * @param xml String
     * @since 2004-10-18
     */
    public void fromXML(String xml)
    {
        this.setUserObject(xml);
    }

    public final static short ROOT = 0;
    public final static short ALL_CLASSES = 1;
    public final static short ALL_PROPERTIES = 2;
    public final static short ALL_INSTANCES = 3;

    public final static short CLASS = 4;
    public final static short PROPERTY = 5;
    public final static short INSTANCE = 6;

    public final static short PACKAGE = 7;
    public final static short DOMAIN = 8;

// used on INDUS Data source editor
    public final static short ATTRIBUTE = 9;
    public final static short AVH = 10;
    public final static short DB = 11;
    public final static short TABLE = 12;

    /**
     *
     * @param o Object
     * @return int
     * @since 2004-10-18
     */
    public int compareTo(Object o)
    {
        if (o instanceof TypedNode)
        {
            return getLocalName().compareTo( ( (TypedNode) o).getLocalName());
        }
        return -2;
    }

    /**
     * TypedNode
     */
    public TypedNode()
    {
    }

}
