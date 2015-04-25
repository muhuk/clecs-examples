(ns clecs-tetris.world.system.new-shape-test
  (:require [clecs-tetris.world.system.new-shape :refer :all]
            [clecs.query :as query]
            [clecs.test.mock :as mock]
            [clecs.world :as world]
            [midje.sweet :refer :all]))


(fact "-can-create-new-shape? does nothing if there is a current shape."
      (let [w (mock/mock-editable-world)]
        (-can-create-new-shape? w) => anything
        (provided (query/all :CurrentShapeComponent
                             :ShapeTargetComponent) => ..q..
                  (mock/query w ..q..) => [..eid..])))


(fact "-can-create-new-shape? does nothing if there is no next shape to convert."
      (let [w (mock/mock-editable-world)]
        (-can-create-new-shape? w) => anything
        (provided (query/all :CurrentShapeComponent
                             :ShapeTargetComponent) => ..q1..
                  (mock/query w ..q1..) => nil
                  (query/all :NextShapeComponent) => ..q2..
                  (mock/query w ..q2..) => nil)))


(fact "-create-new-shape converts next shape into a current shape."
      (let [w (mock/mock-editable-world)]
        (-create-new-shape w ..x.. ..y.. ..cd..) => anything
        (provided (query/all :NextShapeComponent) => ..q..
                  (mock/query w ..q..) => [..eid1..]
                  (mock/component w
                                  ..eid1..
                                  :NextShapeComponent) => {:shape-name ..shape-name..
                                                           :shape-index ..shape-index..}
                  (mock/add-entity w) => ..eid2..
                  (world/set-component w
                                       ..eid2..
                                       :CurrentShapeComponent
                                       {:x ..x..
                                        :y ..y..
                                        :shape-name ..shape-name..
                                        :shape-index ..shape-index..}) => anything
                  (world/set-component w
                                       ..eid2..
                                       :ShapeTargetComponent
                                       {:shape-index ..shape-index..
                                        :x ..x..
                                        :y ..y..
                                        :countdown ..cd..}) => anything
                  (mock/remove-entity w ..eid1..) => anything)))
