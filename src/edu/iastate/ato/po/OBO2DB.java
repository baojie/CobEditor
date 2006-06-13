package edu.iastate.ato.po ;

import java.io.BufferedReader ;
import java.io.File ;
import java.io.FileReader ;
import java.io.IOException ;
import java.sql.Connection ;
import java.text.DecimalFormat ;
import java.text.NumberFormat ;
import java.util.HashMap ;
import java.util.HashSet ;
import java.util.Iterator ;
import java.util.Map ;
import java.util.Set ;
import java.util.Vector ;

import edu.iastate.utils.sql.JDBCUtils ;
import edu.iastate.ato.shared.* ;

/**
 * Parse a obo file and import the ontology into database
 * @author Jie Bao
 * @since 2005-08-21
 */
public class OBO2DB extends LongTask
{
    Connection db ;
    String user ;
    Map<String, String> packageOidCache = new HashMap() ;
    Set<String> unknownLabels = new HashSet() ;

    String global_oid ;

    public OBO2DB(Connection db, String user)
    {
        this.db = db ;
        this.user = user ;
        this.global_oid = getPackageOid(Package.GlobalPkg) ; // initialize global package oid
        System.out.println("global_oid: " + global_oid) ;
    }

    String tempUser = "$tempuser!!!$" ;

    /**
     * importOBO

     // parsing a OBO file
     // the OBO format is explained at http://www.geneontology.org/GO.format.shtml#oboflat
     /* example of OBO file:
                                           format-version: GO_1.0
                                           !any comment here
                                           typeref: relationship.types
                                           subsetdef: goslim "Generic GO Slim"
                                           version: $Revision: 1.1 $
                                           date: April 18th, 2003
                                           saved-by: jrichter
                                           remark: Example file

                                           [Term]
                                           id: GO:0003674
                                           name: molecular_function
            def: "The action characteristic of a gene product." [GO:curators]
                                           subset: goslim

                                           [Term]
                                           id: GO:0016209
                                           name: antioxidant activity
                                          is_a: GO:0003674
      def: "Inhibition of the reactions brought about by dioxygen or peroxides. \
      Usually the antioxidant is effective because it can itself be more easily \
          oxidized than the substance protected. The term is often applied to \
         components that can trap free radicals, thereby breaking the chain \
         reaction that normally leads to extensive biological damage." \
                                          [ISBN:0198506732]

      * @param connection Connection
      * @param fileName String
      * @param string String
      * @since 2005-08-21
      */
     public boolean importOBO(String fileName, boolean filterPrefix)
     {
         NumberFormat precent = new DecimalFormat("00.00") ;

         try
         {
             // clear all cache;
             packageOidCache.clear() ;
             unknownLabels.clear() ;
             prefix = null ;

             // add obo ontology schema
             OntologySchema.initOBOSchema(db) ;

             // that could be a large file, so we read it line by line
             File theFile = new File(fileName) ;
             long fileSize = theFile.length() ;
             System.out.println("File size " + fileSize) ;
             long totalRead = 0 ;

             BufferedReader in = new BufferedReader(new FileReader(theFile)) ;
             String default_namespace = "Unknown" ;

             // parse it
             // 1. read header section
             Stanza section = Stanza.readStanza(in) ;
             totalRead += section.totalSize ;
             System.err.println(section.stanza) ;

             if(section == null || section.getType() != Stanza.HEAD)
             {
                 return false ;
             }

             for(String str : section.stanza)
             {
                 TagValuePair tagvalue = TagValuePair.parseTagValuePair(str) ;
                 if(tagvalue.tag.compareTo("default_namespace") == 0)
                 {
                     default_namespace = tagvalue.value ;
                 }
                 //addHeadEntry(tagvalue.tag, tagvalue.value);
             }

             // 2. read schema
             section = Stanza.readStanza(in) ;
             totalRead += section.totalSize ;

             int count = 0 ;

             while(section != null)
             {
                 count++ ;
                 if(count == 500)
                 {
                    // break ;
                 }

                 System.err.println(section.stanza) ;
                 if(section.getType() == Stanza.SCHEMA)
                 {
                     Vector <TagValuePair> allPairs = new Vector<TagValuePair>();
                     for(String str : section.stanza)
                     {
                         int leading = DB2OBO.SCHEMA_LEADING.length() ;

                         if(str.startsWith(DB2OBO.SCHEMA_LEADING))
                         {
                             // filter the leading string
                             str = str.substring(leading) ;
                             TagValuePair t = TagValuePair.parseTagValuePair(
                                 str) ;
                             //OntologySchema.addPredicate(db, t.tag, t.value) ;
                             allPairs.add(t);
                             if(t.tag.equals(OntologySchema.PREFIX))
                             {
                                 prefix = t.value ;
                             }
                         }
                     }
                     OntologySchema.addPredicateBacth(db,allPairs);

                     if(!filterPrefix)
                     {
                         prefix = null ;
                     }
                 }
                 else if(section.getType() == Stanza.PACKAGE)
                 {
                     addPackageEntry(section.stanza) ;
                 }
                 else if(section.getType() == Stanza.TERM)
                 {
                     addTermEntry(section.stanza, default_namespace) ;
                 }
                 else if(section.getType() == Stanza.TYPEDEF)
                 {
                     addTypedefEntry(section.stanza) ;
                 }

                 section = Stanza.readStanza(in) ;
                 totalRead = (section == null) ? fileSize :
                     totalRead + section.totalSize ;

                 String info = (fileName + " " +
                     precent.format(100.00 * totalRead / fileSize) +
                     "% finished (" + totalRead + "/" + fileSize + ")") ;
                 updateProgress(info) ;

             }

             // 4. add unknown labels as term properties
             // get current properties
             Vector v = OntologySchema.getTermProperties(db) ;
             // remove existing ones
             this.unknownLabels.removeAll(v) ;
             // add them as properties
             for(String label : unknownLabels)
             {
                 OntologySchema.addPredicate(db, OntologySchema.TERM_PROPERTY,
                     label) ;
             }

             // close the stream
             in.close() ;

             // update relation table -
             String sql = "UPDATE relation SET pid = term.oid " +
                 " WHERE relation.pid = term.id AND " +
                 " relation.author = '" + this.tempUser + "';" ;
             sql += "UPDATE relation SET author = '" + user +
                 "' WHERE author = '" + tempUser + "'" ;
             JDBCUtils.updateDatabase(db, sql) ;
             return true ;
         }
         catch(Exception ex)
         {
             ex.printStackTrace() ;
         }
         return false ;
     }

