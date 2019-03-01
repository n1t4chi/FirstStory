package com.firststory.firstinscriptions.transfer.relations;

import com.firststory.firstinscriptions.transfer.objects.Node;
import lombok.Data;
import org.neo4j.ogm.annotation.EndNode;
import org.neo4j.ogm.annotation.GeneratedValue;
import org.neo4j.ogm.annotation.Id;
import org.neo4j.ogm.annotation.StartNode;

@Data
public class With< Start extends Node, End extends Node > {
    
    @Id
    @GeneratedValue
    private Long id;
    
    private String name;
    
    @StartNode
    private Start start;
    
    @EndNode
    private End end;
}
