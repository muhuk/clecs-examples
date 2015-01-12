(ns clecs-tetris.world.system.gravity-test
  (:require [clecs.core :refer [make-world]]
            [clecs.query :as query]
            [clecs.world :as world]
            [clecs-tetris.world.component :refer [->CurrentShapeComponent
                                                  ->GlassTileComponent
                                                  ->TargetLocationComponent
                                                  CurrentShapeComponent
                                                  GlassTileComponent
                                                  TargetLocationComponent]]
            [clecs-tetris.world.system.gravity :refer :all]
            [midje.sweet :refer :all]))



(fact "-apply-gravity decrements countdown if it's still positive."
      (-apply-gravity ..w.. 44 123 ..cd..) => 79)


(fact "-apply-gravity runs -move-target-location transaction and resets countdown."
      (let [w (make-world identity)]
        (-apply-gravity w 17 -3 ..cd..) => ..cd..
        (provided (world/transaction! w -move-target-location) => anything)))


(fact "-move-target-location does nothing if there's no current shape."
      (let [w (make-world identity)]
        (-move-target-location w) => nil
        (provided (query/all TargetLocationComponent) => ..q..
                  (world/query w ..q..) => [])))


(fact "-move-target-location moves target location down."
      (let [w (make-world identity)
            A (->TargetLocationComponent ..eid.. ..x.. 7 ..ct..)
            B (->TargetLocationComponent ..eid.. ..x.. 6 ..ct..)]
        (-move-target-location w) => nil
        (provided (query/all TargetLocationComponent) => ..q..
                  (world/query w ..q..) => [..eid..]
                  (world/component w ..eid.. TargetLocationComponent) => A
                  (world/set-component w B) => anything)))
