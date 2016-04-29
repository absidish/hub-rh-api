package io.subutai.test;


import java.io.IOException;

import org.json.JSONObject;
import org.junit.Test;

import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;


public class TestRhApi
{
    @Test
    public void copyDockerFileTest()
    {
        String[] commands = {
                "mkdir /home/ubuntu/docker/docker-nginx",
                "sudo /apps/subutai/current/bin/curl -O https://raw.githubusercontent.com/nginxinc/docker-nginx/master/stable/jessie/Dockerfile",
                "mv Dockerfile ../docker/docker-nginx",
                "sudo /apps/subutai/current/bin/test.sh"
        };

        for ( String command : commands )
        {
            if ( sendRequest( command ) )
            {
                System.out.println( "Command: " + command );
            }
        }
    }


    private boolean sendRequest( String cmd )
    {

        boolean isSuccess = false;
        JSONObject json = new JSONObject();
        json.put( "cmd", cmd );

        CloseableHttpClient httpClient = HttpClientBuilder.create().build();

        try
        {
            HttpPost request = new HttpPost( "http://192.168.0.102:82/docker" );
            StringEntity params = new StringEntity( json.toString() );
            request.addHeader( "content-type", "application/json" );
            request.setEntity( params );
            httpClient.execute( request );
            isSuccess = true;
        }
        catch ( Exception ex )
        {
            isSuccess = false;
            System.out.println( ex.getMessage() );
        }
        finally
        {
            try
            {
                httpClient.close();
                return isSuccess;
            }
            catch ( IOException e )
            {
                e.printStackTrace();
            }
        }

        return isSuccess;
    }
}
