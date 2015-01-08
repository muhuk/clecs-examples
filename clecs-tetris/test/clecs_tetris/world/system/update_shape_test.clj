(ns clecs-tetris.world.system.update-shape-test
  (:require [clecs-tetris.world.component :refer [->CurrentShapeComponent
                                                  ->GlassTileComponent
                                                  ->TargetLocationComponent
                                                  CurrentShapeComponent
                                                  GlassTileComponent
                                                  TargetLocationComponent]]
            [clecs-tetris.world.system.update-shape :refer :all]
            [clecs.mock :refer [mock-component
                                mock-query
                                mock-set-component
                                mock-world]]
            [clecs.query :as query]
            [midje.sweet :refer :all]))


(fact "-set-glass-tile sets one tile."
      (-set-glass-tile mock-world ..x.. ..y.. ..tile..) => nil
      (provided (query/all GlassTileComponent) => ..q..
                (mock-query ..q..) => [..n-2.. ..n-1.. ..n.. ..n+1..]
                (mock-component ..n-2.. GlassTileComponent) => (->GlassTileComponent ..n-2.. ..x.. ..y-1.. anything)
                (mock-component ..n-1.. GlassTileComponent) => (->GlassTileComponent ..n-1.. ..x+1.. ..y-1.. anything)
                (mock-component ..n.. GlassTileComponent) => (->GlassTileComponent ..n-2.. ..x.. ..y.. ..other-tile..)
                (mock-component ..n+1.. GlassTileComponent) => (->GlassTileComponent ..n-1.. ..x+1.. ..y.. anything)
                (->GlassTileComponent ..n.. ..x.. ..y.. ..tile..) => ..component..
                (mock-set-component ..component..) => anything))


(fact "-update-glass-tiles erases and then re-applied the tiles."
      (-update-glass-tiles mock-world [1 2] [5 6] [[:empty :filled]]) => nil
      (provided (-set-glass-tile mock-world 2 2 :empty) => nil
                (-set-glass-tile mock-world 6 6 :moving) => nil))


(fact "-update-shape-location does nothing if there is no current shape."
      (-update-shape-location mock-world ..dt.. ..cd..) => nil
      (provided (query/all CurrentShapeComponent
                           TargetLocationComponent) => ..q..
                (mock-query ..q..) => nil))


(fact "-update-shape-location decrements the countdown if it's not zero."
      (-update-shape-location mock-world 17 ..cd..) => nil
      (provided (query/all CurrentShapeComponent
                           TargetLocationComponent) => ..q..
                (mock-query ..q..) => [..eid..]
                (mock-component ..eid..
                                TargetLocationComponent) => (->TargetLocationComponent ..eid..
                                                                                       ..target-x..
                                                                                       ..target-y..
                                                                                       30)
                (mock-set-component (->TargetLocationComponent ..eid..
                                                               ..target-x..
                                                               ..target-y..
                                                               13)) => nil))


(fact "-update-shape-location updates location and resets countdown."
      (let [shape {:tiles ..tiles..}
            [x y] [0 0]
            [target-x target-y] [1 1]
            current-shape-component (->CurrentShapeComponent ..eid.. x y shape)
            target-location-component (->TargetLocationComponent ..eid.. target-x target-y -2)]
        (-update-shape-location mock-world ..dt.. ..cd..) => nil
        (provided (query/all CurrentShapeComponent
                             TargetLocationComponent) => ..q..
                  (mock-query ..q..) => [..eid..]
                  (mock-component ..eid.. CurrentShapeComponent) => current-shape-component
                  (mock-component ..eid.. TargetLocationComponent) => target-location-component
                  (mock-set-component (->CurrentShapeComponent ..eid.. target-x target-y shape)) => nil
                  (-update-glass-tiles mock-world [x y] [target-x target-y] ..tiles..) => nil
                  (mock-set-component (->TargetLocationComponent ..eid.. target-x target-y ..cd..)) => nil)))
