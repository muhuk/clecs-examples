(ns clecs-tetris.world.system.update-shape
  (:require [clecs-tetris.world.shape :refer [tiles]]
            [clecs.query :as query]
            [clecs.system :refer [system]]
            [clecs.world :as world]))


(defn -set-glass-tile [w x y tile]
  (let [f (fn [eid]
            (let [{cx :x cy :y} (world/component w eid :GlassTileComponent)]
              (and (= cx x) (= cy y))))
        eids (world/query w (query/all :GlassTileComponent))]
    (when-let [eid (first (filter f eids))]
      (world/set-component w eid :GlassTileComponent {:x x :y y :tile-type tile}))
    nil))


(defn -update-glass-tiles [w [old-x old-y] [x y] tiles]
  (let [h-1 (dec (count tiles))
        f (fn [neg-y row]
            (map-indexed (fn [x tile] [x (- h-1 neg-y) tile]) row))
        position-mapped (map-indexed f tiles)
        only-filled (filter #(= (nth % 2) "filled") (apply concat position-mapped))]
    (doseq [[tile-x tile-y tile] only-filled]
      (-set-glass-tile w (+ old-x tile-x) (+ old-y tile-y) "empty"))
    (doseq [[tile-x tile-y tile] only-filled]
      (-set-glass-tile w (+ x tile-x) (+ y tile-y) "moving"))
    nil))


(defn -update-shape-location [w dt countdown-duration]
  (let [q (query/all :CurrentShapeComponent
                     :TargetLocationComponent)]
    (when-let [[eid] (world/query w q)]
      (let [{target-x :x target-y :y value :countdown} (world/component w eid :TargetLocationComponent)]
        (if (pos? value)
          (world/set-component w
                               eid
                               :TargetLocationComponent
                               {:x target-x :y target-y :countdown (- value dt)})
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
            (-update-glass-tiles w [x y] [target-x target-y] (tiles shape-name
                                                                    shape-index))
            (world/set-component w
                                 eid
                                 :TargetLocationComponent
                                 {:x target-x :y target-y :countdown countdown-duration})))))))

(defn make-update-shape-system [countdown-duration]
  (system {:name :update-shape-system
           :process-fn (fn [w dt]
                         (-update-shape-location w dt countdown-duration))
           :writes #{:CurrentShapeComponent
                     :GlassTileComponent
                     :TargetLocationComponent}}))
