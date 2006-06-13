package edu.iastate.utils.lang;

import java.util.Vector;

interface Compare
{
    boolean lessThan(Object lhs, Object rhs);

    boolean lessThanOrEqual(Object lhs, Object rhs);
}

class StringCompare
    implements Compare
{
    public boolean lessThan(Object l, Object r)
    {
        return ( (String) l).toLowerCase().compareTo(
            ( (String) r).toLowerCase()) < 0;
    }

    public boolean
        lessThanOrEqual(Object l, Object r)
    {
        return ( (String) l).toLowerCase().compareTo(
            ( (String) r).toLowerCase()) <= 0;
    }
}

/**
 * @since 2005-04-24
 */
public class SortedVector
    extends Vector
{
    private Compare compare; // To hold the callback
    public SortedVector(Compare comp)
    {
        compare = comp;
    }
    public SortedVector()
    {
        compare = new StringCompare();
    }
    public void sort()
    {
        quickSort(0, size() - 1);
    }

    private void quickSort(int left, int right)
    {
        if (right > left)
        {
            Object o1 = elementAt(right);
            int i = left - 1;
            int j = right;
            while (true)
            {
                while (compare.lessThan(
                    elementAt(++i), o1))
                {
                    ;
                }
                while (j > 0)
                {
                    if (compare.lessThanOrEqual(
                        elementAt(--j), o1))
                    {
                        break; // out of while
                    }
                }
                if (i >= j)
                {
                    break;
                }
                swap(i, j);
            }
            swap(i, right);
            quickSort(left, i - 1);
            quickSort(i + 1, right);
        }
    }

    private void swap(int loc1, int loc2)
    {
        Object tmp = elementAt(loc1);
        setElementAt(elementAt(loc2), loc1);
        setElementAt(tmp, loc2);
    }
} ///:~
