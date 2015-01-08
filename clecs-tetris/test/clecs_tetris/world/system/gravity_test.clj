(ns clecs-tetris.world.system.gravity-test
  (:require [clecs.mock :refer [mock-add-entity
                                mock-component
                                mock-query
                                mock-remove-entity
                                mock-set-component
                                mock-transaction!
                                mock-world]]
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
             (-apply-gravity mock-world 17 -3 ..cd..) => ..cd..
             (provided (mock-transaction! -move-target-location) => anything))


(fact "-move-target-location does nothing if there's no current shape."
      (-move-target-location mock-world) => nil
      (provided (query/all TargetLocationComponent) => ..q..
                (mock-query ..q..) => []))


(fact "-move-target-location moves target location down."
      (-move-target-location mock-world) => nil
      (provided (query/all TargetLocationComponent) => ..q..
                (mock-query ..q..) => [..eid..]
                (mock-component ..eid..
                                TargetLocationComponent) => (->TargetLocationComponent ..eid..
                                                                                       ..x..
                                                                                       7
                                                                                       ..ct..)
                (mock-set-component (->TargetLocationComponent ..eid..
                                                               ..x..
                                                               6
                                                               ..ct..)) => anything))
