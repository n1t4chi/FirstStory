package com.firststory.firstslave;

import com.firststory.firsttools.FirstToolsConstants;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.logging.Logger;

public class LoggingRequestInterceptor implements ClientHttpRequestInterceptor {

    final static Logger log = FirstToolsConstants.getLogger( LoggingRequestInterceptor.class );

    @Override
    public ClientHttpResponse intercept( HttpRequest request, byte[] body, ClientHttpRequestExecution execution ) throws
        IOException {
        traceRequest( request, body );
        ClientHttpResponse response = execution.execute( request, body );
        traceResponse( response );
        return response;
    }

    private void traceRequest( HttpRequest request, byte[] body ) {
        log.info( "" +
            "=====request begin======\n" +
            "URI         : " + request.getURI() + "\n" +
            "Method      : " + request.getMethod() + "\n" +
            "Headers     : " + request.getHeaders() + "\n" +
            "Request body: " + new String( body, StandardCharsets.UTF_8 ) + "\n" +
            "=====request end========"
        );
    }

    private void traceResponse( ClientHttpResponse response ) throws IOException {
        StringBuilder inputStringBuilder = new StringBuilder();
        InputStream body = response.getBody();
        BufferedReader bufferedReader = new BufferedReader( new InputStreamReader( body, StandardCharsets.UTF_8 ) );
        String line = bufferedReader.readLine();
        while ( line != null ) {
            inputStringBuilder.append( line );
            inputStringBuilder.append( '\n' );
            line = bufferedReader.readLine();
        }
        log.info( "" +
            "=====response begin======\n" +
            "Status code  : " + response.getStatusCode() + "\n" +
            "Status text  : " + response.getStatusText() + "\n" +
            "Headers      : " + response.getHeaders() + "\n" +
            "Response body: " + inputStringBuilder.toString() + "\n" +
            "=====response end========"
        );
    }
}
