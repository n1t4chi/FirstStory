CREATE CONSTRAINT ON (n:Terrain) ASSERT n.name IS UNIQUE;
CREATE CONSTRAINT ON (n:Texture) ASSERT n.name IS UNIQUE;

CREATE
  (:Terrain {name: 'Grass'}),
  (:Terrain {name: 'Water'}),
  (:Terrain {name: 'Sand'}),
  (:Terrain {name: 'Ice'}),
  (:Texture {name: 'Grass', fileName: 'grass3D', directions: 1, frames: 1, rows: 1, columns: 1}),
  (:Texture {name: 'Sand', fileName: 'sand3D', directions: 1, frames: 1, rows: 1, columns: 1}),
  (:Texture {name: 'Water', fileName: 'water3D', directions: 1, frames: 1, rows: 1, columns: 1}),
  (:Texture {name: 'Hero', fileName: 'hero3D', directions: 1, frames: 1, rows: 1, columns: 1})
;

MATCH (terrain:Terrain)
MATCH (texture:Texture)
  WHERE texture.name = terrain.name
CREATE (terrain)-[:withTexture {name: 'default'}]->(texture)
;

MATCH (n)
RETURN n;

MATCH (n)
DETACH DELETE n;

MATCH (n)
OPTIONAL MATCH (n)-[rel]->()
RETURN n, rel;
