(ns clecs-example-roguelike.app
  (:require [clecs-example-roguelike.screen :refer [make-screen-adapter]]
            [clecs-example-roguelike.systems :refer [make-systems]]
            [clecs-example-roguelike.world :refer [make-world]]
            [clecs.world :as w]
            [com.stuartsierra.component :as component])
  (:gen-class))


(declare update-world)


(defrecord Application [dt-fn escape-hatch running? world]
  component/Lifecycle
  (start [this]
         (if (not running?)
           (do
             (println ";; Starting application.")
             (assoc this :running? true))
           (do
             (println ";; Application already started.")
             this)))
  (stop [this]
        (if running?
          (do
            (println ";; Stopping application.")
            (assoc this :running? false))
          (do
            (println ";; Application is not running yet.")
            this)))
  clojure.lang.IFn
  (invoke [this]
          (let [dt (dt-fn)]
            (println ";; Main loop. (dt=" dt ")")
            (w/process! (:world world) dt)
            (when (< dt 17)
              (Thread/sleep 5)))))


(defn dt-fn [now-fn]
  (let [previous-timestamp (atom nil)]
    (fn []
      (let [previous-ts @previous-timestamp
            now (now-fn)
            dt (if (nil? previous-ts) 0 (- now previous-ts))]
        (reset! previous-timestamp now)
        dt))))


(defn make-application []
  (component/using (map->Application {:running false})
                   [:dt-fn :escape-hatch :world]))


(defn make-system
  ([] (make-system (promise)))
  ([escape-hatch] (component/system-map :application (make-application)
                                        :escape-hatch escape-hatch
                                        :dt-fn (dt-fn #(System/currentTimeMillis))
                                        :screen-adapter (make-screen-adapter)
                                        :systems (make-systems)
                                        :world (make-world))))
