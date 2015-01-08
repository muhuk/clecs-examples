(ns clecs-tetris.world.system.next-shape-test
  (:require [clecs.mock :refer [mock-add-entity
                                mock-component
                                mock-query
                                mock-set-component
                                mock-transaction!
                                mock-world]]
            [clecs.query :as query]
            [clecs.world :as world]
            [clecs-tetris.world.component :refer [->NextShapeComponent
                                                  NextShapeComponent]]
            [clecs-tetris.world.system.next-shape :refer :all]
            [midje.sweet :refer :all]))


(fact "-add-shape creates transaction that adds a new NextShapeComponent."
      ((-add-shape ..shape..) mock-world) => anything
      (provided (mock-add-entity) => ..eid..
                (->NextShapeComponent ..eid.. ..shape..) => ..c..
                (mock-set-component ..c..) => anything))


(fact "-set-next-shape creates a NextShapeComponent if non exists."
      (-set-next-shape mock-world [..shape.. ..next-shape..]) => [..next-shape..]
      (provided (query/all NextShapeComponent) => ..q..
                (mock-query ..q..) => nil
                (-add-shape ..shape..) => ..f..
                (mock-transaction! ..f..) => anything))


(fact "-set-next-shape does nothing if there a NextShapeComponent exists."
      (-set-next-shape mock-world ..shapes..) => ..shapes..
      (provided (query/all NextShapeComponent) => ..q..
                (mock-query ..q..) => [..eid..]))