    /**
     * Add a typdef item
     * eg:
     *    [Typedef]
     *    id: part_of
     *    name: part of
     *    is_transitive: true
     *
     * @param strings Vector
     * @return boolean
     * @since 2005-02-20
     *        modified 2005-08-21
     */
    boolean addTypedefEntry(Vector strings)
    {
        String id = null, name = null, domain = "", range = "",
            is_cyclic = "false", is_transitive = "false",
            is_symmetric = "false" ;
        Iterator it = strings.iterator() ;
        while(it.hasNext())
        {
            String str = (String)it.next() ;
            TagValuePair tagvalue = TagValuePair.parseTagValuePair(str) ;
            //System.out.println(tagvalue.tag);
            if(tagvalue.tag.compareTo("id") == 0)
            {
                //id: part_of
                id = tagvalue.value ;
            }
            else if(tagvalue.tag.compareTo("name") == 0)
            {
                //name: part of
                name = tagvalue.value ;
            }
            else if(tagvalue.tag.compareTo("is_transitive") == 0)
            {
                //is_transitive: true
                is_transitive = tagvalue.value ;
            }
            else if(tagvalue.tag.compareTo("is_symmetric") == 0)
            {
                //is_transitive: true
                is_symmetric = tagvalue.value ;
            }
            else if(tagvalue.tag.compareTo("is_cyclic") == 0)
            {
                //is_transitive: true
                is_cyclic = tagvalue.value ;
            }
            else if(tagvalue.tag.compareTo("domain") == 0)
            {
                //is_transitive: true
                domain = tagvalue.value ;
            }
            else if(tagvalue.tag.compareTo("range") == 0)
            {
                //is_transitive: true
                range = tagvalue.value ;
            }
        }

        // table schema
        return OntologySchema.addPartialOrders(db, id) ;
    }

