(ns clecs-tetris.world.system.rendering
  (:require [clecs-tetris.world.protocol :as protocol]
            [clecs-tetris.world.shape :refer [tiles]]
            [clecs.query :as query]
            [clecs.system :refer [system]]
            [clecs.world :as world]))


(defn -get-stats [w]
  (let [q (query/all :LevelComponent
                     :LinesDroppedComponent
                     :ScoreComponent)
        [eid] (world/query w q)
        level (:level (world/component w eid :LevelComponent))
        lines (:lines (world/component w eid :LinesDroppedComponent))
        score (:score (world/component w eid :ScoreComponent))]
    {:level level
     :lines lines
     :score score}))


(defn make-rendering-system
  "Renders entities onto screen."
  [screen]
  (system {:name :rendering-system
           :process-fn  (fn [w _]
                          (protocol/render screen (-get-stats w)))
           :reads #{:LevelComponent
                    :LinesDroppedComponent
                    :ScoreComponent}}))
