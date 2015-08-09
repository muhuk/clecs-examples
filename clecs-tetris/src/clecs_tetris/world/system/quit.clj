(ns clecs-tetris.world.system.quit
  (:require [clecs.query :as query]
            [clecs.system :refer [system]]
            [clecs.world :as world]))


(defn -process-events [w escape-hatch]
  (doseq [eid (world/query w (query/all :KeyboardInputEvent))
          :let [{:keys [id key-code]} (world/component w eid :KeyboardInputEvent)]]
      (when (= key-code "ESCAPE")
        (world/remove-entity w eid)
        (deliver escape-hatch nil))))


(defn make-quit-system [escape-hatch]
  (system {:name :quit-system
           :process-fn (fn [w _] (-process-events w escape-hatch))
           :writes #{:KeyboardInputEvent}}))
