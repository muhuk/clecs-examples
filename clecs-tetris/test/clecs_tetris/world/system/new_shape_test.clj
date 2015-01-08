(ns clecs-tetris.world.system.new-shape-test
  (:require [clecs.mock :refer [mock-add-entity
                                mock-component
                                mock-query
                                mock-remove-entity
                                mock-set-component
                                mock-world]]
            [clecs.query :as query]
            [clecs.world :as world]
            [clecs-tetris.world.component :refer [->CurrentShapeComponent
                                                  ->NextShapeComponent
                                                  ->TargetLocationComponent
                                                  CurrentShapeComponent
                                                  NextShapeComponent
                                                  TargetLocationComponent]]
            [clecs-tetris.world.system.new-shape :refer :all]
            [midje.sweet :refer :all]))


(fact "-can-create-new-shape? does nothing if there is a current shape."
      (-can-create-new-shape? mock-world) => anything
      (provided (query/all CurrentShapeComponent TargetLocationComponent) => ..q..
                (mock-query ..q..) => [..eid..]))


(fact "-can-create-new-shape? does nothing if there is no next shape to convert."
      (-can-create-new-shape? mock-world) => anything
      (provided (query/all CurrentShapeComponent TargetLocationComponent) => ..q1..
                (mock-query ..q1..) => nil
                (query/all NextShapeComponent) => ..q2..
                (mock-query ..q2..) => nil))


(fact "-create-new-shape converts next shape into a current shape."
      (-create-new-shape mock-world ..x.. ..y.. ..cd..) => anything
      (provided (query/all NextShapeComponent) => ..q..
                (mock-query ..q..) => [..eid1..]
                (mock-component ..eid1.. NextShapeComponent) => (->NextShapeComponent ..eid1.. ..shape..)
                (mock-add-entity) => ..eid2..
                (->CurrentShapeComponent ..eid2.. ..x.. ..y.. ..shape..) => ..current-shape-component..
                (->TargetLocationComponent ..eid2.. ..x.. ..y.. ..cd..) => ..target-location-component..
                (mock-set-component ..current-shape-component..) => anything
                (mock-set-component ..target-location-component..) => anything
                (mock-remove-entity ..eid1..) => anything))