    // 2005-08-24
    boolean addPackageEntry(Vector<String> strings)
    {
        String package_id = null, name = null, author = null, modified = null ;
        Vector<TagValuePair> pkgRelations = new Vector<TagValuePair>() ;
        for(String str : strings)
        {
            TagValuePair tagvalue = TagValuePair.parseTagValuePair(str) ;
            if(tagvalue.tag.compareTo("id") == 0)
            {
                package_id = tagvalue.value ;
            }
            else if(tagvalue.tag.compareTo("name") == 0)
            {
                name = tagvalue.value ;
            }
            else if(tagvalue.tag.compareTo("author") == 0)
            {
                author = tagvalue.value ;
            }
            else if(tagvalue.tag.compareTo("modified") == 0)
            {
                modified = tagvalue.value ;
            }
            else if(tagvalue.tag.compareTo("relationship") == 0)
            {
                // eg: relationship: nest_in Global
                String s[] = tagvalue.value.split("\\s") ;
                if(s.length == 2)
                {
                    pkgRelations.add(new TagValuePair(s[0], s[1])) ; // it includes two words!
                }
            }
            else // unknown tags
            {}
        } // end of for

        // save to database
        if(package_id == null)
        {
            return false ;
        }
        String oid = getPackageOid(package_id) ;
        DbPackage ppp = new DbPackage(oid, package_id, name, author, modified) ;
        boolean suc = ppp.write(db, author, true) ;

        if(suc)
        {
            // write relations
            for(TagValuePair pair : pkgRelations)
            {
                String relation = pair.tag ;
                String parent_id = pair.value ;
                String parent_oid = getPackageOid(parent_id) ;
                OntologyEdit.addPackageRelation(db, oid, relation, parent_oid,
                    user) ;
            }
        }

        return suc ;
    }

