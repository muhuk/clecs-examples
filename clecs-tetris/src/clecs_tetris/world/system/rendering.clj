(ns clecs-tetris.world.system.rendering
  (:require [clecs-tetris.world.component]
            [clecs-tetris.world.protocol :as protocol]
            [clecs.query :as query]
            [clecs.world :as world])
  (:import (clecs_tetris.world.component GlassTileComponent
                                         LevelComponent
                                         LinesDroppedComponent
                                         NextShapeComponent
                                         ScoreComponent)))


(defn -get-next-shape [w]
  (let [q (query/all NextShapeComponent)
        [eid] (world/query w q)
        {:keys [shape]} (world/component w eid NextShapeComponent)]
    (:tiles shape)))


(defn -get-stats [w]
  (let [q (query/all LevelComponent LinesDroppedComponent ScoreComponent)
        [eid] (world/query w q)
        level (:level (world/component w eid LevelComponent))
        lines (:lines (world/component w eid LinesDroppedComponent))
        score (:score (world/component w eid ScoreComponent))]
    {:level level
     :lines lines
     :score score}))


(defn -get-tiles [w width height]
  (let [q (query/all GlassTileComponent)
        tile-eids (world/query w q)
        tile-components (map #(world/component w % GlassTileComponent) tile-eids)
        empty-tiles (vec (repeat height (vec (repeat width :empty))))]
    (reduce (fn [tiles tile-component]
              (let [[x y tile-type] ((juxt :x :y :tile-type) tile-component)
                    y (- (dec height) y)]
                (assoc-in tiles [y x] tile-type)))
            empty-tiles
            tile-components)))


(defn make-rendering-system
  "Renders entities onto screen."
  [screen]
  (fn [w _]
    (let [tiles (-get-tiles w 10 20)
          {:keys [level lines score]} (-get-stats w)
          next-shape (-get-next-shape w)]
      (protocol/render screen {:score score
                               :lines lines
                               :level level
                               :tiles tiles
                               :next-shape next-shape}))))
