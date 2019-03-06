package com.firststory.firstinscriptions.transfer.relations;

import com.firststory.firstinscriptions.transfer.objects.TextureNode;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.neo4j.ogm.annotation.RelationshipEntity;

@EqualsAndHashCode( callSuper = true )
@ToString( callSuper = true )
@RelationshipEntity( type = "withTexture" )
@Data
public class WithTexture extends WithGeneric< TextureNode > {}
