package edu.iastate.ato.gui ;

import org.w3c.dom.Document ;
import org.w3c.dom.Element ;
import org.w3c.dom.Node ;

import edu.iastate.utils.log.Config ;

/**
 * @author Jie Bao
 * @since 1.0 2005-03-18
 */
public class OntologyServerInfo
{
    public String name = "" ;
    public String url = "" ;
    public String driver = "" ;
    public String type = "" ;
    public String user = "" ;
    public String password = "" ;
    public static final String ROOT = "Ontology" ;

    boolean loaded = false ;

    public String toString()
    {
        return name ;
    }

    public Element toXML(Node parent)
    {
        Document doc = parent.getOwnerDocument() ;
        Element oneOntNode = doc.createElement(ROOT) ;

        Config.setProperty(oneOntNode, "name", name) ;
        Config.setProperty(oneOntNode, "url", url) ;
        Config.setProperty(oneOntNode, "driver", driver) ;
        Config.setProperty(oneOntNode, "type", type) ;
        Config.setProperty(oneOntNode, "user", user) ;
        Config.setPropertyCipher(oneOntNode, "password", password) ;
        return oneOntNode ;
    }

    public void fromXML(Element oneOntNode)
    {
        name = Config.getProperty(oneOntNode, "name") ;
        url = Config.getProperty(oneOntNode, "url") ;
        driver = Config.getProperty(oneOntNode, "driver") ;
        type = Config.getProperty(oneOntNode, "type") ;
        user = Config.getProperty(oneOntNode, "user") ;
        password = Config.getPropertyCipher(oneOntNode, "password") ;
    }

    public static OntologyServerInfo getAtoOntology()
    {
        OntologyServerInfo info = new OntologyServerInfo() ;
        info.name = "Animal Trait Ontology" ;
        info.url = "jdbc:postgresql://boole.cs.iastate.edu/ato" ;
        info.type = "POSTGRE" ;
        info.driver = "org.postgresql.Driver" ;
        info.user = "ato" ;
        info.password = "ato" ;
        return info ;
    }
}
