package edu.iastate.ato.po;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import edu.iastate.ato.shared.*;

/**
 * @author Jie Bao
 * @since 2005-08-15
 *
 * OBO file format guide : http://www.geneontology.org/GO.format.shtml
 */
public class DB2OBO extends LongTask
{
    public static final String           PROPERTY_LEADING = "![attribute]";
    public static final String           SCHEMA_LEADING   = "![schema]";

    //date: 15:08:2007 22:12
    final public static SimpleDateFormat dateFormat       = new SimpleDateFormat(
                                                              "dd:MM:yyyy HH:mm");

    public static String getTime()
    {
        return dateFormat.format(new Date());
    }

    String prefix;

    String makeHeader(String user)
    {
        String header = "format-version: 1.0\n" + "date: "
            + getTime() + "\n" + "saved-by: " + user + "\n"
            + "auto-generated-by: " + AtoConstent.APP_NAME + "\n"
            + "default-namespace: ato\n\n";
        return header;
    }

    /* eg
     [Typedef]
     id: part_of
     name: part of
     */
    String makeTypedef(String relation)
    {
        return "[Typedef]\n" + "id: " + relation + "\n" + "name: " + relation
            + "\n\n";
    }

    String makeComment(String comment)
    {
        return "comment: " + comment + "\n";
    }

    //unit: cm
    // @see TagValuePair.parseXref(), OBO2DB.addTermEntry()
    String makeTerm_Property(String prop, String value)
    {
        if (OntologySchema.isOBOProperty(prop))
        {
            if (prop.contains("synonym") || prop.equals("def"))
            {
                // e.g. related_synonym: "aa" []
                value = value.replaceAll("\"", "");
                return prop + ": \"" + value + "\" []\n";
            }
            else
            return prop + ": " + value + "\n";
        }
        else
        { // treate it as comment
            return PROPERTY_LEADING + prop + ": " + value + "\n";
        }
    }

    // 2005-08-24
    public String addPrefix(String id)
    {
        if (prefix != null && prefix.trim().length() > 0)
        {
            return prefix + ":" + id;
        }
        else
        {
            return id;
        }
    }

    // is_a: GO:0051532 ! regulation of NFAT protein-nucleus import
    String makeTerm_Relation(String relation, String parent_id,
        String parent_name)
    {
        String str = "relationship: " + relation + " " + addPrefix(parent_id);
        if (parent_name != null)
        {
            str += " ! " + parent_name;
        }
        return str + "\n";
    }

    // 2005-08-24
    String makePackageRelation(String relation, String parent_id,
        String parent_name)
    {
        String str = "relationship: " + relation + " " + parent_id;
        if (parent_name != null)
        {
            str += " ! " + parent_name;
        }
        return str + "\n";
    }

    /**
     * exportOBO
     *
     * @param db Connection
     * @param fileName String
     */
    public void exportOBO(Connection db, String fileName, String user,
        boolean withPrefix)
    {

        try
        {
            BufferedWriter out = new BufferedWriter(new FileWriter(fileName));

            // prepare data structures
            this.prefix = withPrefix ? OntologySchema.getPrefix(db) : null;
            packageNameCache.clear();

            // 1.  make header
            out.write(makeHeader(user));

            // 2. make schema
            exportSchema(db, out);

            // 3. export package
            exportPackages(db, out);

            // 4. write term by term
            exportTerms(db, out);

            out.close();
        }
        catch (IOException ex)
        {}
    }

    /**
     * exportPackages
     *
     * @param db Connection
     * @param out BufferedWriter
     * @since 2005-08-24
     */
    private void exportPackages(Connection db, BufferedWriter out)
            throws IOException
    {
        String sql = "SELECT oid, pid, comment, author, modified FROM package";
        try
        {
            Statement stmt = db.createStatement();
            ResultSet resultSet = stmt.executeQuery(sql);

            while (resultSet.next())
            {
                String oid = resultSet.getString("oid");
                String pid = resultSet.getString("pid");
                String comment = resultSet.getString("comment");
                String author = resultSet.getString("author");
                String modified = resultSet.getString("modified");

                // don't export GlobalPackage
                if (pid.equals(Package.GlobalPkg))
                {
                    continue;
                }

                out.write("[Package]\n");
                // write the comments
                String str = "id: " + pid + "\n";
                str += "name: " + (comment == null ? "" : comment) + "\n";
                out.write(str);
                str = makeTerm_Property("author", author);
                out.write(str);
                str = makeTerm_Property("modified", modified);
                out.write(str);

                //SELECT DISTINCT relation, package.pid AS parent, package.comment AS parent_name
                //FROM pkg_relation, package WHERE pkg_relation.p1 = '116329' AND pkg_relation.p2 = package.oid

                sql = "SELECT DISTINCT relation, package.pid AS parent, package.comment AS parent_name"
                    + " FROM pkg_relation, package WHERE pkg_relation.p1 = '"
                    + oid + "' AND pkg_relation.p2 = package.oid";
                //Debug.systrace(this,sql);
                Statement stmt2 = db.createStatement();
                ResultSet resultSet2 = stmt2.executeQuery(sql);
                while (resultSet2.next())
                {
                    String relation = resultSet2.getString("relation");
                    String parent = resultSet2.getString("parent");
                    String parent_name = resultSet2.getString("parent_name");

                    str = makePackageRelation(relation, parent, parent_name);
                    out.write(str);
                }
                out.write("\n");
            } // end of a package
        }
        catch (SQLException ex)
        {
            ex.printStackTrace();
        }

    }

