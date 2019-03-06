package com.firststory.firstinscriptions.transfer.relations;

import com.firststory.firstinscriptions.transfer.objects.RotationNode;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.neo4j.ogm.annotation.RelationshipEntity;

@EqualsAndHashCode( callSuper = true )
@ToString( callSuper = true )
@RelationshipEntity( type = "withRotation" )
@Data
public class WithRotation extends WithGeneric< RotationNode > {}
