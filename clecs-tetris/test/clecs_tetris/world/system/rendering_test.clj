(ns clecs-tetris.world.system.rendering-test
  (:require [clecs.mock :refer [mock-component
                                mock-query
                                mock-world]]
            [clecs.query :as query]
            [clecs.world :as world]
            [clecs-tetris.world.component :refer [->LevelComponent
                                                  ->LinesDroppedComponent
                                                  ->NextShapeComponent
                                                  ->ScoreComponent
                                                  ->GlassTileComponent
                                                  LevelComponent
                                                  LinesDroppedComponent
                                                  NextShapeComponent
                                                  ScoreComponent
                                                  GlassTileComponent]]
            [clecs-tetris.world.system.rendering :refer :all]
            [midje.sweet :refer :all]))


(fact "-get-next-shape returns 4x4 tile grid of next shape."
      (-get-next-shape mock-world) => ..tiles..
      (provided (query/all NextShapeComponent) => ..q..
                (mock-query ..q..) => [..eid..]
                (mock-component ..eid.. NextShapeComponent) => (->NextShapeComponent ..eid.. {:tiles ..tiles..})))


(fact "-get-stats returns level, score and lines."
      (-get-stats mock-world) => {:level ..level..
                                  :lines ..lines..
                                  :score ..score..}
      (provided (query/all LevelComponent
                           LinesDroppedComponent
                           ScoreComponent) => ..q..
                (mock-query ..q..) => [..eid..]
                (mock-component ..eid.. LevelComponent) => (->LevelComponent ..eid.. ..level..)
                (mock-component ..eid.. LinesDroppedComponent) => (->LinesDroppedComponent ..eid.. ..lines..)
                (mock-component ..eid.. ScoreComponent) => (->ScoreComponent ..eid.. ..score..)))


(fact "-get-tiles returns a 2D vector of tile types."
      (-get-tiles mock-world 3 2) => [[:empty ..11.. ..21..]
                                      [..00.. ..10.. :empty]]
      (provided (query/all GlassTileComponent) => ..q..
                (mock-query ..q..) => [..a.. ..b.. ..c.. ..d..]
                (mock-component ..a.. GlassTileComponent) => (->GlassTileComponent ..a.. 0 0 ..00..)
                (mock-component ..b.. GlassTileComponent) => (->GlassTileComponent ..b.. 1 0 ..10..)
                (mock-component ..c.. GlassTileComponent) => (->GlassTileComponent ..c.. 1 1 ..11..)
                (mock-component ..d.. GlassTileComponent) => (->GlassTileComponent ..d.. 2 1 ..21..)))
