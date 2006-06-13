package edu.iastate.anthill.indus.iterator;

import java.sql.Connection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import edu.iastate.anthill.indus.tree.TypedNode;
import edu.iastate.anthill.indus.tree.TypedTree;

import edu.iastate.utils.sql.JDBCUtils;

/**
 * @author Jie Bao
 * @since 2005-04-21
 */
abstract public class Tree2DB
{
    public Tree2DB(Connection db)
    {
        this.db = db;
    }

    protected Connection db;
    public boolean deleteUndeclaredChildren = false;

    /**
     * @since 2005-04-22
     * @param tree TypedTree
     * @param relationName String eg. ISA, part-of
     */
    public void saveTree(TypedTree tree, String relationName)
    {
        if (this.deleteUndeclaredChildren)
        {
            Enumeration en = tree.getTop().breadthFirstEnumeration();
            while (en.hasMoreElements())
            {
                TypedNode node = (TypedNode) en.nextElement();
                String id = node.getUserObject().toString();
                deleteChildren(id);
            }
        }

        Enumeration en = tree.getTop().breadthFirstEnumeration();
        while (en.hasMoreElements())
        {
            TypedNode node = (TypedNode) en.nextElement();
            TypedNode parent = (TypedNode) node.getParent();

            String id = node.getUserObject().toString();
            //Object comment = node.getComment();

            saveNode(node);

            if (parent != null)
            {
                String parentId = parent.getUserObject().toString();
                saveRelation(id, parentId, relationName);
            }
        }
    }

    abstract protected void deleteNode(String id);

    abstract protected void deleteChildren(String id);

    abstract protected void deleteParent(String id);

    abstract protected void saveNode(TypedNode node);

    abstract protected void saveRelation(String id, String parentId,
                                         String relation);

    /**
     *
     * @param relationTable String
     * @param parentCol String
     * @param id String
     * @param relationCol String
     * @param relationType String
     * @author Jie Bao
     * @since 2005-04-22
     */
    protected void defaultDeleteChildren(String relationTable,
                                         String parentCol,
                                         String id,
                                         String relationCol,
                                         String relationType)
    {
        String sql = "DELETE FROM " + relationTable + " WHERE " +
            parentCol + " = '" + id + "' ";
        if (relationCol != null && relationType != null)
        {
            sql += "AND " + relationCol + "'" + relationType + "'";
        }
        JDBCUtils.updateDatabase(db, sql);
    }

    protected void defaultDeleteParent(String relationTable,
                                       String idCol,
                                       String id,
                                       String relationCol,
                                       String relationType)
    {
        String sql = "DELETE FROM " + relationTable + " WHERE " +
            idCol + " = '" + id + "' ";
        if (relationCol != null && relationType != null)
        {
            sql += "AND " + relationCol + "'" + relationType + "'";
        }
        JDBCUtils.updateDatabase(db, sql);
    }

    /**
     * Insert comment into table
     * The table should has at least two columns, one for id and another for
     *    the comment. id is the primary key.
     *
     * @param commentTable String
     * @param id_col String
     * @param comment_col String
     * @param id String
     * @param comment String
     * @author Jie Bao
     * @since 2005-04-22
     */
    protected void defaultSaveNode(String commentTable, String id_col,
                                   String comment_col, TypedNode node)
    {
        String id = node.getUserObject().toString();
        Object comment = node.getComment();

        Map field_value = new HashMap();
        field_value.put(id_col, id);
        field_value.put(comment_col, comment);

        JDBCUtils.insertOrUpdateDatabase(db, commentTable, field_value, id_col);
    }

    /**
     *
     * @param commentTable String
     * @param id_col String
     * @param comment_col String
     * @param id String
     * @since 2005-04-24
     */
    protected void defaultDeleteNode(String commentTable, String id_col,
                                     String id)
    {
        String sql = "DELETE FROM " + commentTable + " WHERE " +
            id_col + " = '" + id + "' ";
        JDBCUtils.updateDatabase(db, sql);
    }

    /**
     * Save relation, the table has columns
     *     childcol, parentCol, relationCol(optional)
     *
     * @param relationTable String
     * @param childCol String
     * @param id String
     * @param parentCol String
     * @param parent_id String
     * @param relationCol String
     * @param relationType String
     *
     * @author Jie Bao
     * @since 20050-04-20
     */
    protected boolean defaultSaveRelation(String relationTable,
                                       String childCol,
                                       String id,
                                       String parentCol,
                                       String parent_id,
                                       String relationCol,
                                       String relationType)
    {
        // save (id, pid) or (id,pid, relationType)
        Map field_value = new HashMap();
        field_value.put(childCol, id);
        field_value.put(parentCol, parent_id);
        if (relationCol != null && relationType != null)
        {
            field_value.put(relationCol, relationType);
        }

        Vector pk = new Vector();
        pk.add(childCol);
        pk.add(parentCol);
        if (relationCol != null)
        {
            pk.add(relationCol);
        }
        return JDBCUtils.insertOrUpdateDatabase(db, relationTable, field_value, pk);
    }
}
