(ns clecs-tetris.world.system.apply-gravity
  (:require [clecs.query :as query]
            [clecs.system :refer [system]]
            [clecs.world :as world]))


(declare -get-gravity
         -move-target-location)


(defn -apply-gravity [w dt]
  (let [{countdown-value :countdown
         countdown-duration :acceleration
         eid :gravity-eid :as c} (-get-gravity w)
        new-countdown-value (if (pos? countdown-value)
                              (- countdown-value dt)
                              (do
                                (-move-target-location w)
                                countdown-duration))]
    (world/set-component w
                         eid
                         :GravityComponent
                         (-> c
                             (dissoc :gravity-eid)
                             (assoc :countdown new-countdown-value)))
    nil))


(defn -get-gravity [w]
  (let [[eid] (world/query w (query/all :GravityComponent))]
    (assoc (world/component w eid :GravityComponent)
      :gravity-eid eid)))


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


(def apply-gravity-system
  (system  {:name :gravity-system
              :process-fn -apply-gravity
              :writes #{:CollisionComponent
                        :GravityComponent
                        :ShapeTargetComponent}}))
