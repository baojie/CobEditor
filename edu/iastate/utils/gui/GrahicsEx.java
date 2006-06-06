package edu.iastate.utils.gui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.Stroke;

/**
 * @author Jie Bao
 * @since 1.0 2004-10-31
 */

class MyPoint
{
    int x;
    int y;

    MyPoint(int p, int q)
    /*@Point_pre@*/
    {
        x = p;
        y = q;
    }

    /*@Point_post@*/

    MyPoint difference(MyPoint p)
    /*@difference_pre@*/
    {
        MyPoint result = new MyPoint(x - p.x, y - p.y);
        return result;
    }

    /*@difference_post@*/

    int counterClockwise(MyPoint p1, MyPoint p2)
    /*@counterClockwise_pre@*/
    {
        MyPoint d1 = p1.difference(this);
        MyPoint d2 = p2.difference(this);
        int result = 0;
        int s = d1.x * d2.y;
        int t = d1.y * d2.x;
        if (s > t)
        {
            result = +1;
        }
        else if (s < t)
        {
            result = -1;
        }
        else if ( (d1.x * d2.x < 0) || (d1.y * d2.y < 0))
        {
            result = -1;
        }
        else
        {
            int l1 = d1.x * d1.x + d1.y * d1.y;
            int l2 = d2.x * d2.x + d2.y * d2.y;
            if (l1 < l2)
            {
                result = +1;
            }
        }
        return result;
    }
    /*@counterClockwise_post@*/
}

class Line
{
    MyPoint a;
    MyPoint b;

    Line(MyPoint p, MyPoint q)
    /*@Line_pre@*/
    {
        a = p;
        b = q;
    }

    /*@Line_post@*/

    boolean intersects(Line l)
    /*@intersects_pre@*/
    {
        int ccw1 = a.counterClockwise(b, l.a);
        int ccw2 = a.counterClockwise(b, l.b);
        int t1 = ccw1 * ccw2;
        int ccw3 = l.a.counterClockwise(l.b, a);
        int ccw4 = l.a.counterClockwise(l.b, b);
        int t2 = ccw3 * ccw4;
        boolean result = (t1 <= 0) && (t2 <= 0);
        return result;
    }

    /*@intersects_post@*/

    boolean onLine(MyPoint p)
    /*@onLine_pre@*/
    {
        MyPoint d1 = p.difference(a);
        MyPoint d2 = b.difference(a);
        boolean colinear = (d1.y * d2.x == d1.x * d2.y);
        int l1 = d1.x * d1.x + d1.y * d1.y;
        int l2 = d2.x * d2.x + d2.y * d2.y;
        boolean result = colinear && (l1 <= l2);
        return result;
    }

    /*@onLine_post@*/

    static void demo()
    /*@demo_pre@*/
    {
        Line l1 = new Line(new MyPoint(2, 1), new MyPoint(5, 3));
        Line l2 = new Line(new MyPoint(0, 5), new MyPoint(5, 0));
        boolean i12a = l1.intersects(l2);
        boolean i12b = l2.intersects(l1);

        Line l3 = new Line(new MyPoint(1, 1), new MyPoint( -10, -8));
        Line l4 = new Line(new MyPoint( -5, 0), new MyPoint(0, 5));
        boolean i34a = l3.intersects(l4);
        boolean i34b = l4.intersects(l3);

        Line l5 = new Line(new MyPoint(1, 1), new MyPoint(2, 2));
        Line l6 = new Line(new MyPoint(3, 3), new MyPoint(5, 5));
        boolean i56a = l5.intersects(l6);
        boolean i56b = l6.intersects(l5);

        Line l7 = new Line(new MyPoint(1, 1), new MyPoint(4, 4));
        Line l8 = new Line(new MyPoint(3, 3), new MyPoint(5, 5));
        boolean i78a = l7.intersects(l8);
        boolean i78b = l8.intersects(l7);
    }
    /*@demo_post@*/
}

public class GrahicsEx
{
    static public int SOLID = 1;
    static public int DASHED = 2;

    static class Setting
    {
        Color oldColor;
        Stroke oldStroke;
        Font oldFont;
    }

