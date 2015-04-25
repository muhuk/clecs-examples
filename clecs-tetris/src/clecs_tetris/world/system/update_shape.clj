(ns clecs-tetris.world.system.update-shape
  (:require [clecs-tetris.world.glass :refer [find-glass-tile]]
            [clecs-tetris.world.shape :refer [offset tiles with-coordinates]]
            [clecs.query :as query]
            [clecs.system :refer [system]]
            [clecs.world :as world]))


(declare -move-shape
         -set-glass-tile
         only-filled
         set-tiles)


(defn -set-glass-tile [w x y tile]
  (when-let [eid (find-glass-tile w x y)]
    (world/set-component w eid :GlassTileComponent {:x x :y y :tile-type tile}))
  nil)


(defn -set-tiles [w tiles x y tile]
  (doseq [[x y _] (-> tiles (only-filled) (offset x y))]
      (-set-glass-tile w x y tile)))


(defn -update-shape-location [w dt countdown-duration]
  (let [q (query/all :CurrentShapeComponent
                     :CollisionComponent
                     :ShapeTargetComponent)]
    (when-let [[eid] (world/query w q)]
      (let [{target-x :x
             target-y :y
             target-shape-index :shape-index
             cd :countdown
             :as target-shape-component} (world/component w eid :ShapeTargetComponent)]
        (if (pos? cd)
          ;; Countdown is positive => decrement it.
          (world/set-component w
                               eid
                               :ShapeTargetComponent
                               (assoc target-shape-component :countdown (- cd dt)))
          ;; Countdown is not positive => try to move the shape.
          (let [{collides? :collision?} (world/component w eid :CollisionComponent)
                {:keys [x
                        y
                        shape-name
                        shape-index]
                 :as current-shape} (world/component w eid :CurrentShapeComponent)]
            (if collides?
              (do
                (-set-tiles w (tiles shape-name shape-index) x y "filled")
                (world/remove-entity w eid))
              (do
                (world/set-component w
                                     eid
                                     :CurrentShapeComponent
                                     (assoc current-shape
                                       :x target-x
                                       :y target-y
                                       :shape-index target-shape-index))
                (-set-tiles w (tiles shape-name shape-index) x y "empty")
                (-set-tiles w (tiles shape-name target-shape-index) target-x target-y "moving")
                (world/set-component w
                                     eid
                                     :ShapeTargetComponent
                                     (assoc target-shape-component :countdown countdown-duration))
                (world/remove-component w eid :CollisionComponent)))))))))


(defn make-update-shape-system [countdown-duration]
  (system {:name :update-shape-system
           :process-fn (fn [w dt]
                         (-update-shape-location w dt countdown-duration))
           :writes #{:CurrentShapeComponent
                     :CollisionComponent
                     :GlassTileComponent
                     :ShapeTargetComponent}}))


(defn- only-filled [tiles]
  (filter #(= (nth % 2) "filled") (with-coordinates tiles)))
