(ns clecs-tetris.world.system.update-shape
  (:require [clecs-tetris.world.glass :refer [find-glass-tile]]
            [clecs-tetris.world.shape :refer [offset tiles with-coordinates]]
            [clecs.query :as query]
            [clecs.system :refer [system]]
            [clecs.world :as world]))


(declare -move-shape
         -set-glass-tile
         only-filled)


(defn- only-filled [tiles]
  (filter #(= (nth % 2) "filled") (with-coordinates tiles)))

(defn -freeze-shape [w x y tiles]
  (doseq [[x y _] (-> tiles (only-filled) (offset x y))]
      (-set-glass-tile w x y "filled")))


(defn -move-shape [w [old-x old-y] [x y] tiles]
  (let [filled-tiles (only-filled tiles)]
    (doseq [[x y _] (offset filled-tiles old-x old-y)]
      (-set-glass-tile w x y "empty"))
    (doseq [[x y _] (offset filled-tiles x y)]
      (-set-glass-tile w x y "moving"))
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
          (let [{collides? :collision?} (world/component w eid :CollisionComponent)
                {:keys [x
                        y
                        shape-name
                        shape-index]
                 :as current-shape} (world/component w eid :CurrentShapeComponent)]
            (if collides?
              (do
                (-freeze-shape w x y (tiles shape-name shape-index))
                (world/remove-entity w eid))
              (do
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
                (world/remove-component w eid :CollisionComponent)))))))))


(defn make-update-shape-system [countdown-duration]
  (system {:name :update-shape-system
           :process-fn (fn [w dt]
                         (-update-shape-location w dt countdown-duration))
           :writes #{:CurrentShapeComponent
                     :CollisionComponent
                     :GlassTileComponent
                     :TargetLocationComponent}}))
