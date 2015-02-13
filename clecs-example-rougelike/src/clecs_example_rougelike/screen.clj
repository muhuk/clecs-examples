(ns clecs-example-rougelike.screen
  (:require [com.stuartsierra.component :as component]
            [lanterna.screen :as s]))


(defrecord ScreenAdapter [screen running? size]
  component/Lifecycle
  (start [component]
         (if (not running?)
           (do
             (println ";; Starting screen.")
             (s/start screen)
             (assoc component :running? true))
           (do
             (println ";; Screen already started.")
             component)))
  (stop [component]
        (if running?
          (do
            (println ";; Stopping screen")
            (s/stop screen)
            (assoc component :running? false))
          (do
            (println ";; Screen is not running yet.")
            component))))


(defn make-screen-adapter []
  (let [size (atom [0 0])
        resize-listener (fn [cols rows] (reset! size [cols rows]))
        screen (s/get-screen :swing {:cols 80
                                     :rows 24
                                     :font-size 18
                                     :resize-listener resize-listener})]
    (apply resize-listener (s/get-size screen))
    (->ScreenAdapter screen false size)))
