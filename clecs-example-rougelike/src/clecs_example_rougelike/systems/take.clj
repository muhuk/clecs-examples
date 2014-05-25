(ns clecs-example-rougelike.systems.take
  (:require [clecs.component :refer [component-label]]
            [clecs.world :as w]
            [clecs-example-rougelike.components :refer [->Inventory]]
            [clecs-example-rougelike.entities :refer [find-entities-at]])
  (:import [clecs_example_rougelike.components Location Takeable TakeIntent]))


(declare q-takeable q-takers)


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


(defn- q-takeable [clabels]
  (and (some #{(component-label Takeable)} clabels)
       (some #{(component-label Location)} clabels)))


(defn- q-takers [clabels]
  (and (some #{(component-label TakeIntent)} clabels)
       (some #{(component-label Location)} clabels)))
