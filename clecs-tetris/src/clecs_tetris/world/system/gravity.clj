(ns clecs-tetris.world.system.gravity
  (:require [clecs.query :as query]
            [clecs.system :refer [system]]
            [clecs.world :as world]))


(declare -move-target-location)


(defn -apply-gravity [w dt countdown-value countdown-duration]
  (if (pos? countdown-value)
    (- countdown-value dt)
    (do
      (-move-target-location w)
      countdown-duration)))


(defn -move-target-location [w]
  (let [q (query/all :ShapeTargetComponent)
        [eid] (world/query w q)]
    (when eid
      (let [{:keys [shape-index x y countdown]} (world/component w eid :ShapeTargetComponent)]
        (world/set-component w
                             eid
                             :ShapeTargetComponent
                             {:shape-index shape-index
                              :x x
                              :y (dec y)
                              :countdown countdown})
        (world/remove-component w eid :CollisionComponent))))
  nil)


(defn make-gravity-system [countdown-duration]
  ;; TODO: Use scheduling when its implemented.
  (let [countdown-value (atom countdown-duration)
        process (fn [w dt]
                  (swap! countdown-value
                         #(-apply-gravity w
                                          dt
                                          %
                                          countdown-duration)))]
    (system  {:name :gravity-system
              :process-fn process
              :writes #{:CollisionComponent
                        :ShapeTargetComponent}})))
