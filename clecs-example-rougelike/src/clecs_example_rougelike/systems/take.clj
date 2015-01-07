(ns clecs-example-rougelike.systems.take
  (:require [clecs.query :as query]
            [clecs.world :as w]
            [clecs-example-rougelike.components :refer [->Inventory
                                                        Location
                                                        Takeable
                                                        TakeIntent]]
            [clecs-example-rougelike.entities :refer [find-entities-at]]))


(def ^:private q-takeable (query/all Takeable Location))


(def ^:private q-takers (query/all TakeIntent Location))


(defn take-system [world dt]
  (println ";; Running take-system.")
  (w/transaction!  world
                   (fn [world]
                     (doseq [eid (w/query world q-takers)]
                       (w/remove-component world eid TakeIntent)
                       (let [{:keys [x y]} (w/component world eid Location)
                             takeable-eids (w/query world q-takeable)]
                         (doseq [takeable-eid (find-entities-at world
                                                                takeable-eids
                                                                x
                                                                y)]
                           (doto world
                             (w/remove-component takeable-eid Location)
                             (w/set-component (->Inventory takeable-eid)))))))))
