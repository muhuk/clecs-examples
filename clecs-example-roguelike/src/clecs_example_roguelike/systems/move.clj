(ns clecs-example-roguelike.systems.move
  (:require [clecs.query :as query]
            [clecs.system :refer [system]]
            [clecs.world :as w]))


(declare walkable?)


(def ^:private q-movers (query/all :MoveIntent :Location))


(def ^:private q-walkables (query/all :Walkable :Location))


(defn- displace [x y direction]
  (case direction
    "left" [(dec x) y]
    "right" [(inc x) y]
    "up" [x (dec y)]
    "down" [x (inc y)]))


(def move-system
  (system {:name :move
           :process-fn (fn [world dt]
                         (println ";; Running move-system.")
                         (doseq [eid (w/query world q-movers)]
                           (let [{:keys [direction]} (w/component world eid :MoveIntent)
                                 {:keys [x y]} (w/component world eid :Location)
                                 [x y] (displace x y direction)]
                             (when (walkable? world x y)
                               (w/set-component world eid :Location {:x x :y y}))
                             (w/remove-component world eid :MoveIntent))))
           :reads #{:Walkable}
           :writes #{:Location :MoveIntent}}))


(defn- walkable? [world x y]
  (some (fn [eid]
          (let [{wx :x wy :y} (w/component world eid :Location)]
            (and (= x wx) (= y wy))))
        (w/query world q-walkables)))
