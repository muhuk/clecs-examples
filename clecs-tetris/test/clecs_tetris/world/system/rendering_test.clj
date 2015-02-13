(ns clecs-tetris.world.system.rendering-test
  (:require [clecs-tetris.world.system.rendering :refer :all]
            [clecs.query :as query]
            [clecs.test.mock :as mock]
            [midje.sweet :refer :all]))


(fact "-get-next-shape returns 4x4 tile grid of next shape."
      (let [w (mock/mock-editable-world)]
        (-get-next-shape w) => ..tiles..
        (provided (query/all :NextShapeComponent) => ..q..
                  (mock/query w ..q..) => [..eid..]
                  (mock/component w ..eid.. :NextShapeComponent) => {:tiles ..tiles..})))


(fact "-get-stats returns level, score and lines."
      (let [w (mock/mock-editable-world)]
        (-get-stats w) => {:level ..level..
                           :lines ..lines..
                           :score ..score..}
        (provided (query/all :LevelComponent
                             :LinesDroppedComponent
                             :ScoreComponent) => ..q..
                  (mock/query w ..q..) => [..eid..]
                  (mock/component w ..eid.. :LevelComponent) => {:level ..level..}
                  (mock/component w ..eid.. :LinesDroppedComponent) => {:lines ..lines..}
                  (mock/component w ..eid.. :ScoreComponent) => {:score ..score..})))


(fact "-get-tiles returns a 2D vector of tile types."
      (let [w (mock/mock-editable-world)]
        (-get-tiles w 3 2) => [["empty" ..11.. ..21..]
                               [..00.. ..10.. "empty"]]
        (provided (query/all :GlassTileComponent) => ..q..
                  (mock/query w ..q..) => [..a.. ..b.. ..c.. ..d..]
                  (mock/component w ..a.. :GlassTileComponent) => {:x 0 :y 0 :tile-type ..00..}
                  (mock/component w ..b.. :GlassTileComponent) => {:x 1 :y 0 :tile-type ..10..}
                  (mock/component w ..c.. :GlassTileComponent) => {:x 1 :y 1 :tile-type ..11..}
                  (mock/component w ..d.. :GlassTileComponent) => {:x 2 :y 1 :tile-type ..21..})))
