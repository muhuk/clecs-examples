(ns clecs-example-roguelike.core
  (:require [clecs-example-roguelike.app :refer [make-system]]
            [com.stuartsierra.component :as component])
  (:gen-class))


(defn run []
  (let [escape-hatch (promise)
        system (component/start (make-system escape-hatch))]
    (let [app (:application system)]
      (while (not (realized? escape-hatch))
        (let [start (System/currentTimeMillis)]
          (app)
          (let [duration (- (System/currentTimeMillis) start)]
            (when (< duration 16)
              (Thread/sleep (- 16 duration)))))))
    (component/stop system))
  nil)



(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (alter-var-root #'*read-eval* (constantly false))
  (run))
