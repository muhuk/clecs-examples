(ns clecs-tetris.world.system.move-shape
  (:require [clecs-tetris.world.shape :refer [rotate-shape]]
            [clecs.query :as query]
            [clecs.system :refer [system]]
            [clecs.world :as world]))


(declare -rotate
         -update-current-shape)



(defn -move-left [_ {:keys [x] :as c}]
  (assoc c :x (dec x)))


(defn -move-right [_ {:keys [x] :as c}]
  (assoc c :x (inc x)))


(defn -process-events [w]
  ;; This let should be outside of the definition
  ;; but midje complains about -rotate then.
  (let [input-mapping {"LEFT" -move-left
                       "RIGHT" -move-right
                       "UP" -rotate}
        handled-inputs (set (keys input-mapping))]
    (doseq [eid (world/query w (query/all :KeyboardInputEvent))
            :let [{:keys [id key-code]} (world/component w eid :KeyboardInputEvent)]]
      (when (contains? handled-inputs key-code)
        (-update-current-shape w (get input-mapping key-code))
        (world/remove-entity w eid)))))


(defn -rotate [{:keys [shape-name]} {:keys [shape-index] :as c}]
  (let [new-shape-index (:shape-index (rotate-shape shape-name shape-index))]
    (assoc c :shape-index new-shape-index)))


(defn -update-current-shape [w f]
  (let [[eid] (world/query w (query/all :CurrentShapeComponent))
        current-shape-component (world/component w eid :CurrentShapeComponent)
        shape-target-component (or (world/component w eid :ShapeTargetComponent)
                                   {:x (:x current-shape-component)
                                    :y (:y current-shape-component)
                                    :shape-index (:shape-index current-shape-component)
                                    :countdown 0})]
    (world/set-component w eid :ShapeTargetComponent (f current-shape-component
                                                        shape-target-component))
    nil))


(def move-shape-system
  (system {:name :move-shape-system
           :process-fn (fn [w _] (-process-events w))
           :reads #{:CurrentShapeComponent
                    :KeyboardInputEvent}
           :writes #{:ShapeTargetComponent}}))
