package com.firststory.firstmaster.services;

import com.firststory.firstinscriptions.TerrainTransferData;
import com.firststory.firstinscriptions.WithTextureRelation;
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
    public Collection< TerrainTransferData > save( Collection< TerrainTransferData > terrain ) {
        Collection< TerrainTransferData > terrains = new ArrayList<>();
        TerrainRepository.saveAll( terrain ).forEach( terrains::add );
        return terrains;
    }

    @Transactional()
    public void delete( Collection< TerrainTransferData > terrain ) {
        TerrainRepository.deleteAll( terrain );
    }

    @Transactional( readOnly = true )
    public Collection< TerrainTransferData > findByNameLike( String pattern ) {
        return TerrainRepository.findByNameLike( pattern );
    }

    @Transactional( readOnly = true )
    public Map< String, Object > graph( int limit ) {
        return toD3Format( TerrainRepository.graph( limit ) );
    }

    private Map< String, Object > toD3Format( Collection< TerrainTransferData > terrains ) {
        List< Map< String, Object > > nodes = new ArrayList<>();
        List< Map< String, Object > > rels = new ArrayList<>();
        for ( TerrainTransferData terrain : terrains ) {
            nodes.add( Map.of( "name", terrain.getName(), "label", "Terrain" ) );
            Collection< WithTextureRelation > withTextures = terrain.getWithTexture();
            withTextures.forEach( withTexture -> rels.add( Map.of( "source",
                withTexture.getTerrain(),
                "target",
                withTexture.getTexture()
            ) ) );
        }
        return Map.of( "nodes", nodes, "links", rels );
    }

}
