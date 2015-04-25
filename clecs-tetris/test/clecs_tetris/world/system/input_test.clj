(ns clecs-tetris.world.system.input-test
  (:require [clecs-tetris.world.system.input :refer :all]
            [clecs.test.mock :as mock]
            [clecs.world :as world]
            [midje.sweet :refer :all]))


(fact "-process-inputs does nothing if there are no inputs."
      (-process-inputs (mock/mock-editable-world) (atom []) 99) => 99)


(fact "-process-inputs generate KeyboardInputEvent components."
      (let [w (mock/mock-editable-world)
            event-queue (atom [[:keyboard-event "UP"]
                               [:keyboard-event "RIGHT"]
                               [:keyboard-event "ESCAPE"]])
            start-id 42
            end-id 45]
        (-process-inputs w event-queue start-id) => end-id
        (provided (mock/add-entity w) =streams=> [..eid-1..
                                                  ..eid-2..
                                                  ..eid-3..] :times 3
                  (world/set-component w
                                       ..eid-1..
                                       :KeyboardInputEvent
                                       {:id 42
                                        :key-code "UP"}) => anything
                  (world/set-component w
                                       ..eid-2..
                                       :KeyboardInputEvent
                                       {:id 43
                                        :key-code "RIGHT"}) => anything
                  (world/set-component w
                                       ..eid-3..
                                       :KeyboardInputEvent
                                       {:id 44
                                        :key-code "ESCAPE"}) => anything)
        @event-queue => []))