    /**
     * Parse a term entry
     *
     * @param strings Vector - line of this term entry
     * @param default_namespace String
     * @return boolean
     * @since 2005-02-18
     */
    boolean addTermEntry(Vector strings, String default_namespace)
    {
        Iterator it = strings.iterator() ;

        String term_id = null, name = null, namespace = default_namespace,
            def = null, def_xref = null, comment = null, is_obsolete = "false" ;
        Vector<String> alt_id = new Vector<String>() ; // of string(7)
        Vector<String> subset = new Vector<String>() ; // of string(50)
        Vector<TagValuePair> synonym = new Vector<TagValuePair>() ; // of TagValuePair
        Vector<TagValuePair> related_synonym = new Vector<TagValuePair>() ; // ditto
        Vector<TagValuePair> exact_synonym = new Vector<TagValuePair>() ; // ditto
        Vector<TagValuePair> broad_synonym = new Vector<TagValuePair>() ; // ditto
        Vector<TagValuePair> narrow_synonym = new Vector<TagValuePair>() ; // ditto
        Vector<String> xref_analog = new Vector<String>() ; //of string
        Vector<String> xref_unknown = new Vector<String>() ; // of string
        Vector<TagValuePair> is_a = new Vector<TagValuePair>() ; // of TagValuePair
        Vector<TagValuePair> relationship = new Vector<TagValuePair>() ; //of TagValuePair
        Vector<TagValuePair> use_term = new Vector<TagValuePair>() ; // of TagValuePair
        Vector<TagValuePair> unknown_tag = new Vector<TagValuePair>() ;
        String author = user, modified = "", package_id = null ;
        String slm = Package.PUBLIC ;

        // parse it
        while(it.hasNext())
        {
            String str = (String)it.next() ;
            TagValuePair tagvalue = TagValuePair.parseTagValuePair(str) ;
            //System.out.println(tagvalue.tag);
            if(tagvalue.tag.compareTo("id") == 0)
            {
                // eg: id: GO:0000041
                term_id = filterID(tagvalue.value) ;
            }
            else if(tagvalue.tag.compareTo("name") == 0)
            {
                //eg: name: transition metal ion transport
                name = tagvalue.value ;
            }
            else if(tagvalue.tag.compareTo("alt_id") == 0)
            {
                // alt_id: GO:0006594
                alt_id.add(filterID(tagvalue.value)) ;
            }
            else if(tagvalue.tag.compareTo("namespace") == 0)
            {
                //namespace: biological_process
                namespace = tagvalue.value ;
            }
            else if(tagvalue.tag.compareTo("def") == 0)
            {
                //def: "Interacting selectively with transfer RNA." [GO:ai]
                TagValuePair t = TagValuePair.parseXref(tagvalue.value) ;
                def = t.tag ;
                def_xref = t.value ;
            }
            else if(tagvalue.tag.compareTo("comment") == 0)
            {
                // comment: This term was made ...
                comment = tagvalue.value ;
            }

            else if(tagvalue.tag.compareTo("subset") == 0)
            {
                // subset: gosubset_prok
                subset.add(tagvalue.value) ;
            }
            else if(tagvalue.tag.compareTo("synonym") == 0)
            {
                //  synonym: "The Bug" [VEH:391840]
                TagValuePair t = TagValuePair.parseXref(tagvalue.value) ;
                synonym.add(t) ;

            }
            else if(tagvalue.tag.compareTo("related_synonym") == 0)
            {
                //related_synonym: "Type 1" []
                TagValuePair t = TagValuePair.parseXref(tagvalue.value) ;
                related_synonym.add(t) ;

            }
            else if(tagvalue.tag.compareTo("exact_synonym") == 0)
            {
                //  exact_synonym: "VW Bug" [VW:0283, TPT:938VWB]
                TagValuePair t = TagValuePair.parseXref(tagvalue.value) ;
                exact_synonym.add(t) ;
            }
            else if(tagvalue.tag.compareTo("broad_synonym") == 0)
            {
                //broad_synonym: "glutamine amidotransferase\:cyclase" []
                TagValuePair t = TagValuePair.parseXref(tagvalue.value) ;
                broad_synonym.add(t) ;
            }
            else if(tagvalue.tag.compareTo("narrow_synonym") == 0)
            {
                //narrow_synonym: "TRAP complex" []
                TagValuePair t = TagValuePair.parseXref(tagvalue.value) ;
                narrow_synonym.add(t) ;
            }
            else if(tagvalue.tag.compareTo("xref_analog") == 0)
            {
                //xref_analog: EC:3.1.3.21
                xref_analog.add(tagvalue.value) ;
            }
            else if(tagvalue.tag.compareTo("xref_unknown") == 0)
            {
                // xref_unknown: EC:1.14.13.78
                xref_unknown.add(tagvalue.value) ;
            }
            else if(tagvalue.tag.compareTo("is_a") == 0)
            {
                //is_a: GO:0006118 ! electron transport
                TagValuePair t = new TagValuePair("is_a",
                    filterID(tagvalue.value)) ;
                //System.out.println(t.tag+":"+t.value);
                is_a.add(t) ;
            }
            else if(tagvalue.tag.compareTo("relationship") == 0)
            {
                // eg: relationship: part_of GO:0016236 ! macroautophagy
                String s[] = tagvalue.value.split("\\s") ;
                if(s.length == 2)
                {
                    relationship.add(new TagValuePair(s[0], filterID(s[1]))) ; // it includes two words!
                }
            }
            else if(tagvalue.tag.compareTo("is_obsolete") == 0)
            {
                // is_obsolete: true
                is_obsolete = tagvalue.value ;
            }
            else if(tagvalue.tag.compareTo("use_term") == 0)
            {
                TagValuePair t = new TagValuePair("use_term",
                    filterID(tagvalue.value)) ;
                use_term.add(t) ;
            }
            else if(tagvalue.tag.compareTo("author") == 0)
            {
                author = tagvalue.value ;
            }
            else if(tagvalue.tag.compareTo("modified") == 0)
            {
                modified = tagvalue.value ;
            }
            else if(tagvalue.tag.compareTo("package") == 0)
            {
                package_id = tagvalue.value ;
            }
            else if(tagvalue.tag.compareTo("slm") == 0)
            {
                slm = tagvalue.value ;
            }
            else if(tagvalue.tag.compareTo("[Term]") == 0)
            {}
            else if(tagvalue.tag.trim().length() == 0)
            {}
            else
            { // unsupportted tags
                // add as term property
                unknownLabels.add(tagvalue.tag) ;
                unknown_tag.add(tagvalue) ;
            }

        } // while

        // write to database
        if(term_id == null || name == null)
        {
            return false ;
        }
        else
        {
            // insert information into database
            //System.out.println(term_id) ;

            // get package oid
            if(package_id == null)
            {
                package_id = namespace ;
            }
            String pkg_oid = getPackageOid(package_id) ;
            if(pkg_oid == null)
            {
                return false ;
            }
            // save the term
            String term_oid = OntologyEdit.addTerm(db, term_id, name, pkg_oid,
                slm, is_obsolete, author, modified) ;
            if(term_oid == null)
            {
                return false ;
            }

            // def
            OntologyEdit.addTermProperty(db, term_oid, "def", def, user) ;
            // def_xref
            OntologyEdit.addTermProperty(db, term_oid, "def_xref", def_xref,
                user) ;
            // comment
            OntologyEdit.addTermProperty(db, term_oid, "comment", comment, user) ;

            // alt_id
            for(String item : alt_id)
            {
                OntologyEdit.addTermProperty(db, term_oid, "alt_id",
                    item, user) ;
            }

            // subset
            for(String item : subset)
            {
                OntologyEdit.addTermProperty(db, term_oid, "subset",
                    item, user) ;
            }

            // go_synonym
            addSynonym(term_oid, "synonym", synonym) ;
            addSynonym(term_oid, "related_synonym", related_synonym) ;
            addSynonym(term_oid, "exact_synonym", exact_synonym) ;
            addSynonym(term_oid, "broad_synonym", broad_synonym) ;
            addSynonym(term_oid, "narrow_synonym", narrow_synonym) ;

            //  xref_analog
            for(String item : xref_analog)
            {
                OntologyEdit.addTermProperty(db, term_oid, "xref_analog",
                    item, user) ;
            }

            // xref_unknow
            for(String item : xref_unknown)
            {
                OntologyEdit.addTermProperty(db, term_oid, "xref_unknown",
                    item, user) ;
            }

            // go_relationship
            addRelationship(term_oid, is_a) ; //is_a: GO:0006118
            addRelationship(term_oid, relationship) ; // part_of GO:0016236

            // use_term
            for(TagValuePair t : use_term)
            {
                OntologyEdit.addTermProperty(db, term_oid, t.tag, t.value, user) ;
            }

            // unknown_tag - unstandard term property
            for(TagValuePair t : unknown_tag)
            {
                OntologyEdit.addTermProperty(db, term_oid, t.tag, t.value, user) ;
            }

            //System.err.println("End of term entry") ;

            return true ;
        }
    }

