(ns clecs-tetris.world.system.collision-test
  (:require [clecs-tetris.world.glass :refer [find-glass-tile]]
            [clecs-tetris.world.shape :refer [tiles with-coordinates]]
            [clecs-tetris.world.system.collision :refer :all]
            [clecs.query :as query]
            [clecs.test.mock :as mock]
            [midje.sweet :refer :all]))


(facts "-collides? checks collision against glass tiles."
       (let [w (mock/mock-editable-world)]
         (-collides? w ..eid..) => true
         (provided (mock/component w
                                   ..eid..
                                   :CurrentShapeComponent) => {:x irrelevant
                                                               :y irrelevant
                                                               :shape-name ..shape-name..
                                                               :shape-index ..shape-index..}
                   (tiles ..shape-name.. ..shape-index..) => ..tiles..
                   (with-coordinates ..tiles..) => [[0 0 "empty"]
                                                    [1 0 "filled"]
                                                    [0 1 "filled"]
                                                    [1 1 "empty"]]
                   (mock/component w
                                   ..eid..
                                   :ShapeTargetComponent) => {:x 3 :y 4}
                   (find-glass-tile w 4 4) => ..glass-eid-1..
                   (mock/component w
                                   ..glass-eid-1..
                                   :GlassTileComponent) => {:tile-type "filled"})


         (-collides? w ..eid..) => false
         (provided (mock/component w
                                   ..eid..
                                   :CurrentShapeComponent) => {:x irrelevant
                                                               :y irrelevant
                                                               :shape-name ..shape-name..
                                                               :shape-index ..shape-index..}
                   (tiles ..shape-name.. ..shape-index..) => ..tiles..
                   (with-coordinates ..tiles..) => [[0 0 "empty"]
                                                    [1 0 "filled"]
                                                    [0 1 "filled"]
                                                    [1 1 "empty"]]
                   (mock/component w
                                   ..eid..
                                   :ShapeTargetComponent) => {:x 3 :y 4}
                   (find-glass-tile w 4 4) => ..glass-eid-1..
                   (mock/component w
                                   ..glass-eid-1..
                                   :GlassTileComponent) => {:tile-type "moving"}
                   (find-glass-tile w 3 5) => ..glass-eid-2..
                   (mock/component w
                                   ..glass-eid-2..
                                   :GlassTileComponent) => {:tile-type "empty"})))


(fact "-collides? checks collision against the bottom of glass."
      (let [w (mock/mock-editable-world)]
        (-collides? w ..eid..) => true
        (provided (mock/component w
                                  ..eid..
                                  :CurrentShapeComponent) => {:x irrelevant
                                                              :y irrelevant
                                                              :shape-name ..shape-name..
                                                              :shape-index ..shape-index..}
                  (tiles ..shape-name.. ..shape-index..) => ..tiles..
                  (with-coordinates ..tiles..) => [[0 0 "filled"]]
                  (mock/component w
                                  ..eid..
                                  :ShapeTargetComponent) => {:x 5 :y -1})))


(fact "-collision-entities returns a sequence of entities to check for collision."
      (let [w (mock/mock-editable-world)]
        (-collision-entities w) => [..a.. ..c..]
        (provided (query/all :CurrentShapeComponent
                             :ShapeTargetComponent) => ..q..
                  (mock/query w ..q..) => [..a.. ..b.. ..c..]
                  (mock/component w ..a.. :CollisionComponent) => nil
                  (mock/component w ..b.. :CollisionComponent) => anything
                  (mock/component w ..c.. :CollisionComponent) => nil)))
