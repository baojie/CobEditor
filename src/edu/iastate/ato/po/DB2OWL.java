package edu.iastate.ato.po ;

import java.io.BufferedWriter ;
import java.io.FileWriter ;
import java.io.IOException ;
import java.sql.Connection ;
import java.sql.ResultSet ;
import java.sql.SQLException ;
import java.sql.Statement ;
import java.util.Vector ;

/**
 * @author Jie Bao
 * @since 2005-08-15
 */
public class DB2OWL
{
    //<rdfs:comment rdf:datatype="http://www.w3.org/2001/XMLSchema#string" >It's A</rdfs:comment>
    static String makeComment(String comment)
    {
        return
            "  <rdfs:comment rdf:datatype=\"http://www.w3.org/2001/XMLSchema#string\" >" +
            comment + "</rdfs:comment>\n" ;
    }

    static String head =
        "<?xml version=\"1.0\"?>\n" +
        "<rdf:RDF\n" +
        "     xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\"\n" +
        "     xmlns:xsd=\"http://www.w3.org/2001/XMLSchema#\"\n" +
        "     xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\"\n" +
        "     xmlns:owl=\"http://www.w3.org/2002/07/owl#\"\n" +
        "     xmlns=\"http://www.owl-ontologies.com/unnamed.owl#\"\n" +
        "     xml:base=\"http://www.owl-ontologies.com/unnamed.owl\">\n" +
        "<owl:Ontology rdf:about=\"\"/>\n" +
        "<owl:Class rdf:ID=\"Term\"/>\n" +
        "<owl:DatatypeProperty rdf:ID=\"is_obsolete\">\n" +
        "      <rdfs:range rdf:resource=\"http://www.w3.org/2001/XMLSchema#boolean\"/>\n" +
        "      <rdfs:domain rdf:resource=\"#Term\"/>\n" +
        "</owl:DatatypeProperty>\n" ;
    ;

    static String tail = " </rdf:RDF>" ;

    //<owl:ObjectProperty rdf:ID="isa">
    //    <rdfs:domain rdf:resource="#Term"/>
    //</owl:ObjectProperty>
    static String makeSchema_Relation(String relation)
    {
        return "<owl:ObjectProperty rdf:ID=\"" + relation + "\">\n" +
            "  <rdfs:domain rdf:resource=\"#Term\"/>\n" +
            "</owl:ObjectProperty>\n" ;
    }

    //<owl:DatatypeProperty rdf:ID="unit">
    //    <rdfs:domain rdf:resource="#Term"/>
    //</owl:DatatypeProperty>
    static String makeSchema_Property(String prop)
    {
        return "<owl:DatatypeProperty rdf:ID=\"" + prop + "\">\n" +
            "  <rdfs:domain rdf:resource=\"#Term\"/>\n" +
            "</owl:DatatypeProperty>\n" ;
    }

    //<isa rdf:resource="#C"/>
    //<isa rdf:resource="#A"/>
    static String makeTerm_Relation(String relation, String parent)
    {
        return "  <" + relation + " rdf:resource=\"#" + parent + "\"/>\n" ;
    }

    //<unit rdf:datatype="http://www.w3.org/2001/XMLSchema#string">cm</unit>
    static String makeTerm_Property(String prop, String value)
    {
        return "  <" + prop +
            " rdf:datatype=\"http://www.w3.org/2001/XMLSchema#string\">" +
            value + "</" + prop + ">\n" ;
    }

