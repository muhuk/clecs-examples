(ns clecs-tetris.world.system.update-shape-test
  (:require [clecs-tetris.world.component :refer [->CurrentShapeComponent
                                                  ->GlassTileComponent
                                                  ->TargetLocationComponent
                                                  CurrentShapeComponent
                                                  GlassTileComponent
                                                  TargetLocationComponent]]
            [clecs-tetris.world.system.update-shape :refer :all]
            [clecs.core :refer [make-world]]
            [clecs.query :as query]
            [clecs.world :as world]
            [midje.sweet :refer :all]))


(fact "-set-glass-tile sets one tile."
      (let [w (make-world identity)]
        (-set-glass-tile w ..x.. ..y.. ..tile..) => nil
        (provided (query/all GlassTileComponent) => ..q..
                  (world/query w ..q..) => [..n-2.. ..n-1.. ..n.. ..n+1..]
                  (world/component w ..n-2.. GlassTileComponent) => (->GlassTileComponent ..n-2.. ..x.. ..y-1.. anything)
                  (world/component w ..n-1.. GlassTileComponent) => (->GlassTileComponent ..n-1.. ..x+1.. ..y-1.. anything)
                  (world/component w ..n.. GlassTileComponent) => (->GlassTileComponent ..n-2.. ..x.. ..y.. ..other-tile..)
                  (world/component w ..n+1.. GlassTileComponent) => (->GlassTileComponent ..n-1.. ..x+1.. ..y.. anything)
                  (->GlassTileComponent ..n.. ..x.. ..y.. ..tile..) => ..component..
                  (world/set-component w ..component..) => anything)))


(fact "-update-glass-tiles erases and then re-applied the tiles."
      (let [w (make-world identity)]
        (-update-glass-tiles w [1 2] [5 6] [[:empty :filled]]) => nil
        (provided (-set-glass-tile w 2 2 :empty) => nil
                  (-set-glass-tile w 6 6 :moving) => nil)))


(fact "-update-shape-location does nothing if there is no current shape."
      (let [w (make-world identity)]
        (-update-shape-location w ..dt.. ..cd..) => nil
        (provided (query/all CurrentShapeComponent
                             TargetLocationComponent) => ..q..
                  (world/query w ..q..) => nil)))


(fact "-update-shape-location decrements the countdown if it's not zero."
      (let [w (make-world identity)
            A (->TargetLocationComponent ..eid.. ..target-x.. ..target-y.. 30)
            B (->TargetLocationComponent ..eid.. ..target-x.. ..target-y.. 13)]
        (-update-shape-location w 17 ..cd..) => nil
        (provided (query/all CurrentShapeComponent
                             TargetLocationComponent) => ..q..
                  (world/query w ..q..) => [..eid..]
                  (world/component w ..eid.. TargetLocationComponent) => A
                  (world/set-component w B) => nil)))


(fact "-update-shape-location updates location and resets countdown."
      (let [w (make-world identity)]
        (let [shape {:tiles ..tiles..}
              [x y] [0 0]
              [target-x target-y] [1 1]
              current-shape-component (->CurrentShapeComponent ..eid.. x y shape)
              target-location-component (->TargetLocationComponent ..eid.. target-x target-y -2)]
          (-update-shape-location w ..dt.. ..cd..) => nil
          (provided (query/all CurrentShapeComponent
                               TargetLocationComponent) => ..q..
                    (world/query w ..q..) => [..eid..]
                    (world/component w ..eid.. CurrentShapeComponent) => current-shape-component
                    (world/component w ..eid.. TargetLocationComponent) => target-location-component
                    (world/set-component w (->CurrentShapeComponent ..eid.. target-x target-y shape)) => nil
                    (-update-glass-tiles w [x y] [target-x target-y] ..tiles..) => nil
                    (world/set-component w (->TargetLocationComponent ..eid.. target-x target-y ..cd..)) => nil))))
