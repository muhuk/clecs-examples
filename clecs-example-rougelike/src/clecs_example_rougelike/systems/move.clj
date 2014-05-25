(ns clecs-example-rougelike.systems.move
  (:require [clecs.component :refer [component-label]]
            [clecs.world :as w]
            [clecs-example-rougelike.components :refer [->Location]])
  (:import [clecs_example_rougelike.components Location MoveIntent Walkable]))


(declare q-movers q-walkables walkable?)


(defn- displace [x y direction]
  (case direction
    :left [(dec x) y]
    :right [(inc x) y]
    :up [x (dec y)]
    :down [x (inc y)]))


(defn move-system [world dt]
  (println ";; Running move-system.")
  (w/transaction! world
                  (fn [world]
                    (doseq [eid (w/query world q-movers)]
                      (let [{:keys [direction]} (w/component world eid MoveIntent)
                            {:keys [x y]} (w/component world eid Location)
                            [x y] (displace x y direction)]
                        (when (walkable? world x y)
                          (w/set-component world (->Location eid x y)))
                        (w/remove-component world eid MoveIntent))))))


(defn- q-movers [clabels]
  (and (some #{(component-label MoveIntent)} clabels)
       (some #{(component-label Location)} clabels)))


(defn- q-walkables [clabels]
  (and (some #{(component-label Walkable)} clabels)
       (some #{(component-label Location)} clabels)))


(defn- walkable? [world x y]
  (some (fn [eid]
          (let [{wx :x wy :y} (w/component world eid Location)]
            (and (= x wx) (= y wy))))
        (w/query world q-walkables)))
