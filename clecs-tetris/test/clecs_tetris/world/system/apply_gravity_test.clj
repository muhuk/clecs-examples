(ns clecs-tetris.world.system.apply-gravity-test
  (:require [clecs-tetris.world.system.apply-gravity :refer :all]
            [clecs.query :as query]
            [clecs.test.mock :as mock]
            [clecs.world :as world]
            [midje.sweet :refer :all]))



(fact "-apply-gravity decrements countdown if it's still positive."
      (-apply-gravity ..w.. 44) => nil
      (provided (-get-gravity ..w..) => {:countdown 123
                                         :acceleration ..cd..
                                         :gravity-eid ..eid..}
                (world/set-component ..w..
                                     ..eid..
                                     :GravityComponent
                                     {:countdown 79
                                      :acceleration ..cd..}) => irrelevant))


(fact "-apply-gravity runs -move-target-location transaction and resets countdown."
      (-apply-gravity ..w.. 17) => nil
      (provided (-get-gravity ..w..) => {:countdown -3
                                         :acceleration ..cd..
                                         :gravity-eid ..eid..}
                (-move-target-location ..w..) => anything
                (world/set-component ..w..
                                     ..eid..
                                     :GravityComponent
                                     {:countdown ..cd..
                                      :acceleration ..cd..}) => irrelevant))


(fact "-get-gravity."
      (let [w (mock/mock-editable-world)]
        (-get-gravity w) => {:countdown ..ct..
                             :acceleration ..acc..
                             :gravity-eid ..eid..}
        (provided (query/all :GravityComponent) => ..q..
                  (mock/query w ..q..) => [..eid..]
                  (mock/component w ..eid.. :GravityComponent) => {:countdown ..ct..
                                                                   :acceleration ..acc..})))


(fact "-move-target-location does nothing if there's no current shape."
      (let [w (mock/mock-editable-world)]
        (-move-target-location w) => nil
        (provided (query/all :ShapeTargetComponent) => ..q..
                  (mock/query w ..q..) => [])))


(fact "-move-target-location moves target location down."
      (let [w (mock/mock-editable-world)]
        (-move-target-location w) => nil
        (provided (query/all :ShapeTargetComponent) => ..q..
                  (mock/query w ..q..) => [..eid..]
                  (mock/component w
                                  ..eid..
                                  :ShapeTargetComponent) => {:shape-index ..shape-index..
                                                             :x ..x..
                                                             :y 7
                                                             :countdown ..ct..}
                  (world/set-component w
                                       ..eid..
                                       :ShapeTargetComponent
                                       {:shape-index ..shape-index..
                                        :x ..x..
                                        :y 6
                                        :countdown ..ct..}) => nil
                  (mock/remove-component w
                                         ..eid..
                                         :CollisionComponent) => nil)))
