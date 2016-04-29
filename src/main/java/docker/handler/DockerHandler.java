package docker.handler;


import org.json.JSONObject;

import docker.util.ShellExecutor;
import spark.Request;
import spark.Response;
import spark.Route;


public class DockerHandler implements Route
{
    @Override
    public Object handle( Request request, Response response ) throws Exception
    {
        String jsonBody = request.body();
        String cmd = "";
        try
        {
            JSONObject params = new JSONObject( jsonBody );
            cmd = params.getString( "cmd" );

            String h = ShellExecutor.execute( cmd );
            response.body( h );

            return response;
        }
        catch ( Exception e )
        {
            return e.getMessage();
        }
    }
}
