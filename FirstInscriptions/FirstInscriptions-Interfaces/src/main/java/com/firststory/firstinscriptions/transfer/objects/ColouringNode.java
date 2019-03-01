package com.firststory.firstinscriptions.transfer.objects;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.neo4j.ogm.annotation.NodeEntity;

@NodeEntity( label = "Colouring" )
@EqualsAndHashCode( callSuper = true )
@Data
public class ColouringNode extends TextNode {
}
