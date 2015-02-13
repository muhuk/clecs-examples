(ns clecs-example-rougelike.world
  (:require [clecs-example-rougelike.components :refer [components]]
            [clecs-example-rougelike.entities :refer [make-entity]]
            [clecs-example-rougelike.maps :refer [map-1]]
            [clecs.backend.atom-world :refer [atom-world]]
            [com.stuartsierra.component :refer [Lifecycle using]]))


(defrecord World [world components initial-transaction systems]
  Lifecycle
  (start [component]
         (if (nil? world)
           (do
             (println ";; Starting world.")
             (assoc component :world (atom-world components
                                                 initial-transaction
                                                 (:systems systems))))
           (do
             (println ";; World alread started.")
             component)))
  (stop [component]
        (if (nil? world)
          (do
            (println ";; Stopping world.")
            (assoc component :world nil))
          (do
            (println ";; World is not running yet.")
            component))))


(defn init-world [world]
  (doseq [[x y entities] map-1]
    (doseq [e entities]
      (make-entity world x y e))))


(defn make-world []
  (using (map->World {:world nil
                      :components components
                      :initial-transaction init-world})
         [:systems]))
