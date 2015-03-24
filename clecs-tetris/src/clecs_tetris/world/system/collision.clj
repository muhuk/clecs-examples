(ns clecs-tetris.world.system.collision
  (:require [clecs-tetris.world.glass :refer [find-glass-tile]]
            [clecs-tetris.world.shape :refer [tiles with-coordinates]]
            [clecs.query :as query]
            [clecs.system :refer [system]]
            [clecs.world :as world]))


(defn -collides? [w eid]
  (let [local-coords (->> (world/component w eid :CurrentShapeComponent)
                          ((juxt :shape-name :shape-index))
                          (apply tiles)
                          (with-coordinates)
                          (filter #(= (get % 2) "filled"))
                          (map (juxt first second)))
        {target-x :x
         target-y :y} (world/component w
                                       eid
                                       :TargetLocationComponent)
        coords (map (fn [[x y] [tx ty]] [(+ x tx) (+ y ty)])
                    local-coords
                    (repeat [target-x target-y]))]
    (true? (some (fn [[x y]]
                   (or (neg? y)
                       (let [{:keys [tile-type]} (world/component w
                                                                  (find-glass-tile w x y)
                                                                  :GlassTileComponent)]
                         (= tile-type "filled"))))
                 coords))))


(defn -collision-entities [w]
  (->> (query/all :CurrentShapeComponent
                  :TargetLocationComponent)
       (world/query w)
       (filter (fn [eid]
                 (nil? (world/component w eid :CollisionComponent))))))


(def collision-system
  (system {:name :collision-system
           :process-fn (fn [w dt]
                         (let [eids (world/query w (query/all :CurrentShapeComponent))]
                           (doseq [eid eids]
                             (world/set-component w
                                                  eid
                                                  :CollisionComponent
                                                  {:collision? (-collides? w eid)}))))
           :reads #{:CurrentShapeComponent
                    :GlassTileComponent
                    :TargetLocationComponent}
           :writes #{:CollisionComponent}}))
