(ns clecs-tetris.world.system.next-shape-test
  (:require [clecs.core :refer [make-world]]
            [clecs.query :as query]
            [clecs.world :as world]
            [clecs-tetris.world.component :refer [->NextShapeComponent
                                                  NextShapeComponent]]
            [clecs-tetris.world.system.next-shape :refer :all]
            [midje.sweet :refer :all]))


(fact "-add-shape creates transaction that adds a new NextShapeComponent."
      (let [w (make-world identity)]
        ((-add-shape ..shape..) w) => anything
        (provided (world/add-entity w) => ..eid..
                  (->NextShapeComponent ..eid.. ..shape..) => ..c..
                  (world/set-component w ..c..) => anything)))


(fact "-set-next-shape creates a NextShapeComponent if non exists."
      (let [w (make-world identity)]
        (-set-next-shape w [..shape.. ..next-shape..]) => [..next-shape..]
        (provided (query/all NextShapeComponent) => ..q..
                  (world/query w ..q..) => nil
                  (-add-shape ..shape..) => ..f..
                  (world/transaction! w ..f..) => anything)))


(fact "-set-next-shape does nothing if there a NextShapeComponent exists."
      (let [w (make-world identity)]
        (-set-next-shape w ..shapes..) => ..shapes..
        (provided (query/all NextShapeComponent) => ..q..
                  (world/query w ..q..) => [..eid..])))
