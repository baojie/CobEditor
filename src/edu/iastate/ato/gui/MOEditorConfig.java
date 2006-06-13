package edu.iastate.ato.gui ;

import javax.xml.transform.TransformerException ;

import org.apache.xpath.XPathAPI ;
import org.w3c.dom.Element ;
import org.w3c.dom.NodeList ;

import edu.iastate.utils.log.Config ;

/**
 * Configuration
 *
 * <p>@author Jie Bao</p>
 * <p>@since 2005-08-18</p>
 */
class MOEditorConfig extends Config
{
    private static final String tagConfig = "Config" ;
    Element root ;
    private final String SELECTED_ONT = "SelectedOntology" ;
    private final String ALL_ONTOLOGIES = "AllOntologies" ;

    public MOEditorConfig(String filename)
    {
        super(filename, false) ;
        try
        {
            root = (Element)XPathAPI.selectSingleNode(configXML, tagConfig) ;
        }
        catch(TransformerException ex)
        {
        }
    }

    protected void createNew()
    {
        createBlankNew() ;
        root = (Element)addChildTag(configXML, tagConfig) ;
    }

    protected void objToXML(Object obj)
    {
        MOEditor agent = (MOEditor)obj ;
        if(root != null)
        {
            root.getParentNode().removeChild(root) ;
        }
        root = (Element)addChildTag(configXML, tagConfig) ;

        String selected = (agent.selectedServer == null) ? "" :
            agent.selectedServer.name ;
        setProperty(root, SELECTED_ONT, selected) ;

        Element allOnt = (Element)addChildTag(root, ALL_ONTOLOGIES) ;

        for(OntologyServerInfo info : agent.serverList)
        {
            // Get key
            Element oneOntNode = info.toXML(allOnt) ;
            allOnt.appendChild(oneOntNode) ;
        }
    }

    protected void xmlToObj(Object obj)
    {
        MOEditor agent = (MOEditor)obj ;
        agent.serverList.clear() ;

        try
        {
            String selected = getProperty(root, SELECTED_ONT) ;
            Element allBBSNode = (Element)XPathAPI.selectSingleNode(root,
                ALL_ONTOLOGIES) ;
            if(allBBSNode != null)
            {
                NodeList nodelist = org.apache.xpath.XPathAPI.selectNodeList(
                    allBBSNode, OntologyServerInfo.ROOT) ;

                if(nodelist != null)
                {
                    // Process the elements in the nodelist
                    for(int i = 0 ; i < nodelist.getLength() ; i++)
                    {
                        // Get element
                        Element oneOntNode = (Element)nodelist.item(i) ;
                        OntologyServerInfo info = new OntologyServerInfo() ;
                        info.fromXML(oneOntNode) ;
                        agent.serverList.add(info) ;
                        if(info.name.compareTo(selected) == 0)
                        {
                            agent.selectedServer = info ;
                        }
                    }
                }
            }
        }
        catch(TransformerException ex)
        {
        }
    }
}
