(ns clecs-tetris.world.system.gravity-test
  (:require [clecs-tetris.world.system.gravity :refer :all]
            [clecs.query :as query]
            [clecs.test.mock :as mock]
            [clecs.world :as world]
            [midje.sweet :refer :all]))



(fact "-apply-gravity decrements countdown if it's still positive."
      (-apply-gravity ..w.. 44 123 ..cd..) => 79)


(fact "-apply-gravity runs -move-target-location transaction and resets countdown."
      (let [w (mock/mock-editable-world)]
        (-apply-gravity w 17 -3 ..cd..) => ..cd..
        (provided (-move-target-location w ) => anything)))


(fact "-move-target-location does nothing if there's no current shape."
      (let [w (mock/mock-editable-world)]
        (-move-target-location w) => nil
        (provided (query/all :TargetLocationComponent) => ..q..
                  (mock/query w ..q..) => [])))


(fact "-move-target-location moves target location down."
      (let [w (mock/mock-editable-world)]
        (-move-target-location w) => nil
        (provided (query/all :TargetLocationComponent) => ..q..
                  (mock/query w ..q..) => [..eid..]
                  (mock/component w
                                  ..eid..
                                  :TargetLocationComponent) => {:x ..x..
                                                                :y 7
                                                                :countdown ..ct..}
                  (world/set-component w
                                       ..eid..
                                       :TargetLocationComponent
                                       {:x ..x..
                                        :y 6
                                        :countdown ..ct..}) => anything)))
