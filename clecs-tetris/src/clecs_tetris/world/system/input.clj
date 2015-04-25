(ns clecs-tetris.world.system.input
  (:require [clecs.system :refer [system]]
            [clecs.world :as world]))


(defn -process-inputs [w event-queue start-id]
  (let [e (agent [])]
    (swap! event-queue (fn [events] (send e (constantly events)) []))
    (await e)
    (doseq [[idx [event-type event-info]] (map-indexed vector @e)]
      (when (= event-type :keyboard-event)
        (world/set-component w
                             (world/add-entity w)
                             :KeyboardInputEvent
                             {:id (+ idx start-id)
                              :key-code event-info})))
    (+ start-id (count @e))))


(defn make-input-system [event-queue]
  (let [id (atom 1)]
    (system {:name :input-system
             :process-fn (fn [w _]
                           (swap! id
                                  #(-process-inputs w event-queue %)))
             :writes #{:KeyboardInputEvent}})))
