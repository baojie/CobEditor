package edu.iastate.ato.shared ;

import javax.swing.JProgressBar ;

/**
 * <p>@author Jie Bao</p>
 * <p>@since 2005-08-22</p>
 */
public class LongTask
{
    JProgressBar progress ;

    public void setProgress(JProgressBar progress)
    {
        this.progress = progress ;
    }

    public void updateProgress(String info)
    {
        if(progress != null)
        {
            progress.setString(info) ;
            progress.validate() ;
        }
    }
}
