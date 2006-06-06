package edu.iastate.ato.po ;

import edu.iastate.utils.string.ParserUtils ;

/**
 * @author Jie Bao
 * @since 2005-02-20
 *        2005-08-22 modified
 */
public class TagValuePair
{
    public String tag ;
    public String value ;

    public TagValuePair(String tag, String value)
    {
        this.tag = tag ;
        this.value = value ;
    }

    // 2005-08-24
    public String toString()
    {
        return tag + ((value == null) ? "" : ":" + value) ;
    }

    // 2005-08-22
    //  tag "value"
    // see http://www.geneontology.org/GO.format.shtml#dbxrefformat
    public String toXref()
    {
        if(value == null)
        {
            return tag ;
        }
        else
        {
            return tag + " \"" + value + "\"" ;
        }
    }

    static TagValuePair parseXref(String xref)
    {
        //def: "Interacting selectively with transfer RNA." [GO:ai]
        String def = ParserUtils.findFirst("\\\".*\\\"", xref) ; // find words in "  ...  "
        if(def != null)
        {
            def = def.substring(1, def.length() - 1) ;
        }
        //System.out.println("          " + def);
        String def_xref = ParserUtils.findFirst("\\[.*?\\]", xref) ; // find words in [ ..... ]
        if(def_xref != null)
        {
            def_xref = def_xref.substring(1, def_xref.length() - 1) ;
        }
        //System.out.println("          " + def_xref);
        return new TagValuePair(def, def_xref) ;
    }

    // parse obo format
    static TagValuePair parseTagValuePair(String str)
    {
        // information embedded in the comment
        if(str.startsWith(DB2OBO.PROPERTY_LEADING))
        {
            // @see DB2OBO.makeTerm_Property()
            str = str.substring(DB2OBO.PROPERTY_LEADING.length()) ;
        }

        // remove all words after '!' . they are comments
        int comments = str.indexOf("!") ;
        if(comments > 0)
        {
            str = str.substring(0, comments) ;
        }

        // replace all "'", since it will be used as database term limitier
        str = str.replaceAll("'", "\"") ;

        // split by the first ':'
        int limiter = str.indexOf(":") ;
        if(limiter > 0)
        {
            String tag = str.substring(0, limiter).trim() ;
            String value = str.substring(limiter + 1).trim() ;
            //System.out.println(str + " = " + tag + " + " + value);
            return new TagValuePair(tag, value) ;
        }
        else
        {
            return new TagValuePair(str, "") ;
        }
    }
}
