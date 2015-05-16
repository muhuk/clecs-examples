(ns clecs-tetris.world.system.accelerate-gravity-test
  (:require [clecs-tetris.world.system.accelerate-gravity :refer :all]
            [clecs.query :as query]
            [clecs.test.mock :as mock]
            [clecs.world :as world]
            [midje.sweet :refer :all]))


(facts "-accelerate does nothing if there are no DOWN events."
       (let [w (mock/mock-editable-world)]
         (-accelerate w ..dt.. ..ct.. ..acc.. ..acc-time..) => nil
         (provided (query/all :KeyboardInputEvent) => ..q..
                   (mock/query w ..q..) => []
                   (-reduce-countdown w ..dt.. ..ct..) => irrelevant))
       (let [w (mock/mock-editable-world)]
         (-accelerate w ..dt.. ..ct.. ..acc.. ..acc-time..) => nil
         (provided (query/all :KeyboardInputEvent) => ..q..
                   (mock/query w ..q..) => [..eid..]
                   (mock/component w ..eid.. :KeyboardInputEvent) => {:key-code "NOTDOWN"}
                   (-reduce-countdown w ..dt.. ..ct..) => irrelevant)))


(fact "-accelerate processes DOWN events."
       (let [w (mock/mock-editable-world)]
         (-accelerate w ..dt.. 1000 5 ..acc-time..) => nil
         (provided (query/all :KeyboardInputEvent) => ..q..
                   (mock/query w ..q..) => [..eid..]
                   (mock/component w ..eid.. :KeyboardInputEvent) => {:key-code "DOWN"}
                   (-set-acceleration-countdown w 200 ..acc-time..) => irrelevant
                   (mock/remove-entity w ..eid..) => irrelevant)))


(fact "-reduce-countdown subtracts dt from current countdown value."
      (let [w (mock/mock-editable-world)]
        (-reduce-countdown w 17 ..ct..) => nil
        (provided (query/all :GravityComponent) => ..q..
                  (mock/query w ..q..) => [..eid..]
                  (mock/component w ..eid.. :GravityComponent) => {:acceleration-countdown 51}
                  (world/set-component w
                                       ..eid..
                                       :GravityComponent
                                       {:acceleration-countdown 34}) => irrelevant)))


(fact "-reduce-countdown sets default countdown when acceleration countdown is zero."
      (let [w (mock/mock-editable-world)]
        (-reduce-countdown w 9 ..ct..) => nil
        (provided (query/all :GravityComponent) => ..q..
                  (mock/query w ..q..) => [..eid..]
                  (mock/component w ..eid.. :GravityComponent) => {:acceleration-countdown 5
                                                                   :acceleration ..old-countdown..}
                  (world/set-component w
                                       ..eid..
                                       :GravityComponent
                                       {:acceleration-countdown 0
                                        :acceleration ..ct..}) => irrelevant)))


(fact "-reduce-countdown creates a :GravityComponent component if none exists."
      (let [w (mock/mock-editable-world)]
        (-reduce-countdown w 9 ..ct..) => nil
        (provided (query/all :GravityComponent) => ..q..
                  (mock/query w ..q..) => []
                  (mock/add-entity w) => ..eid..
                  (world/set-component w
                                       ..eid..
                                       :GravityComponent
                                       {:acceleration ..ct..
                                        :acceleration-countdown 0
                                        :countdown ..ct..}) => irrelevant)))


(fact "-set-acceleration-countdown."
      (let [w (mock/mock-editable-world)]
        (-set-acceleration-countdown w ..acc.. ..acc-time..) => nil
        (provided (query/all :GravityComponent) => ..q..
                  (mock/query w ..q..) => [..eid..]
;;                   (mock/component w ..eid.. :GravityComponent) => {:acceleration ..old-acc..
;;                                                                    :acceleration-countdown ..old-ct..}
                  (world/set-component w
                                       ..eid..
                                       :GravityComponent
                                       {:acceleration-countdown ..acc-time..
                                        :acceleration ..acc..
                                        :countdown 0}) => irrelevant)))
