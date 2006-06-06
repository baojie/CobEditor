package edu.iastate.ato.shared ;

/**
 * <p>@author Jie Bao</p>
 * <p>@since 2005-06-13</p>
 */
public interface AtoConstent
{
    public String packageTable = "package" ;

    public String pkgRelationTable = "pkg_relation" ;
    public String termTable = "term" ;
    public String termRelationTable = "relation" ;

    public static final String IMPORT = "import", NESTED_IN = "nested_in" ;
    public final static String APP_NAME = "Indus DAG editor" ;

    public static final String JDBC_DRIVER = "org.postgresql.Driver" ;

}
