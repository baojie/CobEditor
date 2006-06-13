package edu.iastate.ato.tree ;

import java.sql.Connection ;
import java.util.HashMap ;
import java.util.Map ;

import java.awt.Color ;

import edu.iastate.anthill.indus.iterator.DBTreeNode ;
import edu.iastate.ato.po.Package ;

/**
 * @author Jie Bao
 * @since 1.0 2005-04-23
 */
public abstract class ATOTreeNode extends DBTreeNode
{
    private boolean readOnly = true ;

    String oid ; // database id

    abstract public String getViewMode() ;

    abstract public void save(Connection db) ;

    public boolean isReadOnly()
    {
        return readOnly ;
    }

    public void setReadOnly(boolean r)
    {
        this.readOnly = r ;
    }

    static public final short UNMODIFIED = 0 ;
    static public final short MODIFIED = 1 ;
    static public final short DELETED_NODE = 2 ;
    static public final short DELETED_UPEDGE = 3 ;
    static public final short MERGED = 4 ;

    public String status2string()
    {
        if(status == UNMODIFIED)
        {
            return "Unmodified" ;
        }
        else if(status == MODIFIED)
        {
            return "Modified" ;
        }
        else if(status == DELETED_NODE)
        {
            return "Deleted with all relations" ;
        }
        else if(status == DELETED_UPEDGE)
        {
            return "Relation to parent deleted" ;
        }
        return null ;
    }

    public final static short ROOT = 1000 ;
    public final static short SPECIES = 1001 ;
    public final static short CLASS = 1002 ;
    public final static short TYPE = 1003 ;
    public final static short TRAIT = 1004 ;
    public final static short PACKAGE = 1005 ;
    public final static short PUBLIC_TERM = 1006 ;
    public final static short PRIVATE_TERM = 1007 ;
    public final static short PROTECTED_TERM = 1008 ;

    public final static short META = 10000 ;

    public static String type2slm(short type)
    {
        if(type == PUBLIC_TERM)
        {
            return Package.PUBLIC ;
        }
        else if(type == PRIVATE_TERM)
        {
            return Package.PRIVATE ;
        }
        else if(type == PROTECTED_TERM)
        {
            return Package.PROTECTED ;
        }
        return null ;
    }

    public static short slm2type(String slm)
    {
        if(slm == null)
        {
            return 0 ;
        }
        else if(slm.equals(Package.PUBLIC))
        {
            return PUBLIC_TERM ;
        }
        else if(slm.equals(Package.PRIVATE))
        {
            return PRIVATE_TERM ;
        }
        else if(slm.equals(Package.PROTECTED))
        {
            return PROTECTED_TERM ;
        }
        return 0 ;
    }

    public short status = UNMODIFIED ;

    public ATOTreeNode(Object id, Object externalText, short type)
    {
        super(id, externalText) ;
        this.comment = externalText ;
        setType(type) ;
    }

    // 2005-07-30
    public boolean isDeleted()
    {
        return(status == DELETED_NODE) || (status == DELETED_UPEDGE) ;
    }

    // 2005-08-13
    public boolean isMerged()
    {
        return(status == MERGED) ;
    }

    public boolean isChanged()
    {
        return(status != UNMODIFIED) ;
    }

    public Color getColor()
    {
        if(isReadOnly())
        {
            return Color.lightGray ;
        }
        else
        {
            Map<Short, Color> m = new HashMap() ;
            m.put(UNMODIFIED, Color.black) ;
            m.put(MODIFIED, Color.red) ;
            m.put(MERGED, Color.red) ;
            m.put(DELETED_NODE, Color.cyan) ;
            m.put(DELETED_UPEDGE, Color.blue) ;

            color = m.get(status) ;
            return color ;
        }
    }

    // 2005-07-30
    public void rename(String newName)
    {
        setUserObject(newName) ;
        status = MODIFIED ;
    }
}
