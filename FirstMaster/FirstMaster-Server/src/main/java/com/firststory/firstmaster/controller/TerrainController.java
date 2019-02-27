package com.firststory.firstmaster.controller;

import com.firststory.firstinscriptions.TerrainTransferData;
import com.firststory.firstmaster.services.TerrainService;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.Map;

@RestController
@RequestMapping( "/terrain" )
public class TerrainController {

    private final TerrainService terrainService;

    public TerrainController( TerrainService terrainService ) {
        this.terrainService = terrainService;
    }

    @GetMapping( "/graph" )
    public Map< String, Object > graph( @RequestParam( value = "limit", required = false ) Integer limit ) {
        return terrainService.graph( limit == null ? 100 : limit );
    }

    @GetMapping( "" )
    public Collection< TerrainTransferData > findTerrains( @RequestParam( value = "pattern" ) String pattern ) {
        return terrainService.findByNameLike( pattern );
    }

    @PutMapping( "" )
    public Collection< TerrainTransferData > saveTerrains( @RequestParam( value = "terrains" ) Collection< TerrainTransferData > terrains ) {
        return terrainService.save( terrains );
    }

    @DeleteMapping( "" )
    public void deleteTerrains( @RequestParam( value = "terrains" ) Collection< TerrainTransferData > terrains ) {
        terrainService.delete( terrains );
    }
}