    /**
     * Read or create package
     *
     * @param namespace String
     * @since 2005-08-22
     */
    private String getPackageOid(String namespace)
    {
        // read the cache
        String oid = packageOidCache.get(namespace) ;
        if(oid == null)
        { // not yet in cache
            oid = OntologyQuerier.getPackageOid(db, namespace) ;
            if(oid == null)
            { // not yet created
                // create it
                oid = OntologyEdit.addPackage(db, namespace, namespace, user) ;
                // add it under global package
                OntologyEdit.addPackageNesting(db, oid, global_oid, user) ;
            }
            packageOidCache.put(namespace, oid) ;
        }
        return oid ;
    }

    /**
     *
     * @param term_oid String
     * @param source_id Vector - id, NOT oid, because we don't know it at this moment
     */
    void addRelationship(String term_oid, Vector<TagValuePair> source_id)
    {
        for(TagValuePair t : source_id)
        {
            OntologyEdit.addTermRelation(db, term_oid, t.tag, t.value, tempUser) ;
        }
    }

    /**
     * @param go_id String
     * @param type String   "synonym", "related_synonym", "exact_synonym",
     *                      "broad_synonym",  "narrow_synonym"
     * @param source Vector
     */
    void addSynonym(String term_oid, String type, Vector<TagValuePair> source)
    {
        for(int i = 0 ; i < source.size() ; i++)
        {
            TagValuePair t = source.elementAt(i) ;
            OntologyEdit.addTermProperty(db, term_oid, type, t.toXref(), user) ;
        }
    }

    /**
     *
     * @param id String eg: GO:0012509
     * @return String eg:  0012509
     */
    String filterID(String id)
    {
        if(prefix == null)
        {
            return id ;
        }
        else
        {
            return id.replaceAll(prefix + ":", "") ;
        }
    }

    String prefix = null ;
}

class Stanza
{
    public final static short EMPTY = 0 ;
    public final static short UNKNOWN = 1 ;
    public final static short HEAD = 2 ;
    public final static short SCHEMA = 3 ;
    public final static short PACKAGE = 4 ;
    public final static short TERM = 5 ;
    public final static short TYPEDEF = 6 ;

    public short getType()
    {
        if(stanza.size() == 0)
        {
            return EMPTY ;
        }
        String first = stanza.elementAt(0) ;
        if(first.startsWith("format-version:"))
        {
            return HEAD ;
        }
        else if(first.startsWith("![schema]"))
        {
            return SCHEMA ;
        }
        else if(first.startsWith("[Package]"))
        {
            return PACKAGE ;
        }
        else if(first.startsWith("[Term]"))
        {
            return TERM ;
        }
        else if(first.startsWith("[Typedef]"))
        {
            return TYPEDEF ;
        }
        else
        {
            return UNKNOWN ;
        }

    }

    Vector<String> stanza = new Vector<String>() ;
    int totalSize ;

    // 2005-08-20
    static Stanza readStanza(BufferedReader in) throws IOException
    {
        boolean findText = false ;
        Stanza s = new Stanza() ;

        String str = in.readLine() ;
        while(str != null)
        {
            s.totalSize += str.length() + "\n".length() ;
            if(str.trim().length() == 0) // blank
            {
                if(findText) // already find something
                {
                    // end of the stanza
                    return s ;
                }
                else // still find nothing
                {
                    // skip all empty lines
                    continue ; // keep reading
                }
            }
            else // find text
            {
                findText = true ;
                s.stanza.add(str) ;
            }
            str = in.readLine() ;
        }

        if(s.stanza.size() == 0) // no text
        {
            return null ;
        }

        return s ;
    }
}
