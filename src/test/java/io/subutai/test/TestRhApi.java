package io.subutai.test;


import java.io.IOException;
import java.util.UUID;

import org.json.JSONObject;
import org.junit.Test;

import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.log4j.Logger;


public class TestRhApi
{
    private static Logger LOG = Logger.getLogger( TestRhApi.class.getName() );


        @Test
    public void upload()
    {
        String url = "http://192.168.0.102:82/docker/upload";

        String fileName = "dockerNginxSydyk";
        String filePath = "/mnt/lib/lxc/tmpdir/docker-nginx-subutai-template_4.0.0_amd64.tar.gz";
        String token = "d633c130508ba7f7f77aff96f1e9e5e4";

        JSONObject json = new JSONObject();
        json.put( "fileName", fileName );
        json.put( "filePath", filePath );
        json.put( "userToken", token );

        sendRequest( url, json );
    }


//    @Test
    public void copyDockerFileTest()
    {
        String[] commands = {
                "mkdir /home/ubuntu/docker/docker-nginx",
                "sudo /apps/subutai/current/bin/curl -O https://raw.githubusercontent"
                        + ".com/nginxinc/docker-nginx/master/stable/jessie/Dockerfile",
                "mv Dockerfile ../docker/docker-nginx", "sudo /apps/subutai/current/bin/test.sh",
                "sudo subutai export docker-nginx"
        };

        String url = "http://192.168.0.102:82/docker";

        for ( String command : commands )
        {

            JSONObject json = new JSONObject();
            json.put( "cmd", command );

            if ( sendRequest( url, json ) )
            {
                LOG.info( "Command: " + command );
            }
        }
    }


    private boolean sendRequest( String url, JSONObject json )
    {

        boolean isSuccess = false;

        CloseableHttpClient httpClient = HttpClientBuilder.create().build();

        try
        {
            HttpPost request = new HttpPost( url );
            StringEntity params = new StringEntity( json.toString() );
            request.addHeader( "content-type", "application/json" );
            request.setEntity( params );
            httpClient.execute( request );
            isSuccess = true;
        }
        catch ( Exception ex )
        {
            isSuccess = false;
            LOG.error( ex.getMessage() );
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
                LOG.error( e.getMessage() );
            }
        }

        return isSuccess;
    }
}
