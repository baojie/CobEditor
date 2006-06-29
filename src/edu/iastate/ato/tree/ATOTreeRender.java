package edu.iastate.ato.tree ;

import edu.iastate.ato.shared.IconLib ;
import edu.iastate.utils.tree.TypedTreeRender;

/**
 * Animal Trait Ontology Tree Render
 * @author Jie Bao
 * @since 2005-04-22
 */
public class ATOTreeRender extends TypedTreeRender
{
    public ATOTreeRender()
    {
        super() ;
        icons.put(ATOTreeNode.ROOT + "", IconLib.iconRoot) ;
        icons.put(ATOTreeNode.PACKAGE + "", IconLib.iconPackage) ;
        icons.put(ATOTreeNode.PUBLIC_TERM + "", IconLib.iconPublic) ;
        icons.put(ATOTreeNode.PROTECTED_TERM + "", IconLib.iconProtected) ;
        icons.put(ATOTreeNode.PRIVATE_TERM + "", IconLib.iconPrivate) ;
        icons.put(ATOTreeNode.META + "", IconLib.iconMeta) ;
    }
}
