(ns clecs-example-rougelike.systems.input
  (:require [clecs-example-rougelike.entities :refer [tagged-entities]]
            [clecs.system :refer [system]]
            [clecs.world :as w]
            [lanterna.screen :as s]))


(defn- get-inputs! [inputs screen]
  (loop [i (s/get-key screen)]
    (when i
      (do
        (swap! inputs conj i)
        (recur (s/get-key screen))))))


(defn- process-input [inputs world]
  (let [eid (:player @tagged-entities)
        c (w/component world eid :MoveIntent)
        i (atom nil)]
    (when (nil? c)
      (swap! inputs
             (fn [inputs]
               (reset! i (peek inputs))
               (if (empty? inputs)
                 inputs
                 (pop inputs))))
      (println ";; Processing input: " @i)
      (case @i
        ;; Move
        :left (w/set-component world eid :MoveIntent {:direction "left"})
        :right (w/set-component world eid :MoveIntent {:direction "right"})
        :up (w/set-component world eid :MoveIntent {:direction "up"})
        :down (w/set-component world eid :MoveIntent {:direction "down"})
        ;; Items
        \t (w/set-component world eid :TakeIntent nil)
        nil))))


(defn input-system [screen]
  (let [inputs (atom clojure.lang.PersistentQueue/EMPTY)]
    (system {:name :input
             :process-fn (fn [world _]
                           (println ";; Running input-system.")
                           (get-inputs! inputs screen)
                           (process-input inputs world))
             :writes #{:MoveIntent
                       :TakeIntent}})))
