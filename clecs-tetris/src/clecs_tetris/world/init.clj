(ns clecs-tetris.world.init
  (:require [clecs.world :as world]))


(defn- create-game-stats [w]
  (let [eid (world/add-entity w)]
    (world/set-component w eid :LevelComponent {:level 1})
    (world/set-component w eid :LinesDroppedComponent {:lines 0})
    (world/set-component w eid :ScoreComponent {:score 0})))


(defn- create-glass-tiles [w glass-width glass-height default-tile]
  (doseq [y (range glass-height)
          x (range glass-width)]
    (let [eid (world/add-entity w)]
      (world/set-component w
                           eid
                           :GlassTileComponent
                           {:x x :y y :tile-type default-tile}))))


(defn make-initializer [& {:keys [default-tile glass-height glass-width]}]
  (fn [w]
    (doto w
      (create-game-stats)
      (create-glass-tiles glass-width glass-height default-tile))))
