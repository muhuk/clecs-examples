(ns clecs-tetris.world.system.update-shape
  (:require [clecs-tetris.world.glass :refer [find-glass-tile]]
            [clecs-tetris.world.shape :refer [tiles with-coordinates]]
            [clecs.query :as query]
            [clecs.system :refer [system]]
            [clecs.world :as world]))


(declare -move-shape
         -set-glass-tile)


(defn -move-shape [w [old-x old-y] [x y] tiles]
  (let [only-filled (filter #(= (nth % 2) "filled") (with-coordinates tiles))]
    (doseq [[tile-x tile-y tile] only-filled]
      (-set-glass-tile w (+ old-x tile-x) (+ old-y tile-y) "empty"))
    (doseq [[tile-x tile-y tile] only-filled]
      (-set-glass-tile w (+ x tile-x) (+ y tile-y) "moving"))
    nil))


(defn -set-glass-tile [w x y tile]
  (when-let [eid (find-glass-tile w x y)]
    (world/set-component w eid :GlassTileComponent {:x x :y y :tile-type tile}))
  nil)


(defn -update-shape-location [w dt countdown-duration]
  (let [q (query/all :CurrentShapeComponent
                     :CollisionComponent
                     :TargetLocationComponent)]
    (when-let [[eid] (world/query w q)]
      (let [{target-x :x
             target-y :y
             cd :countdown} (world/component w eid :TargetLocationComponent)]
        (if (pos? cd)
          ;; Countdown is positive => decrement it.
          (world/set-component w
                               eid
                               :TargetLocationComponent
                               {:x target-x :y target-y :countdown (- cd dt)})
          ;; Countdown is not positive => try to move the shape.
          (let [{:keys [x
                        y
                        shape-name
                        shape-index]
                 :as current-shape} (world/component w eid :CurrentShapeComponent)]
            (world/set-component w
                                 eid
                                 :CurrentShapeComponent
                                 (assoc current-shape
                                   :x target-x
                                   :y target-y))
            (-move-shape w
                         [x y]
                         [target-x target-y]
                         (tiles shape-name shape-index))
            (world/set-component w
                                 eid
                                 :TargetLocationComponent
                                 {:x target-x :y target-y :countdown countdown-duration})
            (world/remove-component w eid :CollisionComponent)))))))


(defn make-update-shape-system [countdown-duration]
  (system {:name :update-shape-system
           :process-fn (fn [w dt]
                         (-update-shape-location w dt countdown-duration))
           :writes #{:CurrentShapeComponent
                     :CollisionComponent
                     :GlassTileComponent
                     :TargetLocationComponent}}))
