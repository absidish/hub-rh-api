

import docker.handler.DockerHandler;

import static spark.Spark.port;
import static spark.Spark.post;


public class Init
{
    public static void main( String[] args )
    {
        port( 8081 );
        post( "/docker", new DockerHandler() );
    }
}