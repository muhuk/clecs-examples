(ns clecs-example-roguelike.systems
  (:require [clecs-example-roguelike.systems.input :refer [input-system]]
            [clecs-example-roguelike.systems.move :refer [move-system]]
            [clecs-example-roguelike.systems.rendering :refer [rendering-system]]
            [clecs-example-roguelike.systems.take :refer [take-system]]
            [com.stuartsierra.component :refer [Lifecycle using]]))


(defrecord Systems [systems screen-adapter]
  Lifecycle
  (start [component]
         (if (nil? systems)
           (let [scr (:screen screen-adapter)
                 systems [(input-system scr)
                          (rendering-system scr)
                          move-system
                          take-system]]
             (println ";; Starting systems.")
             (assoc component :systems systems))
           (do
             (println ";; Systems is already running.")
             component)))
  (stop [component]
        (if (nil? systems)
          (let [systems nil]
            (println ";; Systems hasn't started.")
            component)
          (do
            (println ";; Stopping systems.")
            (assoc component :systems nil)))))


(defn make-systems []
  (using (map->Systems {:systems nil}) [:screen-adapter]))
