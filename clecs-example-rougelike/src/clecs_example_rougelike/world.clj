(ns clecs-example-rougelike.world
  (:require [clecs.world :as w]
            [clecs-example-rougelike.entities :refer [make-entity]]
            [clecs-example-rougelike.maps :refer [map-1]]))


(defn init-world [world]
  (doseq [[x y entities] map-1]
    (doseq [e entities]
      (make-entity world x y e))))
