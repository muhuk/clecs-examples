(ns clecs-tetris.core
  (:require [clecs-tetris.screen :refer [make-screen]]
            [clecs-tetris.world :refer [make-world]])
  (:gen-class))


(declare timestamp)


(defn init []
  (let [screen (make-screen 30)]
    {:world (make-world :default-tile :empty
                        :glass-height 20
                        :glass-width 10
                        :gravity-countdown 1000
                        :screen screen
                        :update-shape-countdown-duration 50)
     :escape-hatch (promise)}))


(defn make-timer []
  (let [then (atom (timestamp))
        dt (atom nil)]
    (fn []
      (let [now (timestamp)]
        (swap! then (fn [v]
                      (reset! dt (- now v))
                      now)))
      @dt)))


(defn run [{:keys [escape-hatch world]}]
  (let [timer (make-timer)]
    (while (not (realized? escape-hatch))
      (let [dt (timer)]
        (world dt)
        (when (< dt 16)
          (Thread/sleep 10))))))


(defn timestamp [] (System/currentTimeMillis))


(defn -main
  [& args]
  (-> (init)
      (run)))
