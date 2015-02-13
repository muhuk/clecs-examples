(ns clecs-tetris.world.system.next-shape-test
  (:require [clecs-tetris.world.system.next-shape :refer :all]
            [clecs.query :as query]
            [clecs.test.mock :as mock]
            [clecs.world :as world]
            [midje.sweet :refer :all]))


(fact "-add-shape creates transaction that adds a new NextShapeComponent."
      (let [w (mock/mock-editable-world)]
        (-add-shape w {:shape-name ..shape-name..
                       :tiles ..tiles..
                       :shape-index ..shape-index..}) => anything
        (provided (mock/add-entity w) => ..eid..
                  (world/set-component w
                                       ..eid..
                                       :NextShapeComponent
                                       {:shape-name ..shape-name..
                                        :shape-index ..shape-index..}) => anything)))


(fact "-set-next-shape creates a NextShapeComponent if non exists."
      (let [w (mock/mock-editable-world)]
        (-set-next-shape w [..shape.. ..next-shape..]) => [..next-shape..]
        (provided (query/all :NextShapeComponent) => ..q..
                  (mock/query w ..q..) => nil
                  (-add-shape w ..shape..) => anything)))


(fact "-set-next-shape does nothing if there a NextShapeComponent exists."
      (let [w (mock/mock-editable-world)]
        (-set-next-shape w ..shapes..) => ..shapes..
        (provided (query/all :NextShapeComponent) => ..q..
                  (mock/query w ..q..) => [..eid..])))
