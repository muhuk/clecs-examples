(ns clecs-tetris.world.system.gravity
  (:require [clecs.query :as query]
            [clecs.world :as world]
            [clecs-tetris.world.component :refer [->CurrentShapeComponent
                                                  ->GlassTileComponent
                                                  ->TargetLocationComponent]])
  (:import [clecs_tetris.world.component
            CurrentShapeComponent
            GlassTileComponent
            TargetLocationComponent]))


(declare -move-target-location)


(defn -apply-gravity [w dt countdown-value countdown-duration]
  (if (pos? countdown-value)
    (- countdown-value dt)
    (do
      (world/transaction! w -move-target-location)
      countdown-duration)))


(defn -move-target-location [w]
  (let [q (query/all TargetLocationComponent)
        [eid] (world/query w q)]
    (when eid
      (let [{:keys [x y countdown]} (world/component w eid TargetLocationComponent)]
        (world/set-component w (->TargetLocationComponent eid x (dec y) countdown)))))
  nil)


(defn make-gravity-system [countdown-duration]
  (let [countdown-value (atom countdown-duration)]
    (fn [w dt]
      (swap! countdown-value #(-apply-gravity w dt % countdown-duration)))))
