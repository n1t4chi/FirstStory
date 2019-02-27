package com.firststory.firstmaster;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.task.TaskExecutor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

//@EnableScheduling
@Controller
public class ChatController {
    
    private final TaskExecutor executor;
    
    private final SimpMessagingTemplate template;
    
    public ChatController(
        SimpMessagingTemplate template,
        @Qualifier( "brokerChannelExecutor" ) TaskExecutor executor
    ) {
        this.template = template;
        this.executor = executor;
    }
    
    @MessageMapping( "/chat/{topic}" )
    @SendTo( "/topic/messages" )
    public OutputMessage send( @DestinationVariable( "topic" ) String topic, Message message ) {
        executor.execute( () -> {
            try {
                Thread.sleep( 3000 );
                sendUpdate();
            } catch ( InterruptedException e ) {
                e.printStackTrace();
            }
        } );
        return new OutputMessage( message.getFrom(), message.getText(), topic );
        
    }
    
//    @Scheduled( fixedRate = 1000 )
    private void sendUpdate() {
        this.template.convertAndSend( "/topic/messages", new OutputMessage( "server", "i message you after some time", "messages" ) );
    }
}
