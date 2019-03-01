package com.firststory.firstinscriptions.transfer.objects;

import lombok.Data;
import org.neo4j.ogm.annotation.GeneratedValue;
import org.neo4j.ogm.annotation.Id;
import org.neo4j.ogm.annotation.NodeEntity;

@NodeEntity( label = "Object" )
@Data
public class Node {
    
    @Id
    @GeneratedValue
    private Long id;
    private String name;
}