    /**
     * exportOWL : example
     *
     <?xml version="1.0"?>
     <rdf:RDF
         xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#"
         xmlns:xsd="http://www.w3.org/2001/XMLSchema#"
         xmlns:rdfs="http://www.w3.org/2000/01/rdf-schema#"
         xmlns:owl="http://www.w3.org/2002/07/owl#"
         xmlns="http://www.owl-ontologies.com/unnamed.owl#"
       xml:base="http://www.owl-ontologies.com/unnamed.owl">
       <owl:Ontology rdf:about=""/>
       <owl:Class rdf:ID="Term"/>
       <owl:ObjectProperty rdf:ID="isa">
         <rdfs:domain rdf:resource="#Term"/>
       </owl:ObjectProperty>
       <owl:DatatypeProperty rdf:ID="unit">
         <rdfs:domain rdf:resource="#Term"/>
       </owl:DatatypeProperty>
       <Term rdf:ID="C"/>
       <Term rdf:ID="A">
         <unit rdf:datatype="http://www.w3.org/2001/XMLSchema#string"
         >cm</unit>
         <rdfs:comment rdf:datatype="http://www.w3.org/2001/XMLSchema#string"
         >It's A</rdfs:comment>
       </Term>
       <Term rdf:ID="B">
         <isa rdf:resource="#C"/>
         <isa rdf:resource="#A"/>
       </Term>
     </rdf:RDF>
     *
     * @param connection Connection
     * @param newfile String
     * @since 2005-08-15
     */
    public static void exportOWL(Connection db, String newfile)
    {
        try
        {

            //< owl:ObjectProperty rdf:ID = "isa" / >
            BufferedWriter out = new BufferedWriter(new FileWriter(newfile)) ;
            out.write(head) ;

            // 1. write the schema
            Vector<String> relations = OntologySchema.getPartialOrders(db) ;
            for(String relation : relations)
            {
                String str = makeSchema_Relation(relation) ;
                out.write(str) ;
            }

            Vector<String> properties = OntologySchema.getTermProperties(db) ;
            for(String prop : properties)
            {
                String str = makeSchema_Property(prop) ;
                out.write(str) ;
            }

            // 2. write term by term
            String sql =
                "SELECT oid, id, name, author, modified, is_obsolete FROM term" ;
            try
            {
                System.out.println(sql) ;

                Statement stmt = db.createStatement() ;
                ResultSet resultSet = stmt.executeQuery(sql) ;

                while(resultSet.next())
                {
                    String oid = resultSet.getString("oid") ;
                    String id = resultSet.getString("id") ;
                    String name = resultSet.getString("name") ;
                    String author = resultSet.getString("author") ;
                    String modified = resultSet.getString("modified") ;
                    String is_obsolete = resultSet.getString("is_obsolete") ;

                    out.write("<Term rdf:ID=\"" + id + "\">\n") ;

                    // write the comments
                    String str ;
                    if(name != null)
                    {
                        str = makeComment("Comments: " + name) ;
                        out.write(str) ;
                    }
                    if(is_obsolete.equalsIgnoreCase("true"))
                    {
                        str = "  <is_obsolete rdf:datatype=\"http://www.w3.org/2001/XMLSchema#boolean\">true</is_obsolete>\n" ;
                        out.write(str) ;
                    }

                    str = makeComment("author: " + author) ;
                    out.write(str) ;
                    str = makeComment("modified: " + modified) ;
                    out.write(str) ;

                    // write the property details
                    sql = "SELECT attribute, value FROM details WHERE term = '" +
                        oid + "'" ;
                    Statement stmt1 = db.createStatement() ;
                    ResultSet resultSet1 = stmt1.executeQuery(sql) ;
                    while(resultSet1.next())
                    {
                        String attribute = resultSet1.getString("attribute") ;
                        String value = resultSet1.getString("value") ;

                        str = makeTerm_Property(attribute, value) ;
                        out.write(str) ;
                    }

                    // SELECT DISTINCT relation, term.id AS parent FROM relation
                    // WHERE id = '59056' AND relation.pid = term.oid
                    sql =
                        "SELECT DISTINCT relation, term.id AS parent FROM relation " +
                        "WHERE id = '" + oid + "' AND relation.pid = term.oid" ;
                    Statement stmt2 = db.createStatement() ;
                    ResultSet resultSet2 = stmt2.executeQuery(sql) ;
                    while(resultSet2.next())
                    {
                        String relation = resultSet2.getString("relation") ;
                        String parent = resultSet2.getString("parent") ;

                        str = makeTerm_Relation(relation, parent) ;
                        out.write(str) ;
                    }

                    out.write("</Term>\n") ;
                }
            }
            catch(SQLException e)
            {
                System.err.println(sql) ;
                e.printStackTrace() ;
            }

            out.write(tail) ;
            out.close() ;

        }
        catch(IOException ex)
        {
        }
    }
}
