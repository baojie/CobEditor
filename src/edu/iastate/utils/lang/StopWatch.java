package edu.iastate.utils.lang ;

import java.util.Date ;

/**
 * 
 * Example:
 *      StopWatch w = new StopWatch() ;
        w.start() ;
        treeLoader.makeDagFromRootsQuick(this, cutoff, true, this) ;
        w.stop();
        System.out.println(w.print());
 * 
 * <p>@author Jie Bao</p>
 * <p>@since 2005-08-30</p>
 */
public class StopWatch
{
    long startTime = 0 ; // in milliseconds
    long endTime = 0 ; // in milliseconds
    long currentTime = 0 ; // in milliseconds

    public void start()
    {
        startTime = new Date().getTime() ;
    }

    public void peek()
    {
        currentTime = new Date().getTime() ;
    }

    public void stop()
    {
        currentTime = new Date().getTime() ;
        endTime = currentTime ;
    }

    public long getSeconds()
    {
        return(currentTime - startTime) / 1000 ;
    }

    public String print()
    {
        long duration = getSeconds() ;
        long days = duration / (60 * 60 * 24) ;
        long remains = duration - days * (60 * 60 * 24) ;
        long hours = remains / (60 * 60) ;
        remains = remains - hours * (60 * 60) ;
        long minute = remains / 60 ;
        long seconds = remains - minute * 60 ;

        String str = duration + " second(s)" ;
        if(duration > 60)
        {
            str += " or " + ((days > 0) ? days + " day(s) " : "") ;
            str += ((hours > 0) ? hours + " hours(s) " : "") ;
            str += ((minute > 0) ? minute + " minute(s) " : "") ;
            str += seconds + " second(s)" ;
        }
        return str ;
    }
}
