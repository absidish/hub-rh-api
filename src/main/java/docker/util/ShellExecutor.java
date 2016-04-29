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
        int extCode = 9;
        Runtime rt = Runtime.getRuntime();
        Process pr = null;
        try
        {
            pr = rt.exec( cmd );
            extCode = pr.waitFor();
        }
        catch ( Exception e )
        {
            log.println( e.getMessage() );
        }
        log.println( "exit code: " + extCode );


        InputStreamReader isr = new InputStreamReader( pr.getInputStream() );
        BufferedReader br = new BufferedReader( isr );


        String line = null;

        try
        {
            while ( ( line = br.readLine() ) != null )
            {
                result += " " + line;
                log.println( line );
            }
        }
        catch ( Exception e )
        {
            log.println( e.getMessage() );
        }

        log.println( "result: " + result );

        return result;
    }
}
