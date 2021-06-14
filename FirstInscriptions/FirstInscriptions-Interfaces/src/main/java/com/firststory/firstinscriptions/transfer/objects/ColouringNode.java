package com.firststory.firstinscriptions.transfer.objects;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.neo4j.ogm.annotation.NodeEntity;

@NodeEntity( label = "Colouring" )
@EqualsAndHashCode( callSuper = true )
@ToString( callSuper = true )
@Data
public class ColouringNode extends TextNode {
}