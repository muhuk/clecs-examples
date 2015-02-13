(ns clecs-tetris.world.system.new-shape
  (:require [clecs.query :as query]
            [clecs.system :refer [system]]
            [clecs.world :as world]))


(defn -can-create-new-shape? [w]
  (and (empty? (world/query w (query/all :CurrentShapeComponent
                                         :TargetLocationComponent)))
       (not (empty? (world/query w (query/all :NextShapeComponent))))))


(defn -create-new-shape [w x y countdown-duration]
  (let [[next-shape-eid] (world/query w (query/all :NextShapeComponent))
        {:keys [shape-name
                shape-index]} (world/component w next-shape-eid :NextShapeComponent)
        current-shape-eid (world/add-entity w)]
    (world/set-component w
                         current-shape-eid
                         :CurrentShapeComponent
                         {:x x
                          :y y
                          :shape-name shape-name
                          :shape-index shape-index})
    (world/set-component w
                         current-shape-eid
                         :TargetLocationComponent
                         {:x x :y y :countdown countdown-duration})
    (world/remove-entity w next-shape-eid)))


(defn make-new-shape-system [x y countdown-duration]
  (system {:name :new-shape-system
           :process-fn (fn [w _]
                         (when (-can-create-new-shape? w)
                           (-create-new-shape w x y countdown-duration)))
           :writes #{:CurrentShapeComponent
                     :NextShapeComponent
                     :TargetLocationComponent}}))
