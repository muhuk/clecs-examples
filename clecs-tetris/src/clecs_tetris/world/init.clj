(ns clecs-tetris.world.init
  (:require [clecs.world :as world]))


(defn- create-game-stats [w]
  (let [eid (world/add-entity w)]
    (world/set-component w eid :LevelComponent {:level 1})
    (world/set-component w eid :LinesDroppedComponent {:lines 0})
    (world/set-component w eid :ScoreComponent {:score 0})))



(defn make-initializer [& {:keys [default-tile glass-height glass-width]}]
  (fn [w]
    (doto w
      (create-game-stats))))