    /**
     * Enhanced line drawer
     * @param g Graphics
     * @param x1 int
     * @param y1 int
     * @param x2 int
     * @param y2 int
     * @param c Color
     * @param thickness int
     * @param MODE int
     *
     * @author Jie Bao
     * @since 2004-10-31
     */
    static public void drawLine(Graphics g, int x1, int y1, int x2, int y2,
                                Color c, int thickness, int MODE)
    {
        Setting oldSetting = setSetting(g, c, thickness, MODE);
        g.drawLine(x1, y1, x2, y2);
        restoreSetting(g, oldSetting);

    }

    /**
     * Enhanced rectangle drawer
     * @param g Graphics
     * @param x int
     * @param y int
     * @param width int
     * @param height int
     * @param c Color
     * @param thickness int
     * @param MODE int
     *
     * @author Jie Bao
     * @since 2004-10-31
     */
    static public void drawRect(Graphics g, int x1, int y1, int x2, int y2,
                                Color c, int thickness, int MODE)
    {
        Setting oldSetting = setSetting(g, c, thickness, MODE);
        g.drawLine(x1, y1, x1, y2);
        g.drawLine(x1, y2, x2, y2);
        g.drawLine(x2, y2, x2, y1);
        g.drawLine(x2, y1, x1, y1);
        restoreSetting(g, oldSetting);
    }

    static Setting setSetting(Graphics g, Color c, int thickness, int MODE)
    {
        Graphics2D g2d = (Graphics2D) g;
        Setting oldSetting = new Setting();

        oldSetting.oldColor = g.getColor();
        oldSetting.oldStroke = g2d.getStroke();

        if (thickness > 0)
        {
            Stroke solid_line = new BasicStroke(thickness);
            Stroke dashed_line =
                new BasicStroke(
                    thickness, // width
                    BasicStroke.CAP_BUTT, // the decoration of the ends
                    BasicStroke.JOIN_BEVEL, //  the decoration applied where path segments meet
                    0, //  the limit to trim the miter join. The miterlimit must be greater than or equal to 1.0f.
                    new float[]
                    {9}, //  the array representing the dashing pattern
                    0 // the offset to start the dashing pattern
                );
            if (MODE == GrahicsEx.SOLID)
            {
                g2d.setStroke(solid_line);
            }
            else if (MODE == GrahicsEx.DASHED)
            {
                g2d.setStroke(dashed_line);
            }
        }
        if (c != null)
        {
            g.setColor(c);
        }
        return oldSetting;
    }

    static void restoreSetting(Graphics g, Setting oldSetting)
    {
        Graphics2D g2d = (Graphics2D) g;
        // restore old setting
        if (oldSetting.oldColor != null)
        {
            g.setColor(oldSetting.oldColor);
        }
        if (oldSetting.oldStroke != null)
        {
            g2d.setStroke(oldSetting.oldStroke);
        }
    }

    public static boolean isOverlap(Point box1, Point box2, Point line1,
                                    Point line2)
    {
        MyPoint b1 = new MyPoint(box1.x, box1.y);
        MyPoint b2 = new MyPoint(box1.x, box2.y);
        MyPoint b3 = new MyPoint(box2.x, box1.y);
        MyPoint b4 = new MyPoint(box2.x, box2.y);

        Line e1 = new Line(b1, b2);
        Line e2 = new Line(b2, b3);
        Line e3 = new Line(b3, b4);
        Line e4 = new Line(b4, b1);

        MyPoint l1 = new MyPoint(line1.x, line1.y);
        MyPoint l2 = new MyPoint(line2.x, line2.y);
        Line l = new Line(l1, l2);

        boolean overlap1 = l.intersects(e1);
        boolean overlap2 = l.intersects(e2);
        boolean overlap3 = l.intersects(e3);
        boolean overlap4 = l.intersects(e4);

        return overlap1 || overlap2 || overlap3 || overlap4;
    }

    public static boolean contains(Point box1, Point box2, Point pt)
    {
        Polygon p = new Polygon();
        p.addPoint(box1.x, box1.y);
        p.addPoint(box1.x, box2.y);
        p.addPoint(box2.x, box1.y);
        p.addPoint(box2.x, box2.y);

        return p.contains(pt);
    }
}
