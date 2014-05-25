(ns clecs-example-rougelike.app
  (:require [com.stuartsierra.component :as component]
            [clecs.backend.atom-world :refer [make-world]]
            [clecs.world :as w]
            [clecs-example-rougelike.screen :refer [make-screen-adapter]]
            [clecs-example-rougelike.systems :refer :all]
            [clecs-example-rougelike.world :refer [init-world]])
  (:gen-class))


(declare update-world)


(defrecord Application [dt-fn escape-hatch running? screen-adapter world]
  component/Lifecycle
  (start [this]
         (if (not running?)
           (do
             (println ";; Starting application.")
             (let [scr (:screen screen-adapter)]
               (doto world
                 (w/set-system! :input-system (input-system scr))
                 (w/set-system! :rendering-system (rendering-system scr))
                 (w/set-system! :move-system move-system)
                 (w/set-system! :take-system take-system)))
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
          (let [dt (dt-fn)
                screen (:screen screen-adapter)]
            (println ";; Main loop. (dt=" dt ")")
            (w/process! world dt)
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
                   [:dt-fn
                    :escape-hatch
                    :screen-adapter
                    :world]))


(defn make-system
  ([] (make-system (promise)))
  ([escape-hatch] (component/system-map :application (make-application)
                                        :escape-hatch escape-hatch
                                        :dt-fn (dt-fn #(System/currentTimeMillis))
                                        :screen-adapter (make-screen-adapter)
                                        :world (make-world init-world))))
