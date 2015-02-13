(ns clecs-tetris.world.system.update-shape-test
  (:require [clecs-tetris.world.shape :refer [tiles]]
            [clecs-tetris.world.system.update-shape :refer :all]
            [clecs.query :as query]
            [clecs.test.mock :as mock]
            [clecs.world :as world]
            [midje.sweet :refer :all]))


(fact "-set-glass-tile sets one tile."
      (let [w (mock/mock-editable-world)]
        (-set-glass-tile w ..x.. ..y.. ..tile..) => nil
        (provided (query/all :GlassTileComponent) => ..q..
                  (mock/query w ..q..) => [..n-2.. ..n-1.. ..n.. ..n+1..]
                  (mock/component w
                                  ..n-2..
                                  :GlassTileComponent) => {:x ..x..
                                                           :y ..y-1..
                                                           :tile-type anything}
                  (mock/component w
                                  ..n-1..
                                  :GlassTileComponent) => {:x ..x+1..
                                                           :y ..y-1..
                                                           :tile-type anything}
                  (mock/component w
                                  ..n..
                                  :GlassTileComponent) => {:x ..x..
                                                           :y ..y..
                                                           :tile-type ..other-tile..}
                  (mock/component w
                                  ..n+1..
                                  :GlassTileComponent) => {:x ..x+1..
                                                           :y ..y..
                                                           :tile-type anything}
                  (world/set-component w
                                      ..n..
                                      :GlassTileComponent
                                      {:x ..x..
                                       :y ..y..
                                       :tile-type ..tile..}) => anything)))


(fact "-update-glass-tiles erases and then re-applied the tiles."
      (let [w (mock/mock-editable-world)]
        (-update-glass-tiles w [1 2] [5 6] [["empty" "filled"]]) => nil
        (provided (-set-glass-tile w 2 2 "empty") => nil
                  (-set-glass-tile w 6 6 "moving") => nil)))


(fact "-update-shape-location does nothing if there is no current shape."
      (let [w (mock/mock-editable-world)]
        (-update-shape-location w ..dt.. ..cd..) => nil
        (provided (query/all :CurrentShapeComponent
                             :TargetLocationComponent) => ..q..
                  (mock/query w ..q..) => nil)))


(fact "-update-shape-location decrements the countdown if it's not zero."
      (let [w (mock/mock-editable-world)]
        (-update-shape-location w 17 ..cd..) => nil
        (provided (query/all :CurrentShapeComponent
                             :TargetLocationComponent) => ..q..
                  (mock/query w ..q..) => [..eid..]
                  (mock/component w
                                  ..eid..
                                  :TargetLocationComponent) => {:x ..target-x..
                                                                :y ..target-y..
                                                                :countdown 30}
                  (world/set-component w
                                      ..eid..
                                      :TargetLocationComponent
                                      {:x ..target-x..
                                       :y ..target-y..
                                       :countdown 13}) => nil)))


(fact "-update-shape-location updates location and resets countdown."
      (let [w (mock/mock-editable-world)]
        (let [[x y] [0 0]
              [target-x target-y] [1 1]
              current-shape-component {:x x
                                       :y y
                                       :shape-name ..shape-name..
                                       :shape-index ..shape-index..}
              target-location-component {:x target-x :y target-y :countdown -2}]
          (-update-shape-location w ..dt.. ..cd..) => nil
          (provided (query/all :CurrentShapeComponent
                               :TargetLocationComponent) => ..q..
                    (mock/query w ..q..) => [..eid..]
                    (mock/component w ..eid.. :CurrentShapeComponent) => current-shape-component
                    (mock/component w ..eid.. :TargetLocationComponent) => target-location-component
                    (world/set-component w
                                         ..eid..
                                         :CurrentShapeComponent
                                         {:x target-x
                                          :y target-y
                                          :shape-name ..shape-name..
                                          :shape-index ..shape-index..}) => nil
                    (tiles ..shape-name.. ..shape-index..) => ..tiles..
                    (-update-glass-tiles w [x y] [target-x target-y] ..tiles..) => nil
                    (world/set-component w
                                         ..eid..
                                         :TargetLocationComponent
                                         {:x target-x :y target-y :countdown ..cd..}) => nil))))