    Map<String, String> packageNameCache = new HashMap();

    private String getPackageName(Connection db, String pkg_oid)
    {
        // read the cache
        String pkgName = packageNameCache.get(pkg_oid);
        if (pkgName == null)
        {
            // not yet in cache
            pkgName = DbPackage.read(db, pkg_oid).pid;
            packageNameCache.put(pkg_oid, pkgName);
        }
        return pkgName;
    }

    void exportTerms(Connection db, BufferedWriter out)
    {
        String sql = 
            "SELECT oid, id, name, slm, package, author, modified, is_obsolete FROM term";
        try
        {
            System.out.println(sql);

            Statement stmt = db.createStatement();
            ResultSet resultSet = stmt.executeQuery(sql);

            while (resultSet.next())
            {
                String oid = resultSet.getString("oid");
                String id = resultSet.getString("id");
                String name = resultSet.getString("name");
                String author = resultSet.getString("author");
                String modified = resultSet.getString("modified");
                String is_obsolete = resultSet.getString("is_obsolete");
                String package_oid = resultSet.getString("package");
                String slm = resultSet.getString("slm");

                out.write("[Term]\n");

                // write the basic information
                String str = "id: " + addPrefix(id) + "\n";
                str += "name: " + (name == null ? "" : name) + "\n";
                out.write(str);
                if (is_obsolete.equalsIgnoreCase("true"))
                {
                    str = "is_obsolete: true\n";
                    out.write(str);
                }
                str = makeTerm_Property("author", author);
                out.write(str);
                str = makeTerm_Property("modified", modified);
                out.write(str);
                // write package membership
                str = getPackageName(db, package_oid); // package id
                str = makeTerm_Property("package", str);
                out.write(str);
                str = makeTerm_Property("slm", slm);
                out.write(str);

                // write the property details
                sql = "SELECT attribute, value FROM details WHERE term = '"
                    + oid + "'";
                Statement stmt1 = db.createStatement();
                ResultSet resultSet1 = stmt1.executeQuery(sql);
                while (resultSet1.next())
                {
                    String attribute = resultSet1.getString("attribute");
                    String value = resultSet1.getString("value");

                    if (value != null && value.trim().length() > 0)
                    {
                        str = makeTerm_Property(attribute, value);
                        out.write(str);
                    }
                }

                //SELECT DISTINCT relation, term.id AS parent, term.name AS parent_name
                //FROM relation,term WHERE relation.id = '59056' AND relation.pid = term.oid
                sql = "SELECT DISTINCT relation, term.id AS parent, term.name AS parent_name FROM relation, term "
                    + "WHERE relation.id = '"
                    + oid
                    + "' AND relation.pid = term.oid";
                Statement stmt2 = db.createStatement();
                ResultSet resultSet2 = stmt2.executeQuery(sql);
                while (resultSet2.next())
                {
                    String relation = resultSet2.getString("relation");
                    String parent = resultSet2.getString("parent");
                    String parent_name = resultSet2.getString("parent_name");

                    str = makeTerm_Relation(relation, parent, parent_name);
                    out.write(str);
                }
                out.write("\n");

            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

    }

    // 2005-08-23
    private void exportSchema(Connection db, BufferedWriter out)
            throws IOException
    {
        // 1. write properties - if not obo property
        Vector<String> props = OntologySchema.getTermProperties(db);
        for (String attr : props)
        {
            // ![schema]term-property:unit
            String str = SCHEMA_LEADING + OntologySchema.TERM_PROPERTY + ":"
                + attr + "\n";
            out.write(str);

        }
        // 2. naming policy
        String value = OntologySchema.getNamingPolicy(db);
        if (value != null)
        {
            String str = SCHEMA_LEADING + OntologySchema.NAMING_POLICY + ":"
                + value + "\n";
            out.write(str);
        }

        // 3. prefix
        value = OntologySchema.getPrefix(db);
        if (value != null)
        {
            String str = SCHEMA_LEADING + OntologySchema.PREFIX + ":" + value
                + "\n";
            out.write(str);
        }

        // 4. a blank line
        out.write("\n");

        // 5. write partial orders
        // write typedef like [Typedef]\n  id: part_of \n name: part of
        Vector<String> relations = OntologySchema.getPartialOrders(db);
        for (String relation : relations)
        {
            String str = makeTypedef(relation);
            out.write(str);
        }

    }
}
