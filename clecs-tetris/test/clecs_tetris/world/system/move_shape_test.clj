(ns clecs-tetris.world.system.move-shape-test
  (:require [clecs-tetris.world.shape :refer [rotate-shape]]
            [clecs-tetris.world.system.move-shape :refer :all]
            [clecs.query :as query]
            [clecs.test.mock :as mock]
            [clecs.world :as world]
            [midje.sweet :refer :all]))


(fact "-move-left"
      (-move-left anything {:x 3}) => {:x 2})


(fact "-move-right"
      (-move-right anything {:x 3}) => {:x 4})


(fact "-process-events calls -rotate on keyboard event \"LEFT\""
      (let [w (mock/mock-editable-world)]
        (-process-events w) => nil
        (provided (query/all :KeyboardInputEvent) => ..q..
                  (mock/query w ..q..) => [..eid..]
                  (mock/component w
                                  ..eid..
                                  :KeyboardInputEvent) => {:id irrelevant
                                                           :key-code "LEFT"}
                  (-update-current-shape w -move-left) => irrelevant
                  (mock/remove-entity w ..eid..) => irrelevant)))


(fact "-process-events calls -rotate on keyboard event \"RIGHT\""
      (let [w (mock/mock-editable-world)]
        (-process-events w) => nil
        (provided (query/all :KeyboardInputEvent) => ..q..
                  (mock/query w ..q..) => [..eid..]
                  (mock/component w
                                  ..eid..
                                  :KeyboardInputEvent) => {:id irrelevant
                                                           :key-code "RIGHT"}
                  (-update-current-shape w -move-right) => irrelevant
                  (mock/remove-entity w ..eid..) => irrelevant)))


(fact "-process-events calls -rotate on keyboard event \"UP\""
      (let [w (mock/mock-editable-world)]
        (-process-events w) => nil
        (provided (query/all :KeyboardInputEvent) => ..q..
                  (mock/query w ..q..) => [..eid..]
                  (mock/component w
                                  ..eid..
                                  :KeyboardInputEvent) => {:id irrelevant
                                                           :key-code "UP"}
                  (-update-current-shape w -rotate) => irrelevant
                  (mock/remove-entity w ..eid..) => irrelevant)))


(fact "-rotate"
      (-rotate {:shape-name ..shape-name..}
               {:shape-index ..shape-index..}) => {:shape-index ..new-shape-index..}
      (provided (rotate-shape ..shape-name..
                              ..shape-index..) => {:shape-index ..new-shape-index..}))


(fact "-update-current-shape uses passed function."
      (let [w (mock/mock-editable-world)]
        (-update-current-shape w --f--) => nil
        (provided
         (query/all :CurrentShapeComponent) => ..q..
         (mock/query w ..q..) => [..eid..]
         (mock/component w ..eid.. :CurrentShapeComponent) => ..current-shape-component..
         (mock/component w ..eid.. :ShapeTargetComponent) => ..shape-target-component..
         (--f-- ..current-shape-component.. ..shape-target-component..) => ..new-cdata..
         (world/set-component w
                              ..eid..
                              :ShapeTargetComponent
                              ..new-cdata..) => irrelevant)))
