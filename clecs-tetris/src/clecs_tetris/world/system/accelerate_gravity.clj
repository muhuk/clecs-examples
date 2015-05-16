(ns clecs-tetris.world.system.accelerate-gravity
  (:require [clecs.query :as query]
            [clecs.system :refer [system]]
            [clecs.world :as world]))


(declare -reduce-countdown
         -set-acceleration-countdown)


(defn -accelerate [w dt default-countdown acceleration acceleration-duration]
  (let [eids (for [eid (world/query w (query/all :KeyboardInputEvent))
                   :let [{:keys [key-code]} (world/component w eid :KeyboardInputEvent)]
                   :when (= key-code "DOWN")]
               eid)]
    (if (empty? eids)
      (-reduce-countdown w dt default-countdown)
      (doseq [eid eids]
        (-set-acceleration-countdown w (/ default-countdown acceleration) acceleration-duration)
        (world/remove-entity w eid)))
    nil))


(defn -reduce-countdown [w dt default-countdown]
  (let [[eid] (world/query w (query/all :GravityComponent))]
    (if eid
      (let [{:keys [acceleration-countdown] :as c} (world/component w eid :GravityComponent)
            new-ct (- acceleration-countdown dt)
            new-c (if (pos? new-ct)
                    (assoc c :acceleration-countdown (- acceleration-countdown dt))
                    (assoc c
                      :acceleration-countdown 0
                      :acceleration default-countdown))]
        (world/set-component w eid :GravityComponent new-c))
      (world/set-component w
                           (world/add-entity w)
                           :GravityComponent
                           {:acceleration default-countdown
                            :acceleration-countdown 0
                            :countdown default-countdown})))
  nil)


(defn -set-acceleration-countdown [w acceleration acceleration-duration]
  (when-let [[eid] (world/query w (query/all :GravityComponent))]
    (world/set-component w eid :GravityComponent {:acceleration-countdown acceleration-duration
                                                  :acceleration acceleration
                                                  :countdown 0}))
  nil)


(defn make-accelerate-gravity-system [default-countdown acceleration acceleration-duration]
  (system {:name :accelerate-gravity-system
           :process-fn (fn [w dt] (-accelerate w
                                               dt
                                               default-countdown
                                               acceleration
                                               acceleration-duration))
           :reads #{:KeyboardInputEvent}
           :writes #{:GravityComponent}}))
