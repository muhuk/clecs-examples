(ns clecs-example-rougelike.systems.move
  (:require [clecs.query :as query]
            [clecs.world :as w]
            [clecs-example-rougelike.components :refer [->Location
                                                        Location
                                                        MoveIntent
                                                        Walkable]]))


(declare walkable?)


(def ^:private q-movers (query/all MoveIntent Location))


(def ^:private q-walkables (query/all Walkable Location))


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


(defn- walkable? [world x y]
  (some (fn [eid]
          (let [{wx :x wy :y} (w/component world eid Location)]
            (and (= x wx) (= y wy))))
        (w/query world q-walkables)))
