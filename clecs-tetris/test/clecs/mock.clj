(ns clecs.mock
  (:require [clecs.world :as world]))


(defmacro ^:private def-mock-fn [n]
  `(defn ~n [~'& ~'_]
     (throw (RuntimeException. ~(str n " is called directly.")))))


(def-mock-fn mock-add-entity)
(def-mock-fn mock-component)
(def-mock-fn mock-query)
(def-mock-fn mock-remove-component)
(def-mock-fn mock-remove-entity)
(def-mock-fn mock-set-component)
(def-mock-fn mock-transaction!)


(def mock-world (reify
                  world/IEditableWorld
                  (add-entity [_] (mock-add-entity))
                  (remove-component [_ eid ctype] (mock-remove-component eid ctype))
                  (remove-entity [_ eid] (mock-remove-entity eid))
                  (set-component [_ c] (mock-set-component c))
                  world/IQueryableWorld
                  (component [_ eid ctype] (mock-component eid ctype))
                  (query [_ q] (mock-query q))
                  world/ITransactableWorld
                  (transaction! [_ f] (mock-transaction! f))))
