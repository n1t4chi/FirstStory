package com.firststory.firstinscriptions.transfer.relations;

import com.firststory.firstinscriptions.transfer.objects.ColouringNode;
import com.firststory.firstinscriptions.transfer.objects.UvMappingNode;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.neo4j.ogm.annotation.RelationshipEntity;

@EqualsAndHashCode( callSuper = true )
@ToString( callSuper = true )
@RelationshipEntity( type = "WithUvMap" )
@Data
public class WithUvMap extends WithGeneric< UvMappingNode > {}
