package edu.iastate.utils.gui;

import java.util.Vector;
import java.util.Comparator;

/**
 * This comparator is used to sort vectors of data
 * @author Jie Bao
 * @since 2003
 *
 */
public class TableColumnSorter
    implements Comparator
{
    int colIndex;
    boolean ascending;
    public TableColumnSorter(int colIndex, boolean ascending)
    {
        this.colIndex = colIndex;
        this.ascending = ascending;
    }

    public int compare(Object a, Object b)
    {
        Vector v1 = (Vector) a;
        Vector v2 = (Vector) b;
        Object o1 = v1.get(colIndex);
        Object o2 = v2.get(colIndex);

        // Treat empty strains like nulls
        if (o1 instanceof String && ( (String) o1).length() == 0)
        {
            o1 = null;
        }
        if (o2 instanceof String && ( (String) o2).length() == 0)
        {
            o2 = null;
        }

        // Sort nulls so they appear last, regardless
        // of sort order
        if (o1 == null && o2 == null)
        {
            return 0;
        }
        else if (o1 == null)
        {
            return 1;
        }
        else if (o2 == null)
        {
            return -1;
        }
        else if (o1 instanceof Comparable)
        {
            if (ascending)
            {
                return ( (Comparable) o1).compareTo(o2);
            }
            else
            {
                return ( (Comparable) o2).compareTo(o1);
            }
        }
        else
        {
            if (ascending)
            {
                return o1.toString().compareTo(o2.toString());
            }
            else
            {
                return o2.toString().compareTo(o1.toString());
            }
        }
    }
}
