(ns clecs-tetris.world.system.update-shape
  (:require [clecs-tetris.world.component :refer [->CurrentShapeComponent
                                                  ->GlassTileComponent
                                                  ->TargetLocationComponent
                                                  CurrentShapeComponent
                                                  GlassTileComponent
                                                  TargetLocationComponent]]
            [clecs.query :as query]
            [clecs.world :as world]))


(defn -set-glass-tile [w x y tile]
  (let [f (fn [eid]
            (let [{cx :x cy :y} (world/component w eid  GlassTileComponent)]
              (and (= cx x) (= cy y))))
        eids (world/query w (query/all GlassTileComponent))]
    (when-let [eid (first (filter f eids))]
      (world/set-component w (->GlassTileComponent eid x y tile)))
    nil))


(defn -update-glass-tiles [w [old-x old-y] [x y] tiles]
  (let [h-1 (dec (count tiles))
        f (fn [neg-y row]
            (map-indexed (fn [x tile] [x (- h-1 neg-y) tile]) row))
        position-mapped (map-indexed f tiles)
        only-filled (filter #(= (nth % 2) :filled) (apply concat position-mapped))]
    (doseq [[tile-x tile-y tile] only-filled]
      (-set-glass-tile w (+ old-x tile-x) (+ old-y tile-y) :empty))
    (doseq [[tile-x tile-y tile] only-filled]
      (-set-glass-tile w (+ x tile-x) (+ y tile-y) :moving))
    nil))


(defn -update-shape-location [w dt countdown-duration]
  (let [q (query/all CurrentShapeComponent
                     TargetLocationComponent)]
    (when-let [[eid] (world/query w q)]
      (let [{target-x :x target-y :y value :countdown} (world/component w eid TargetLocationComponent)]
        (if (pos? value)
          (world/set-component w (->TargetLocationComponent eid target-x target-y (- value dt)))
          (let [{:keys [x y shape]} (world/component w eid CurrentShapeComponent)]
            (world/set-component w (->CurrentShapeComponent eid target-x target-y shape))
            (-update-glass-tiles w [x y] [target-x target-y] (:tiles shape))
            (world/set-component w (->TargetLocationComponent eid target-x target-y countdown-duration))))))))

(defn make-update-shape-system [countdown-duration]
  (fn [w dt]
    (world/transaction! w #(-update-shape-location % dt countdown-duration))))
