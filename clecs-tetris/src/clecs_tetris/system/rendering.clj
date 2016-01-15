(ns clecs-tetris.system.rendering
  (:require [clecs-tetris.world.protocol :as protocol]
            [clecs-tetris.world.shape :refer [tiles]]
            [clecs.query :as query]
            [clecs.system :refer [system]]
            [clecs.world :as world]))


(declare to-grid)


(defn -get-next-shape [_]
  ;; TODO: Get next-shape from the world.
  (let [result (to-grid 4 4 "empty" "filled" (tiles "Z" 0))]
    result))


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
                          (let [screen-data (-> (-get-stats w)
                                                (assoc :next-shape (-get-next-shape w)))]
                            (protocol/render screen screen-data)))
           :reads #{:LevelComponent
                    :LinesDroppedComponent
                    :ScoreComponent}}))


(defn to-grid [w h empty full coords]
  (let [empty-grid (->> (repeat w empty)
                        (vec)
                        (repeat h)
                        (vec))]
    (reduce (fn [grid [x y]]
              (assoc-in grid [y x] full))
            empty-grid
            coords)))
