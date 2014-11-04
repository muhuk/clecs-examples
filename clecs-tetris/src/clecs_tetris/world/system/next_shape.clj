(ns clecs-tetris.world.system.next-shape
  (:require [clecs-tetris.world.component :refer [->NextShapeComponent]]
            [clecs.query :as query]
            [clecs.world :as world])
  (:import (clecs_tetris.world.component NextShapeComponent)))


(defn -add-shape [shape]
  (fn [w]
    (let [eid (world/add-entity w)
          c (->NextShapeComponent eid shape)]
      (world/set-component w c))))


(defn -set-next-shape [w shapes]
  (let [q (query/all NextShapeComponent)]
    (if (empty? (world/query w q))
      (do
        (world/transaction! w (-add-shape (first shapes)))
        (rest shapes))
      shapes)))


(defn make-next-shape-system [shapes]
  (let [shapes-atom (atom shapes)]
    (fn [w _]
      (swap! shapes-atom (partial -set-next-shape w)))))
