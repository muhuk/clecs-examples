(ns clecs-tetris.world.system.quit-test
  (:require [clecs-tetris.world.system.quit :refer :all]
            [clecs.test.mock :as mock]
            [clecs.query :as query]
            [midje.sweet :refer :all]))


(fact "-process-events ..."
      (let [w (mock/mock-editable-world)]
        (-process-events w ..escape-hatch..) => nil
        (provided (query/all :KeyboardInputEvent) => ..q..
                  (mock/query w ..q..) => [..eid..]
                  (mock/component w
                                  ..eid..
                                  :KeyboardInputEvent) => {:id irrelevant
                                                            :key-code "ESCAPE"}
                  (mock/remove-entity w ..eid..) => irrelevant
                  (deliver ..escape-hatch.. nil) => irrelevant)))
