package com.firststory.firstmaster.services;

import com.firststory.firstinscriptions.transfer.objects.TerrainNode;
import com.firststory.firstinscriptions.transfer.relations.WithTexture;
import com.firststory.firstmaster.repos.TerrainRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

@Service
public class TerrainService {

    private final TerrainRepository TerrainRepository;

    public TerrainService( TerrainRepository terrainRepository ) {
        this.TerrainRepository = terrainRepository;
    }

    @Transactional()
    public Collection< TerrainNode > save( Collection< TerrainNode > terrain ) {
        Collection< TerrainNode > terrains = new ArrayList<>();
        TerrainRepository.saveAll( terrain ).forEach( terrains::add );
        return terrains;
    }

    @Transactional()
    public void delete( Collection< TerrainNode > terrain ) {
        TerrainRepository.deleteAll( terrain );
    }

    @Transactional( readOnly = true )
    public Collection< TerrainNode > findByNameLike( String pattern ) {
        return TerrainRepository.findByNameLike( pattern );
    }

    @Transactional( readOnly = true )
    public Map< String, Object > graph( int limit ) {
        return toD3Format( TerrainRepository.graph( limit ) );
    }

    private Map< String, Object > toD3Format( Collection< TerrainNode > terrains ) {
        List< Map< String, Object > > nodes = new ArrayList<>();
        List< Map< String, Object > > rels = new ArrayList<>();
        for ( TerrainNode terrain : terrains ) {
            nodes.add( Map.of( "name", terrain.getName(), "label", "Terrain" ) );
            Collection< WithTexture > withTextures = terrain.getTexture();
            withTextures.forEach( withTexture -> rels.add( Map.of( "source",
                withTexture.getStart(),
                "target",
                withTexture.getEnd()
            ) ) );
        }
        return Map.of( "nodes", nodes, "links", rels );
    }

}
