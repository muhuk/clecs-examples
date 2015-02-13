(ns clecs-example-rougelike.systems
  (:require [clecs-example-rougelike.systems.input :refer [input-system]]
            [clecs-example-rougelike.systems.move :refer [move-system]]
            [clecs-example-rougelike.systems.rendering :refer [rendering-system]]
            [clecs-example-rougelike.systems.take :refer [take-system]]
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
