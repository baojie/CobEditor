package edu.iastate.ato.tree ;

/**
 * <p>@author Jie Bao</p>
 * <p>@since </p>
 */
public class DBTermCloneNode extends DbTermNode
{
    public DbTermNode sourceNode ;

    public DBTermCloneNode(DbTermNode fromNode)
    {
        super(fromNode.thisTerm, fromNode.getHomePackageNode()) ;
        this.sourceNode = fromNode ;
        if(fromNode.status == this.DELETED_NODE)
        {
            this.status = fromNode.status ;
        }
        fromNode.addCloned(this) ;
    }

    public void updateSourceNode(DbTermNode fromNode)
    {
        this.sourceNode.removeCloned(this) ;

        this.thisTerm = fromNode.thisTerm ;
        this.setHomePackageNode(fromNode.getHomePackageNode()) ;

        setUserObject(fromNode.thisTerm.id) ;
        this.comment = fromNode.thisTerm.name ;
        setType(slm2type(fromNode.thisTerm.slm)) ;
        this.sourceNode = fromNode ;
        fromNode.addCloned(this) ;
    }

    public String toString()
    {
        //return "#" + super.toString();
        return "#" + sourceNode.toString() ; // 2005-08-14
    }
}
