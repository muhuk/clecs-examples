(ns clecs-tetris.world
  (:require [clecs-tetris.world.component :refer [components]]
            [clecs-tetris.world.init :refer [make-initializer]]
            [clecs-tetris.world.shape :refer [shape-bag]]
            [clecs-tetris.world.system.collision :refer [collision-system]]
            [clecs-tetris.world.system.gravity :refer [make-gravity-system]]
            [clecs-tetris.world.system.input :refer [make-input-system]]
            [clecs-tetris.world.system.move-shape :refer [move-shape-system]]
            [clecs-tetris.world.system.new-shape :refer [make-new-shape-system]]
            [clecs-tetris.world.system.next-shape :refer [make-next-shape-system]]
            [clecs-tetris.world.system.rendering :refer [make-rendering-system]]
            [clecs-tetris.world.system.update-shape :refer [make-update-shape-system]]
            [clecs.backend.atom-world :refer [atom-world-factory]]
            [clecs.world :as world]))


(defn make-world [& {:keys [default-tile
                            event-queue
                            glass-height
                            glass-width
                            gravity-countdown
                            screen
                            update-shape-countdown-duration]}]
  (let [initializer (make-initializer :default-tile default-tile
                                      :glass-height glass-height
                                      :glass-width glass-width)
        systems [collision-system
                 (make-gravity-system gravity-countdown)
                 (make-input-system event-queue)
                 move-shape-system
                 (make-new-shape-system (/ (- glass-width 4) 2)
                                                glass-height
                                                update-shape-countdown-duration)
                 (make-next-shape-system shape-bag)
                 (make-rendering-system screen)
                 (make-update-shape-system update-shape-countdown-duration)]
        w (world/world atom-world-factory
                       {:components components
                        :initializer initializer
                        :systems systems})]
    (fn [dt]
      (world/process! w dt))))
