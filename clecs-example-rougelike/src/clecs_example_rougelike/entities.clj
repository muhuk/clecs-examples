(ns clecs-example-rougelike.entities
  (:require [clecs.world :as w]))


(def tagged-entities (atom {}))


(defn find-entities-at
  ([world x y] (find-entities-at world
                                 (w/query world (constantly true))
                                 x
                                 y))
  ([world eids x y]
   (filter (fn [eid]
             (let [{x' :x y' :y} (w/component world eid :Location)]
               (and (= x' x) (= y' y))))
           eids)))


(defmulti make-entity (fn [world x y e] e))


(defmethod make-entity :floor [world x y _]
  (let [eid (w/add-entity world)]
    (w/set-component world eid :Location {:x x :y y})
    (w/set-component world eid :Renderable {:sprite "floor"})
    (w/set-component world eid :Walkable nil)))


(defmethod make-entity :player [world x y _]
  (let [eid (w/add-entity world)]
    (swap! tagged-entities assoc :player eid)
    (doto world
      (w/set-component eid :Location {:x x :y y})
      (w/set-component eid :Renderable {:sprite "player"}))))


(defmethod make-entity :potion [world x y _]
  (let [eid (w/add-entity world)]
    (doto world
      (w/set-component eid :Location {:x x :y y})
      (w/set-component eid :Renderable {:sprite "potion"})
      (w/set-component eid :Takeable nil)
      (w/set-component eid :Name {:name "Potion"}))))


(defmethod make-entity :stairs-down [world x y _]
  (let [eid (w/add-entity world)]
    (doto world
      (w/set-component eid :Location {:x x :y y})
      (w/set-component eid :Renderable {:sprite "stairs-down"})
      (w/set-component eid :Walkable nil)
      (w/set-component eid :Name {:name "Stairs"}))))


(defmethod make-entity :sword [world x y _]
  (let [eid (w/add-entity world)]
    (doto world
      (w/set-component eid :Location {:x x :y y})
      (w/set-component eid :Renderable {:sprite "sword"})
      (w/set-component eid :Takeable nil)
      (w/set-component eid :Name {:name "Sword"}))))


(defmethod make-entity :wall [world x y _]
  (let [eid (w/add-entity world)]
    (w/set-component world eid :Location {:x x :y y})
    (w/set-component world eid :Renderable {:sprite "wall"})))


(defmethod make-entity :default [world x y e]
  (throw (RuntimeException. (str "Unknown entity type " e))))
