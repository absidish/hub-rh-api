package kurjun;


import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;

import org.json.JSONObject;

import org.apache.http.HttpEntity;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.InputStreamBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;

import spark.Request;
import spark.Response;
import spark.Route;
import spark.utils.IOUtils;


public class KurjunHelper implements Route
{
    private static Logger LOG = Logger.getLogger( KurjunHelper.class.getName() );

    protected static final int TIMEOUT = 60 * 1000; // 1 minute
    protected static final String KURJUN_SSL_URL = "https://cdn.subut.ai:8338/kurjun/";


    @Override
    public Object handle( final Request request, final Response response ) throws Exception
    {
        String jsonBody = request.body();

        String filePath = "";
        String fileName = "";
        String userToken = "";

        try
        {
            JSONObject params = new JSONObject( jsonBody );
            filePath = params.getString( "filePath" );
            fileName = params.getString( "fileName" );
            userToken = params.getString( "userToken" );

            LOG.info( "token: " + userToken );
            LOG.info( "filePath: " + filePath );
            LOG.info( "fileName: " + fileName );

            upload( filePath, fileName, userToken );

            return response;
        }
        catch ( Exception e )
        {
            return e.getMessage();
        }
    }


    public static void upload( String filePath, String fileName, String userToken )
    {
        File template = new File( filePath );
        try
        {
            InputStream inputStream = new FileInputStream( template );
            uploadTemplate( inputStream, fileName, userToken );
        }
        catch ( Exception e )
        {
            LOG.error( e.getMessage() );
        }
    }


    private static boolean uploadTemplate( InputStream inputStream, String fileName, String userToken )
            throws IOException, IllegalAccessException
    {
        boolean result = false;

        CloseableHttpClient client = getHttpsClient();
        try
        {
            HttpPost post = new HttpPost( KURJUN_SSL_URL + "rest/template/upload" );

            MultipartEntityBuilder entityBuilder = MultipartEntityBuilder.create();
            entityBuilder.setMode( HttpMultipartMode.BROWSER_COMPATIBLE );
            entityBuilder.addPart( "file", new InputStreamBody( inputStream, fileName ) );
            entityBuilder.addTextBody( "token", userToken );
            HttpEntity httpEntity = entityBuilder.build();
            LOG.info( "started uploading" );
            post.setEntity( httpEntity );

            CloseableHttpResponse response = client.execute( post );
            LOG.info( "responce: " + response.getStatusLine().getStatusCode() );
            try
            {
                if ( response != null && response.getStatusLine().getStatusCode() == 200 )
                {
                    HttpEntity entity = response.getEntity();
                    LOG.info( "File uploaded, responded id: " + IOUtils.toString( entity.getContent() ) );
                    result = true;
                    EntityUtils.consume( entity );
                }
            }
            finally
            {
                response.close();
            }
        }
        finally
        {
            client.close();
        }

        return result;
    }


    public static CloseableHttpClient getHttpsClient()
    {
        try
        {
            RequestConfig config =
                    RequestConfig.custom().setSocketTimeout( TIMEOUT ).setConnectTimeout( TIMEOUT ).build();

            SSLContextBuilder sslContextBuilder = new SSLContextBuilder();
            sslContextBuilder.loadTrustMaterial( null, new TrustSelfSignedStrategy() );
            SSLConnectionSocketFactory sslSocketFactory =
                    new SSLConnectionSocketFactory( sslContextBuilder.build(), NoopHostnameVerifier.INSTANCE );

            return HttpClients.custom().setDefaultRequestConfig( config ).setSSLSocketFactory( sslSocketFactory )
                              .build();
        }
        catch ( NoSuchAlgorithmException e )
        {
            LOG.info( e.getMessage() );
        }
        catch ( KeyStoreException e )
        {
            LOG.info( e.getMessage() );
        }
        catch ( KeyManagementException e )
        {
            LOG.info( e.getMessage() );
        }

        return HttpClients.createDefault();
    }
}
