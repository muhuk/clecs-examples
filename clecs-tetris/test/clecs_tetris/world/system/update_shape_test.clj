(ns clecs-tetris.world.system.update-shape-test
  (:require [clecs-tetris.world.glass :refer [find-glass-tile]]
            [clecs-tetris.world.shape :refer [tiles]]
            [clecs-tetris.world.system.update-shape :refer :all]
            [clecs.query :as query]
            [clecs.test.mock :as mock]
            [clecs.world :as world]
            [midje.sweet :refer :all]))


(fact "-set-glass-tile sets one tile."
      (let [w (mock/mock-editable-world)]
        (-set-glass-tile w ..x.. ..y.. ..tile..) => nil
        (provided (find-glass-tile w ..x.. ..y..) => ..eid..
                  (world/set-component w
                                       ..eid..
                                       :GlassTileComponent
                                       {:x ..x..
                                        :y ..y..
                                        :tile-type ..tile..}) => anything)))


(fact "-set-tiles"
      (let [w (mock/mock-editable-world)]
        (-set-tiles w [["empty" "filled"]] 3 5 "moving") => nil
        (provided (-set-glass-tile w 4 5 "moving") => nil)))



(fact "-update-shape-location does nothing if all necessary components are not present."
      (let [w (mock/mock-editable-world)]
        (-update-shape-location w ..dt.. ..cd..) => nil
        (provided (query/all :CurrentShapeComponent
                             :CollisionComponent
                             :ShapeTargetComponent) => ..q..
                  (mock/query w ..q..) => nil)))


(fact "-update-shape-location decrements the countdown if it's not zero."
      (let [w (mock/mock-editable-world)]
        (-update-shape-location w 17 ..cd..) => nil
        (provided (query/all :CurrentShapeComponent
                             :CollisionComponent
                             :ShapeTargetComponent) => ..q..
                  (mock/query w ..q..) => [..eid..]
                  (mock/component w
                                  ..eid..
                                  :ShapeTargetComponent) => {:shape-index ..shape-index..
                                                             :x ..target-x..
                                                             :y ..target-y..
                                                             :countdown 30}
                  (world/set-component w
                                       ..eid..
                                       :ShapeTargetComponent
                                       {:shape-index ..shape-index..
                                        :x ..target-x..
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
              target-location-component {:shape-index ..target-shape-index..
                                         :x target-x
                                         :y target-y
                                         :countdown -2}]
          (-update-shape-location w ..dt.. ..cd..) => nil
          (provided (query/all :CurrentShapeComponent
                               :CollisionComponent
                               :ShapeTargetComponent) => ..q..
                    (mock/query w ..q..) => [..eid..]
                    (mock/component w ..eid.. :CollisionComponent) => {:collision? false}
                    (mock/component w ..eid.. :CurrentShapeComponent) => current-shape-component
                    (mock/component w ..eid.. :ShapeTargetComponent) => target-location-component
                    (world/set-component w
                                         ..eid..
                                         :CurrentShapeComponent
                                         {:x target-x
                                          :y target-y
                                          :shape-name ..shape-name..
                                          :shape-index ..target-shape-index..}) => nil
                    (tiles ..shape-name.. ..shape-index..) => ..tiles..
                    (-set-tiles w ..tiles.. x y "empty") => nil
                    (tiles ..shape-name.. ..target-shape-index..) => ..target-tiles..
                    (-set-tiles w ..target-tiles.. target-x target-x "moving") => nil
                    (world/set-component w
                                         ..eid..
                                         :ShapeTargetComponent
                                         {:shape-index ..target-shape-index..
                                          :x target-x
                                          :y target-y
                                          :countdown ..cd..}) => nil
                    (mock/remove-component w
                                           ..eid..
                                           :CollisionComponent) => nil))))


(fact "-update-shape-location freezes shape if there is collision."
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
                               :CollisionComponent
                               :ShapeTargetComponent) => ..q..
                    (mock/query w ..q..) => [..eid..]
                    (mock/component w ..eid.. :CollisionComponent) => {:collision? true}
                    (mock/component w ..eid.. :CurrentShapeComponent) => current-shape-component
                    (mock/component w ..eid.. :ShapeTargetComponent) => target-location-component
                    (tiles ..shape-name.. ..shape-index..) => ..tiles..
                    (-set-tiles w ..tiles.. 0 0 "filled") => nil
                    (mock/remove-entity w ..eid..) => nil))))
