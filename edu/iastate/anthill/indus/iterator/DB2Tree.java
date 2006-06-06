package edu.iastate.anthill.indus.iterator;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;

import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import edu.iastate.anthill.indus.tree.TreeNodeInsertEditing;
import edu.iastate.anthill.indus.tree.TypedNode;
import edu.iastate.anthill.indus.tree.TypedTree;

import edu.iastate.utils.undo.BulkEditingAction;
import edu.iastate.utils.undo.EditingAction;
import edu.iastate.utils.lang.SortedVector;

/**
 * @author Jie Bao
 * @since 1.0 2005-03-11
 */
abstract public class DB2Tree {
	public DB2Tree(Connection db) {
		this.db = db;
	}

	protected Connection db;

	abstract protected String findComments(String id);

	/**
	 * Query over a table commentTable(id_col, comment_col)
	 * 
	 * @param commentTable
	 *            String
	 * @param id_col
	 *            String
	 * @param comment_col
	 *            String
	 * @param id
	 *            String
	 * @return String
	 * @since 2005-03-30
	 * @author Jie Bao
	 */
	protected String defaultFindComments(String commentTable, String id_col,
			String comment_col, String id) {
		try {
			String sql = "SELECT " + comment_col + " FROM " + commentTable
					+ " WHERE " + id_col + "='" + id + "'";
			System.out.println(sql);
			Statement stmt = db.createStatement();
			ResultSet rs = stmt.executeQuery(sql);
			if (rs.next()) {
				String comment = rs.getString(comment_col);
				return (comment == null) ? null : comment.trim();
			}
		} catch (SQLException ex) {
			ex.printStackTrace();
		}
		return null;

	}

	abstract public String getRootId();

	abstract protected Vector getChildren(String from_id);

	/**
	 * 
	 * @param relationTable
	 *            String
	 * @param childCol
	 *            String
	 * @param parentCol
	 *            String
	 * @param from_id
	 *            String
	 * @param relationCol
	 *            String
	 * @param relationType
	 *            String
	 * @return Vector
	 * @since 2005-03-30
	 * @author Jie Bao
	 */
	protected Vector defaultGetChildren(String relationTable, String childCol,
			String parentCol, String from_id, String relationCol,
			String relationType) {
		try {
			String sql = "SELECT " + childCol + " FROM " + relationTable
					+ " WHERE " + parentCol + "='" + from_id + "'";
			if (relationCol != null && relationType != null) {
				sql += " AND " + relationCol + " ='" + relationType + "'";
			}
			System.out.println(sql);
			Statement stmt = db.createStatement();
			ResultSet rs = stmt.executeQuery(sql);
			SortedVector vec = new SortedVector();
			while (rs.next()) {
				String str = rs.getString(childCol).trim();
				vec.add(str);
			}
			vec.sort();
			return vec;
		} catch (SQLException ex) {
			ex.printStackTrace();
		}
		return null;

	}

	abstract protected Vector getParent(String from_id);

	/**
	 * 
	 * @param relationTable
	 *            String
	 * @param childCol
	 *            String
	 * @param parentCol
	 *            String
	 * @param from_id
	 *            String
	 * @param relationCol
	 *            String
	 * @param relationType
	 *            String
	 * @return Vector
	 * @since 2005-03-30
	 * @author Jie Bao
	 */
	protected Vector defaultGetParent(String relationTable, String childCol,
			String parentCol, String from_id, String relationCol,
			String relationType) {
		try {
			String sql = "SELECT " + parentCol + " FROM " + relationTable
					+ " WHERE " + childCol + "='" + from_id + "'";
			if (relationCol != null && relationType != null) {
				sql += " AND " + relationCol + " ='" + relationType + "'";
			}
			System.out.println(sql);
			Statement stmt = db.createStatement();
			ResultSet rs = stmt.executeQuery(sql);
			SortedVector vec = new SortedVector();
			while (rs.next()) {
				String str = rs.getString(parentCol);
				vec.add(str);
			}
			vec.sort();

			return vec;
		} catch (SQLException ex) {
			ex.printStackTrace();
		}
		return null;
	}

	// 2005-03-30
	protected String getFirstParent(String from_id) {
		Vector ppp = getParent(from_id);
		if (ppp != null && ppp.size() > 0) {
			return (String) ppp.elementAt(0);
		} else {
			return null;
		}
	}

