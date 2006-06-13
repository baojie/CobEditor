package edu.iastate.utils.string;

import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Text with <tag></tag>
 * @author Jie Bao
 * @since 1.0 2004-10-17
 */

public class TaggedText
    implements CharSequence
{
    String tag;
    Vector content;
    String leafText = null;

    boolean isLeaf = false;

    public TaggedText()
    {
        try
        {
            jbInit();
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    public TaggedText(String leafText)
    {
        this.tag = null;
        this.content = null;
        this.leafText = leafText;
        this.isLeaf = true;
    }

    public TaggedText(String tag, Vector content)
    {
        this.tag = tag;
        this.content = (content == null) ? new Vector() : content;
        this.leafText = null;
        this.isLeaf = false;
    }

    public int length()
    {
        return toString().length();
    }

    public char charAt(int index)
    {
        return toString().charAt(index);
    }

    public CharSequence subSequence(int start, int end)
    {
        return toString().subSequence(start, end);
    }

    public String toString()
    {
        return internalToXML();
    }

    public void addChild(String tag, String value)
    {
        if (!isLeaf)
        {
            Vector newChildContent = new Vector();
            newChildContent.add(new TaggedText(value));
            TaggedText newChild = new TaggedText(tag, newChildContent);
            content.add(newChild);
        }
    }

    public void addChild(TaggedText newChild)
    {
        if (!isLeaf)
        {
            if (newChild.isLeaf) // there should be at most one child that is a leaf
            {
                // remove the existing leaf
                for (int i = 0; i < content.size(); i++)
                {
                    if ( ( (TaggedText) content.elementAt(i)).isLeaf)
                    {
                        content.remove(i);
                        break;
                    }
                }
            }
            content.add(newChild);
        }
    }

    public String toXML()
    {
        String preface = "<?xml version =\"1.0\" encoding=\"ISO-8859-1\"?>";
        return preface + internalToXML();
    }

    String internalToXML()
    {

        if (isLeaf)
        {
            return (leafText == null) ? "" : leafText;
        }
        else if (content == null)
        {
            return "<" + tag + "></" + tag + ">";
        }
        else
        {
            StringBuffer buf = new StringBuffer();
            buf.append("<" + tag + ">");
            for (int i = 0; i < content.size(); i++)
            {
                buf.append( ( (TaggedText) content.elementAt(i)).internalToXML());
            }
            buf.append("</" + tag + ">");
            return buf.toString();
        }
    }

    /**
     * return the first child that matches given name
     * @param name String
     * @return TaggedText
     * @since 2004-10-18
     */
    public TaggedText getChildByTag(String tag)
    {
        for (int i = 0; i < content.size(); i++)
        {
            TaggedText t = (TaggedText) content.elementAt(i);
            if (t.tag.equals(tag))
            {
                return t;
            }
        }
        return null;
    }

    public Vector getAllChildren()
    {
        return content;
    }

    /**
     * ATTENTION: parameter in tag is not allowed  eg. <table w=1></table>
     * @param inputStr CharSequence
     */
    public void fromXML(CharSequence inputStr)
    {
        //Debug.systrace(this, "Input: "+inputStr.toString());
        // Compile regular expression with a back reference to group 1

        //        String topPattern = "(?s)<(\\S+?).*?>(.*)</\\1>"; // this will allow parameter eg. <table w=1></table>
        // ATTENTION: known bug: <nodes><node></node><node></node></nodes> will be parsed as <node></node><node></node>


//        String topPattern = "(?s)<(\\S+?)>(.*)</\\1>";
        String topPattern = "(?s)<(\\S+?).*?>(.*)</\\1>";
        Pattern top = Pattern.compile(topPattern);
        Matcher matcher = top.matcher(inputStr);

        // Get tagname and contents of tag
        content = new Vector();
        boolean matchFound = matcher.find(); // true
        if (matchFound)
        {
            tag = (matcher.group(1)); // tag
            String nested = matcher.group(2);

            // build nested
            String nestedPattern = "(?s)<(\\S+?)>(.*?)</\\1>";
            Pattern nestedContent = Pattern.compile(nestedPattern);
            matcher = nestedContent.matcher(nested);

            isLeaf = false;
            boolean found = false;
            while (matcher.find())
            {
                TaggedText tt = new TaggedText();
                //Debug.systrace(this, "Match: "+matcher.group(0));
                tt.fromXML(matcher.group(0));
                addChild(tt);
                found = true;
            }
            // should be plain text
            if (!found)
            {
                content.add(new TaggedText(nested));
            }
        }
        else // plain text
        {
            this.tag = null;
            this.content = null;
            this.leafText = inputStr.toString();
            this.isLeaf = true;
        }
    }

    String getLeading(int times)
    {
        String leading = "";
        for (int i = 0; i < times; i++)
        {
            leading += "    ";
        }
        return leading;
    }

    public String print(int level)
    {
        if (isLeaf)
        {
            return getLeading(level) + leafText + "\n";
        }
        else
        {
            StringBuffer head = new StringBuffer();
            if (level == 0)
            {
                head.append(
                    "<?xml version =\"1.0\" encoding=\"ISO-8859-1\"?>\n");
            }

            head.append(getLeading(level) + "<" + tag + ">\n");

            for (int i = 0; i < content.size(); i++)
            {
                TaggedText tt = (TaggedText) content.elementAt(i);
                head.append(tt.print(level + 1));
            }
            head.append(getLeading(level) + "</" + tag + ">\n");
            return head.toString();
        }
    }

    private void jbInit() throws Exception
    {
    }

    public String getTag()
    {
        return tag;
    }

    public Vector getContent()
    {
        return content;
    }

    public static void test()
    {
        //String str = "<html>this<html> is     a  <b h=8>tes<b>ttes</b>ttesttest</b></html>     \r\n\n     \n<b>string</b></html>";
        //String str = "<type><typename>color</typename><subTypeOf>AVH</subTypeOf><root>color_AVH</root>\n<order><child>darkWhite</child><parent>White</parent></order><order><child>lightWhite</child><parent>White</parent></order><order><child>White</child><parent>color_AVH</parent></order><order><child>Blue</child><parent>color_AVH</parent></order><order><child>green</child><parent>color_AVH</parent></order></type>";
        //String str = "<top><level-1>1</level-1><level-1>2</level-1></top>";
        //String str = "<nodes><node>1</node><node>2</node></nodes>";
        String str =
            "<r><allnodes><node>1</node><node>2</node></allnodes><edge><no></no></edge></r>";
        //String str = "sfsdfadfasdf";
        //Vector list = convertToParagraphs(str);
//        Vector list = getNestdBlock("b", str, false);
        //str = (String) printXMLSkeleton(str);
//        TaggedText t1 = new TaggedText("top", new Vector());
//        t1.addChild("level-1", "1");
//        t1.addChild("level-1", "1");
//        t1.addChild(new TaggedText("int"));
//        t1.addChild(new TaggedText("ant"));

        TaggedText tt = new TaggedText();
        tt.fromXML(str);
        System.out.println(tt.print(0));
        System.out.println(tt);

    }

}
