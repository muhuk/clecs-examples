(ns clecs-example-rougelike.systems.take
  (:require [clecs-example-rougelike.entities :refer [find-entities-at]]
            [clecs.query :as query]
            [clecs.system :refer [system]]
            [clecs.world :as w]))


(def ^:private q-takeable (query/all :Takeable :Location))


(def ^:private q-takers (query/all :TakeIntent :Location))


(def take-system
  (system {:name :take
           :process-fn (fn [world dt]
                         (println ";; Running take-system.")
                         (doseq [eid (w/query world q-takers)]
                           (w/remove-component world eid :TakeIntent)
                           (let [{:keys [x y]} (w/component world eid :Location)
                                 takeable-eids (w/query world q-takeable)]
                             (doseq [takeable-eid (find-entities-at world
                                                                    takeable-eids
                                                                    x
                                                                    y)]
                               (doto world
                                 (w/remove-component takeable-eid :Location)
                                 (w/set-component takeable-eid :Inventory nil))))))
           :writes #{:Inventory :Location :TakeIntent}}))
