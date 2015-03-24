(ns clecs-tetris.world.glass
  (:require [clecs.query :as query]
            [clecs.world :as world]))


(defn find-glass-tile [w x y]
  (->> (world/query w (query/all :GlassTileComponent))
       (filter (fn [eid]
                 (let [{cx :x cy :y} (world/component w eid :GlassTileComponent)]
                   (and (= cx x) (= cy y)))))
       (first)))
