(ns clecs-tetris.world.system.new-shape-test
  (:require [clecs.core :refer [make-world]]
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
      (let [w (make-world identity)]
        (-can-create-new-shape? w) => anything
        (provided (query/all CurrentShapeComponent TargetLocationComponent) => ..q..
                  (world/query w ..q..) => [..eid..])))


(fact "-can-create-new-shape? does nothing if there is no next shape to convert."
      (let [w (make-world identity)]
        (-can-create-new-shape? w) => anything
        (provided (query/all CurrentShapeComponent TargetLocationComponent) => ..q1..
                  (world/query w ..q1..) => nil
                  (query/all NextShapeComponent) => ..q2..
                  (world/query w ..q2..) => nil)))


(fact "-create-new-shape converts next shape into a current shape."
      (let [w (make-world identity)]
        (-create-new-shape w ..x.. ..y.. ..cd..) => anything
        (provided (query/all NextShapeComponent) => ..q..
                  (world/query w ..q..) => [..eid1..]
                  (world/component w ..eid1.. NextShapeComponent) => (->NextShapeComponent ..eid1.. ..shape..)
                  (world/add-entity w) => ..eid2..
                  (->CurrentShapeComponent ..eid2.. ..x.. ..y.. ..shape..) => ..current-shape-component..
                  (->TargetLocationComponent ..eid2.. ..x.. ..y.. ..cd..) => ..target-location-component..
                  (world/set-component w ..current-shape-component..) => anything
                  (world/set-component w ..target-location-component..) => anything
                  (world/remove-entity w ..eid1..) => anything)))
