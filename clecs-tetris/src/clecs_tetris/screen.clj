(ns clecs-tetris.screen
  (:require [clecs-tetris.world.protocol :refer [IScreen]]
            [seesaw.core :refer :all]
            [seesaw.graphics :as g]))


(declare draw-tile
         make-side-panel
         make-tile-canvas
         paint-glass)


(deftype Screen [f tiles next-shape]
  IScreen
  (render [_ data]
          (doseq [field [:level :lines :score]
                  :let [selector (keyword (str "#" (name field)))]]
            (-> (select f [selector])
                (value! (field data)))
            (reset! tiles (:tiles data))
            (reset! next-shape (:next-shape data)))
          (repaint! f)))


(defn make-screen [tile-size]
  (let [f (frame :title "clecs-tetris")
        tiles (atom nil)
        next-shape (atom nil)
        glass (make-tile-canvas tiles 10 20 tile-size)
        panel (border-panel :center glass
                            :east (make-side-panel next-shape tile-size))]
    (native!)
    (config! f :content panel)
    (-> f pack! show!)
    (Screen. f tiles next-shape)))


(defn- draw-tile [g2d tile]
  (let [styles {:empty (g/style :background :lightgray)
                :filled (g/style :background :gray)
                :moving (g/style :background :red)}]
    (g/draw g2d
            (g/rect 0 0 1 1)
            (tile styles))))


(defn- make-side-panel [next-shape tile-size]
    (grid-panel :columns 2
                :items [(label "Score:") (label :id :score
                                                :text "0")
                        (label "Lines:") (label :id :lines
                                                :text "0")
                        (label "Level:") (label :id :level
                                                :text "0")
                        (make-tile-canvas next-shape 4 4 tile-size)]))


(defn- make-tile-canvas [tiles w h tile-size]
  (let [border (int (/ tile-size 4))
        width (+ border (* tile-size w) border)
        height (+ border (* tile-size h) border)]
    (canvas :paint (fn [_ g2d] (paint-glass g2d @tiles tile-size border))
            :size [width :by height]
            :border border)))


(defn- paint-glass [g2d tiles tile-size border]
  (g/translate g2d border border)
  (doseq [y (range (count tiles))
          x (range (count (first tiles)))]
    (g/push g2d
            (g/scale g2d tile-size)
            (g/translate g2d x y)
            (draw-tile g2d (-> tiles (get y) (get x))))))
