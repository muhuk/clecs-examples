(ns user
  (:require [com.stuartsierra.component :as component]
            [clojure.tools.namespace.repl :refer [refresh]]
            [clecs-example-roguelike.app :refer [make-system]]))


(def system nil)


(defn init []
  (alter-var-root #'system (constantly (make-system))))


(defn start []
  (alter-var-root #'system component/start))


(defn stop []
  (alter-var-root #'system
    (fn [s] (when s (component/stop s)))))


(defn go []
  (init)
  (start))


(defn reset []
  (stop)
  (refresh :after 'user/go))
