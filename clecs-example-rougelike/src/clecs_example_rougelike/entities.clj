(ns clecs-example-rougelike.entities
  (:require [clecs.world :as w]
            [clecs-example-rougelike.components :refer :all]))


(def tagged-entities (atom {}))


(defn find-entities-at
  ([world x y] (find-entities-at world
                                 (w/query world (constantly true))
                                 x
                                 y))
  ([world eids x y]
   (filter (fn [eid]
             (let [{x' :x y' :y} (w/component world eid Location)]
               (and (= x' x) (= y' y))))
           eids)))


(defmulti make-entity (fn [world x y e] e))


(defmethod make-entity :floor [world x y _]
  (let [eid (w/add-entity world)]
    (w/set-component world (->Location eid x y))
    (w/set-component world (->Renderable eid :floor))
    (w/set-component world (->Walkable eid))))


(defmethod make-entity :player [world x y _]
  (let [eid (w/add-entity world)]
    (swap! tagged-entities assoc :player eid)
    (doto world
      (w/set-component (->Location eid x y))
      (w/set-component (->Renderable eid :player)))))


(defmethod make-entity :potion [world x y _]
  (let [eid (w/add-entity world)]
    (doto world
      (w/set-component (->Location eid x y))
      (w/set-component (->Renderable eid :potion))
      (w/set-component (->Takeable eid))
      (w/set-component (->Name eid "Potion")))))


(defmethod make-entity :stairs-down [world x y _]
  (let [eid (w/add-entity world)]
    (doto world
      (w/set-component (->Location eid x y))
      (w/set-component (->Renderable eid :stairs-down))
      (w/set-component (->Walkable eid))
      (w/set-component (->Name eid "Stairs")))))


(defmethod make-entity :sword [world x y _]
  (let [eid (w/add-entity world)]
    (doto world
      (w/set-component (->Location eid x y))
      (w/set-component (->Renderable eid :sword))
      (w/set-component (->Takeable eid))
      (w/set-component (->Name eid "Sword")))))


(defmethod make-entity :wall [world x y _]
  (let [eid (w/add-entity world)]
    (w/set-component world (->Location eid x y))
    (w/set-component world (->Renderable eid :wall))))


(defmethod make-entity :default [world x y e]
  (throw (RuntimeException. (str "Unknown entity type " e))))
