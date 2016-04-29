

import docker.handler.DockerHandler;
import kurjun.KurjunHelper;

import static spark.Spark.port;
import static spark.Spark.post;


public class Init
{
    public static void main( String[] args )
    {
        port( 82 );
        post( "/docker", new DockerHandler() );
        post( "/docker/upload", new KurjunHelper() );
    }
}