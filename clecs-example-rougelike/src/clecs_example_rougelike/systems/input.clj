(ns clecs-example-rougelike.systems.input
  (:require [clecs.world :as w]
            [clecs-example-rougelike.components :refer [->MoveIntent
                                                        ->TakeIntent
                                                        MoveIntent
                                                        Renderable
                                                        TakeIntent]]
            [clecs-example-rougelike.entities :refer [tagged-entities]]
            [lanterna.screen :as s]))


(defn- get-inputs! [inputs screen]
  (loop [i (s/get-key screen)]
    (when i
      (do
        (swap! inputs conj i)
        (recur (s/get-key screen))))))


(defn- process-input [inputs world]
  (let [eid (:player @tagged-entities)
        c (w/component world eid MoveIntent)
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
        :left (w/transaction! world #(w/set-component % (->MoveIntent eid :left)))
        :right (w/transaction! world #(w/set-component % (->MoveIntent eid :right)))
        :up (w/transaction! world #(w/set-component % (->MoveIntent eid :up)))
        :down (w/transaction! world #(w/set-component % (->MoveIntent eid :down)))
        ;; Items
        \t (w/transaction! world #(w/set-component % (->TakeIntent eid)))
        nil))))


(defn input-system [screen]
  (let [inputs (atom clojure.lang.PersistentQueue/EMPTY)]
    (fn [world _]
      (println ";; Running input-system.")
      (get-inputs! inputs screen)
      (process-input inputs world))))
