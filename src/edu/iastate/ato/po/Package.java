package edu.iastate.ato.po ;

/**
 * <p>@author Jie Bao</p>
 * <p>@since 2005-06-07</p>
 */

/**
 CREATE TABLE package
 (
   pid varchar(255) NOT NULL,
   "comment" varchar(256),
   author varchar(255),
   modified varchar(32),
   CONSTRAINT package_pkey PRIMARY KEY (pid)
 )
 WITH OIDS;
 */
public class Package
{
    public static final String PUBLIC = "public", PROTECTED = "protected",
        PRIVATE = "private" ;
    public final static String GlobalPkg = "Global" ;
}
