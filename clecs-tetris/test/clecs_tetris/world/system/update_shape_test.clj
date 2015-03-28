(ns clecs-tetris.world.system.update-shape-test
  (:require [clecs-tetris.world.glass :refer [find-glass-tile]]
            [clecs-tetris.world.shape :refer [tiles]]
            [clecs-tetris.world.system.update-shape :refer :all]
            [clecs.query :as query]
            [clecs.test.mock :as mock]
            [clecs.world :as world]
            [midje.sweet :refer :all]))


(fact "-freeze-shape"
      (let [w (mock/mock-editable-world)]
        (-freeze-shape w 3 5 [["empty" "filled"]]) => nil
        (provided (-set-glass-tile w 4 5 "filled") => nil)))


(fact "-move-shape erases and then re-applied the tiles."
      (let [w (mock/mock-editable-world)]
        (-move-shape w [1 2] [5 6] [["empty" "filled"]]) => nil
        (provided (-set-glass-tile w 2 2 "empty") => nil
                  (-set-glass-tile w 6 6 "moving") => nil)))


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


(fact "-update-shape-location does nothing if all necessary components are not present."
      (let [w (mock/mock-editable-world)]
        (-update-shape-location w ..dt.. ..cd..) => nil
        (provided (query/all :CurrentShapeComponent
                             :CollisionComponent
                             :TargetLocationComponent) => ..q..
                  (mock/query w ..q..) => nil)))


(fact "-update-shape-location decrements the countdown if it's not zero."
      (let [w (mock/mock-editable-world)]
        (-update-shape-location w 17 ..cd..) => nil
        (provided (query/all :CurrentShapeComponent
                             :CollisionComponent
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
                               :CollisionComponent
                               :TargetLocationComponent) => ..q..
                    (mock/query w ..q..) => [..eid..]
                    (mock/component w ..eid.. :CollisionComponent) => {:collision? false}
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
                    (-move-shape w [x y] [target-x target-y] ..tiles..) => nil
                    (world/set-component w
                                         ..eid..
                                         :TargetLocationComponent
                                         {:x target-x :y target-y :countdown ..cd..}) => nil
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
                               :TargetLocationComponent) => ..q..
                    (mock/query w ..q..) => [..eid..]
                    (mock/component w ..eid.. :CollisionComponent) => {:collision? true}
                    (mock/component w ..eid.. :CurrentShapeComponent) => current-shape-component
                    (mock/component w ..eid.. :TargetLocationComponent) => target-location-component
                    (tiles ..shape-name.. ..shape-index..) => ..tiles..
                    (-freeze-shape w 0 0 ..tiles..) => nil
                    (mock/remove-component w
                                           ..eid..
                                           :CurrentShapeComponent) => nil
                    (mock/remove-component w
                                           ..eid..
                                           :TargetLocationComponent) => nil
                    (mock/remove-component w
                                           ..eid..
                                           :CollisionComponent) => nil))))
