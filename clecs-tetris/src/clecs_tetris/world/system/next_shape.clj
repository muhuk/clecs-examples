(ns clecs-tetris.world.system.next-shape
  (:require [clecs.query :as query]
            [clecs.system :refer [system]]
            [clecs.world :as world]))


(defn -add-shape [w shape]
  (let [eid (world/add-entity w)]
    (world/set-component w eid :NextShapeComponent (dissoc shape :tiles))))


(defn -set-next-shape [w shapes]
  (let [q (query/all :NextShapeComponent)]
    (if (empty? (world/query w q))
      (do
        (-add-shape w (first shapes))
        (rest shapes))
      shapes)))


(defn make-next-shape-system [shapes]
  (let [shapes-atom (atom shapes)
        process (fn [w _]
                  (swap! shapes-atom (partial -set-next-shape w)))]
    (system {:name :next-shape-system
             :process-fn process
             :writes #{:NextShapeComponent}})))