	/**
	 * Get the first parent of a given node we expect to extend the design for
	 * DAG latter on, that's why "first parent"
	 * 
	 * @param from_id
	 *            String
	 * @return Vector the path from root to the given node
	 * @since 2005-03-30
	 */
	protected Vector getFirstParentPath(String from_id) {
		Vector vec = new Vector();
		String current = from_id;
		while (current != null) {
			vec.add(0, current);
			current = getFirstParent(current);
		}
		return vec;
	}

	/**
	 * Insert a path into a tree
	 * 
	 * @param tree
	 *            TypedTree
	 * @param path
	 *            Vector - of String, from high to low
	 * @author Jie Bao
	 * @since 2005-03-30
	 */
	protected EditingAction insertPath(TypedTree tree, Vector path) {
		DefaultTreeModel model = tree.getModel();
		EditingAction action = null;

		Vector existingNode = new Vector(path);
		Vector newNode = new Vector();
		while (existingNode.size() > 0) {
			// System.out.println("existingNode=" + existingNode);
			Object[] nodes = existingNode.toArray();
			TreePath treePath = TypedTree.findByUserObject(tree, nodes);
			// System.out.println("treePath="+treePath);
			if (treePath != null) {
				// find the position to insert
				TypedNode toInsert = (TypedNode) treePath
						.getLastPathComponent();
				// insert remaining node under toInsert
				for (int i = 0; i < newNode.size(); i++) {
					String id = (String) newNode.elementAt(i);
					DBTreeNode node = createNode(id);
					model.insertNodeInto(node, toInsert, toInsert
							.getChildCount());
					if (action == null) {
						action = new TreeNodeInsertEditing(tree, node, toInsert);
					}

					toInsert = node;
				}
				break;
			}
			// remove the last node, search again with a shorter path
			newNode.add(0, existingNode.lastElement());
			existingNode.remove(existingNode.lastElement());
		}
		return action;
	}

	/**
	 * Insert a set of values to a tree
	 * 
	 * @param tree
	 *            TypedTree
	 * @param values
	 *            Set
	 * @author Jie Bao
	 * @since 2005-03-30
	 */
	public BulkEditingAction insertSet(TypedTree tree, Set values) {
		BulkEditingAction allActions = new BulkEditingAction(null);
		for (Iterator it = values.iterator(); it.hasNext();) {
			String id = (String) it.next();
			Vector path = getFirstParentPath(id);
			// System.out.println(path);
			EditingAction action = insertPath(tree, path);
			allActions.addAction(action);
		}
		return allActions;
	}

	/**
	 * 
	 * @param id
	 *            String
	 * @return DBTreeNode
	 */
	public DBTreeNode createNode(String id) {
		// System.out.println("DB2Tree.createNode() :" + id);
		String descrption = findComments(id);
		return new DBTreeNode(id, descrption);
	}

	/**
	 * 
	 * @param cutoff
	 *            int , < 0 if no cutoff, >=0 the cutoff depth
	 * @param from
	 *            DBTreeNode
	 * @since 2005-03-11
	 */
	protected void buildTree(TypedTree tree, int cutoff, DBTreeNode from) {
		// if it's a null node
		if (from == null) {
			return;
		}
		if (cutoff != 0) {
			String ids = (String) from.getUserObject();
			Vector children = getChildren(ids);
			System.out.println(ids + " -> " + children);
			for (int i = 0; i < children.size(); i++) {
				String kid = (String) children.elementAt(i);
				DBTreeNode node = createNode(kid);

				from.add(node);

				buildTree(tree, cutoff - 1, node);
			}
		}
		return;
	}

	/**
	 * build a tree from database
	 * 
	 * @param from_id
	 *            String
	 * @param cutoff
	 *            int
	 * @return TypedTree
	 * 
	 * @author Jie Bao
	 * @since 2005-03-11
	 */
	public TypedTree getTree(String from_id, int cutoff) {
		if (from_id == null) {
			from_id = this.getRootId();
		}

		TypedTree tree = new TypedTree();

		DBTreeNode node = createNode(from_id);

		buildTree(tree, cutoff, node);
		tree.setTop(node);
		return tree;
	}
}
