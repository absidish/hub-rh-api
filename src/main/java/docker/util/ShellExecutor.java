package docker.util;


import java.io.BufferedReader;
import java.io.InputStreamReader;

import static jdk.nashorn.internal.runtime.regexp.joni.Config.log;


public class ShellExecutor
{
    public static String execute( String cmd )
    {
        log.println( "cmd: " + cmd );
        String result = "";
        Runtime rt = Runtime.getRuntime();
        Process pr = null;
        try
        {
            pr = rt.exec( cmd );
        }
        catch ( Exception e )
        {
            return e.getMessage();
        }


        InputStreamReader isr = new InputStreamReader( pr.getInputStream() );
        BufferedReader br = new BufferedReader( isr );

        String line = null;

        try
        {
            while ( ( line = br.readLine() ) != null )
            {
                result += " " + line;
            }
        }
        catch ( Exception e )
        {
            return e.getMessage();
        }

        log.println( "result: " + result );

        return result;
    }
}
