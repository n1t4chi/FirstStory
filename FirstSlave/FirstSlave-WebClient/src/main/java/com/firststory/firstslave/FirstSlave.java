package com.firststory.firstslave;

import com.firststory.firstinscriptions.transfer.objects.TerrainNode;
import com.firststory.firsttools.PropertyUtils;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.Transport;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

import static com.firststory.firstslave.FirstSlaveProperties.MASTER_URL_PROPERTY;
import static com.firststory.firsttools.PropertyUtils.getPropertyOrThrow;

public class FirstSlave {

    public static void main( String[] args ) throws Exception {
        printTerrains();
        websockets();
        
    }
    
    private static void printTerrains() {
        RestTemplate restTemplate = getRestTemplate();
        
        ResponseEntity< List< TerrainNode > > terrainData = restTemplate.exchange(
            getPropertyOrThrow( MASTER_URL_PROPERTY ) + "terrain?pattern={pattern}",
            HttpMethod.GET,
            null, new ParameterizedTypeReference<>() {},
            Map.of( "pattern", "*" )
        );
        List< TerrainNode > body = terrainData.getBody();
        System.err.println( body );
    }
    
    private static RestTemplate getRestTemplate() {
        if( !PropertyUtils.isDebugMode() )
        {
            return new RestTemplate();
        }
        RestTemplate restTemplate = new RestTemplate(new BufferingClientHttpRequestFactory(new SimpleClientHttpRequestFactory()));
        List< ClientHttpRequestInterceptor > interceptors = new ArrayList<>();
        interceptors.add(new LoggingRequestInterceptor());
        restTemplate.setInterceptors( interceptors );
        return restTemplate;
    }
    
    
    private static void websockets() throws  Exception {
        WebSocketClient simpleWebSocketClient = new StandardWebSocketClient();
        List< Transport > transports = new ArrayList<>( 1 );
        transports.add( new WebSocketTransport( simpleWebSocketClient ) );
        
        SockJsClient sockJsClient = new SockJsClient( transports );
        WebSocketStompClient stompClient = new WebSocketStompClient( sockJsClient );
        stompClient.setMessageConverter( new MappingJackson2MessageConverter() );
        
        String url = "ws://localhost:8080/chat";
        String userId = "spring-" + ThreadLocalRandom.current().nextInt( 1, 99 );
        StompSessionHandler sessionHandler = new MyStompSessionHandler( userId );
        StompSession session = stompClient.connect( url, sessionHandler ).get();
        BufferedReader in = new BufferedReader( new InputStreamReader( System.in ) );
        for ( ; ; ) {
            System.out.print( userId + " >> " );
            System.out.flush();
            String line = in.readLine();
            if ( line == null ) { break; }
            if ( line.length() == 0 ) { continue; }
            ClientMessage msg = new ClientMessage( userId, line );
            session.send( "/app/chat/java", msg );
        }
    }
    
    static public class MyStompSessionHandler extends StompSessionHandlerAdapter {
        
        private final String userId;
        
        public MyStompSessionHandler( String userId ) {
            this.userId = userId;
        }
    
        @Override
        public void handleFrame( StompHeaders headers, Object payload ) {
            super.handleFrame( headers, payload );
            System.err.println( "handleFrame: " + payload );
        }
    
        @Override
        public void afterConnected( StompSession session, StompHeaders connectedHeaders ) {
            System.err.println( "Connected!" );
            subscribeTopic( "/topic/messages", session );
            sendJsonMessage( session );
        }
        
        private void sendJsonMessage( StompSession session ) {
            ClientMessage msg = new ClientMessage( userId, "hello from spring" );
            session.send( "/app/chat/java", msg );
        }
        
        private void subscribeTopic( String topic, StompSession session ) {
            session.subscribe( topic, new StompFrameHandler() {
                
                @Override
                public Type getPayloadType( StompHeaders headers ) {
                    return ServerMessage.class;
                }
                
                @Override
                public void handleFrame( StompHeaders headers, Object payload ) {
                    System.err.println( payload.toString() );
                }
            } );
        }
    }
}
