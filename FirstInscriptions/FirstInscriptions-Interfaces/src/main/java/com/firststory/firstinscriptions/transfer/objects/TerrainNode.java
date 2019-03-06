package com.firststory.firstinscriptions.transfer.objects;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.neo4j.ogm.annotation.NodeEntity;

@NodeEntity( label = "Terrain" )
@EqualsAndHashCode( callSuper = true )
@ToString( callSuper = true )
@Data
public class TerrainNode extends GraphicNode {
}
