(ns clecs-tetris.world.system.new-shape
  (:require [clecs-tetris.world.component :refer [->CurrentShapeComponent
                                                  ->TargetLocationComponent
                                                  CurrentShapeComponent
                                                  NextShapeComponent
                                                  TargetLocationComponent]]
            [clecs.query :as query]
            [clecs.world :as world]))


(defn -can-create-new-shape? [w]
  (and (empty? (world/query w (query/all CurrentShapeComponent TargetLocationComponent)))
       (not (empty? (world/query w (query/all NextShapeComponent))))))


(defn -create-new-shape [w x y countdown-duration]
  (let [[next-shape-eid] (world/query w (query/all NextShapeComponent))
        shape (:shape (world/component w next-shape-eid NextShapeComponent))
        current-shape-eid (world/add-entity w)]
    (world/set-component w (->CurrentShapeComponent current-shape-eid x y shape))
    (world/set-component w (->TargetLocationComponent current-shape-eid x y countdown-duration))
    (world/remove-entity w next-shape-eid)))


(defn make-new-shape-system [x y countdown-duration]
  (fn [w _]
    (world/transaction! w
                        (fn [w]
                          (when (-can-create-new-shape? w)
                            (-create-new-shape w x y countdown-duration))))))
