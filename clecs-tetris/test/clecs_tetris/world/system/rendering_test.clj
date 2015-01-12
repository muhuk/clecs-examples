(ns clecs-tetris.world.system.rendering-test
  (:require [clecs.core :refer [make-world]]
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
      (let [w (make-world identity)
            A (->NextShapeComponent ..eid.. {:tiles ..tiles..})]
        (-get-next-shape w) => ..tiles..
        (provided (query/all NextShapeComponent) => ..q..
                  (world/query w ..q..) => [..eid..]
                  (world/component w ..eid.. NextShapeComponent) => A)))


(fact "-get-stats returns level, score and lines."
      (let [w (make-world identity)]
        (-get-stats w) => {:level ..level..
                           :lines ..lines..
                           :score ..score..}
        (provided (query/all LevelComponent
                             LinesDroppedComponent
                             ScoreComponent) => ..q..
                  (world/query w ..q..) => [..eid..]
                  (world/component w ..eid.. LevelComponent) => (->LevelComponent ..eid.. ..level..)
                  (world/component w ..eid.. LinesDroppedComponent) => (->LinesDroppedComponent ..eid.. ..lines..)
                  (world/component w ..eid.. ScoreComponent) => (->ScoreComponent ..eid.. ..score..))))


(fact "-get-tiles returns a 2D vector of tile types."
      (let [w (make-world identity)]
      (-get-tiles w 3 2) => [[:empty ..11.. ..21..]
                                      [..00.. ..10.. :empty]]
      (provided (query/all GlassTileComponent) => ..q..
                (world/query w ..q..) => [..a.. ..b.. ..c.. ..d..]
                (world/component w ..a.. GlassTileComponent) => (->GlassTileComponent ..a.. 0 0 ..00..)
                (world/component w ..b.. GlassTileComponent) => (->GlassTileComponent ..b.. 1 0 ..10..)
                (world/component w ..c.. GlassTileComponent) => (->GlassTileComponent ..c.. 1 1 ..11..)
                (world/component w ..d.. GlassTileComponent) => (->GlassTileComponent ..d.. 2 1 ..21..))))
