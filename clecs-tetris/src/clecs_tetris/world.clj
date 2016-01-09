(ns clecs-tetris.world
  (:require [clecs-tetris.system.input :refer [make-input-system]]
            [clecs-tetris.system.quit :refer [make-quit-system]]
            [clecs-tetris.system.rendering :refer [make-rendering-system]]
            [clecs-tetris.world.init :refer [make-initializer]]
            [clecs-tetris.world.shape :refer [shape-bag]]
            [clecs.backend.atom-world :refer [atom-world-factory]]
            [clecs.component :refer [component]]
            [clecs.world :as world]))


(def components
  [(component :KeyboardInputEvent {:id Integer
                                   :key-code String})
   (component :LevelComponent {:level Integer})
   (component :LinesDroppedComponent {:lines Integer})
   (component :ScoreComponent {:score Integer})])


(defn make-world [& {:keys [default-tile
                            escape-hatch
                            event-queue
                            glass-height
                            glass-width
                            gravity-countdown
                            screen
                            update-shape-countdown-duration]}]
  (let [initializer (make-initializer :default-tile default-tile
                                      :glass-height glass-height
                                      :glass-width glass-width)
        systems [(make-input-system event-queue)
                 (make-quit-system escape-hatch)
                 (make-rendering-system screen)]
        w (world/world atom-world-factory
                       {:components components
                        :initializer initializer
                        :systems systems})]
    (fn [dt]
      (world/process! w dt))))
