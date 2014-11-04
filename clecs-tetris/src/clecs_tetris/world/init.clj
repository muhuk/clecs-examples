(ns clecs-tetris.world.init
  (:require [clecs-tetris.world.component :refer [->GlassTileComponent
                                                  ->LevelComponent
                                                  ->LinesDroppedComponent
                                                  ->ScoreComponent]]
            [clecs.world :as world]))


(defn- create-game-stats [w]
  (let [eid (world/add-entity w)]
    (world/set-component w (->LevelComponent eid 1))
    (world/set-component w (->LinesDroppedComponent eid 0))
    (world/set-component w (->ScoreComponent eid 0))))


(defn- create-glass-tiles [w glass-width glass-height default-tile]
  (doseq [y (range glass-height)
          x (range glass-width)]
    (let [eid (world/add-entity w)]
      (world/set-component w
                           (->GlassTileComponent eid x y default-tile)))))


(defn make-initializer [& {:keys [default-tile glass-height glass-width]}]
  (fn [w]
    (doto w
      (create-game-stats)
      (create-glass-tiles glass-width glass-height default-tile))))
