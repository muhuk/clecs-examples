(ns clecs-tetris.world
  (:require [clecs-tetris.world.init :refer [make-initializer]]
            [clecs-tetris.world.shape :refer [shape-bag]]
            [clecs-tetris.world.system.gravity :refer [make-gravity-system]]
            [clecs-tetris.world.system.new-shape :refer [make-new-shape-system]]
            [clecs-tetris.world.system.next-shape :refer [make-next-shape-system]]
            [clecs-tetris.world.system.rendering :refer [make-rendering-system]]
            [clecs-tetris.world.system.update-shape :refer [make-update-shape-system]]
            [clecs.core :refer [make-world] :rename {make-world clecs-make-world}]
            [clecs.world :as world]))


(defn make-world [& {:keys [default-tile
                            glass-height
                            glass-width
                            gravity-countdown
                            screen
                            update-shape-countdown-duration]}]
  (let [initializer (make-initializer :default-tile default-tile
                                      :glass-height glass-height
                                      :glass-width glass-width)
        next-shape-system (make-next-shape-system shape-bag)
        new-shape-system (make-new-shape-system (/ (- glass-width 4) 2)
                                                glass-height
                                                update-shape-countdown-duration)
        rendering-system (make-rendering-system screen)
        update-shape-system (make-update-shape-system update-shape-countdown-duration)
        gravity-system (make-gravity-system gravity-countdown)
        w (doto (clecs-make-world initializer)
            (world/set-system! :gravity-system gravity-system)
            (world/set-system! :next-shape-system next-shape-system)
            (world/set-system! :new-shape-system new-shape-system)
            (world/set-system! :rendering-system rendering-system)
            (world/set-system! :update-shape-system update-shape-system))]
    (fn [dt]
      (world/process! w dt))))
