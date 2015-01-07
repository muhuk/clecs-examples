(ns clecs-example-rougelike.systems.rendering
  (:require [lanterna.screen :as s]
            [clecs.query :as query]
            [clecs.world :as w]
            [clecs-example-rougelike.components :refer :all]
            [clecs-example-rougelike.entities :refer [find-entities-at
                                                      tagged-entities]]))


(def min-height 20)


(def min-width 60)


(def side-panel-width 20)


(def sprites {:floor {:glyph "." :fg :magenta :bg :black :z-order 1}
              :player {:glyph "@" :fg :black :bg :white :z-order 999}
              :potion {:glyph "b" :fg :cyan :bg :black :z-order 100}
              :stairs-down {:glyph ">" :fg :green :bg :black :z-order 1}
              :sword {:glyph "t" :fg :cyan :bg :black :z-order 100}
              :wall {:glyph "#" :fg :white :bg :black :z-order 999}})


(declare render-entities)


(def ^:private q-inventory (query/all Inventory Name))


(def ^:private q-named (query/all Name Location))


(def ^:private q-renderables (query/all Renderable Location))


(def ^:private q-takeable (query/all Takeable Location))


(defn- clear-screen [screen]
  (let [[w h] (s/get-size screen)]
    (doseq [y (range h)
            x (range w)]
      (s/put-string screen x y " "))))


(defn- find-origin [world]
  (let [eid (:player @tagged-entities)]
    (let [{:keys [x y]} (w/component world eid Location)]
      [x y])))


(defn- make-put-glyph [screen]
  (let [[w h] (s/get-size screen)
        z (make-array Integer/TYPE h w)]
    (fn [z-order x y glyph fg bg]
      (when (and (<= 0 x (dec w))
                 (<= 0 y (dec h))
                 (>= z-order (-> z (aget y) (aget x))))
        (do
          (s/put-string screen x y glyph {:fg fg :bg bg})
          (-> z (aget y) (aset x z-order)))))))


(defn- origin-offset [world screen]
  (let [[w h] (s/get-size screen)
        [world-origin-x world-origin-y] (find-origin world)]
    [(- (/ (- w side-panel-width) 2) world-origin-x)
     (- (/ h 2) world-origin-y)]))


(defn- render-entities [world screen]
  (let [[dx dy] (origin-offset world screen)
        put-glyph (make-put-glyph screen)]
    (doseq [eid (w/query world q-renderables)]
      (let [{sprite :sprite} (w/component world eid Renderable)
            {world-x :x world-y :y} (w/component world eid Location)
            {:keys [fg bg glyph z-order]} (sprites sprite)]
        (put-glyph z-order (+ world-x dx) (+ world-y dy) glyph fg bg)))))


(defn- render-side-panel [world screen]
  (let [[w h] (s/get-size screen)
        colors {:fg :black :bg :yellow}
        player-eid (:player @tagged-entities)
        {:keys [x y]} (w/component world player-eid Location)
        offset-x (inc (- w side-panel-width))
        offset-y (atom 1)]
    ;; Clear side panel area.
    (doseq [y (range 0 h)
            x (range (dec offset-x) w)]
      (s/put-string screen x y " " colors))
    ;; Display player position.
    (s/put-string screen
                  offset-x
                  @offset-y
                  (str "x:" x " y:" y)
                  colors)
    (swap! offset-y (partial + 2))
    ;; Display item if any.
    (let [named-eids (w/query world q-named)
          eid (first (find-entities-at world named-eids x y))]
      (when-let [name-component (w/component world eid Name)]
        (s/put-string screen
                      offset-x
                      @offset-y
                      (str "<" (:name name-component) ">")
                      colors)
        (swap! offset-y (partial + 2))))
    ;; Display inventory.
    (s/put-string screen
                  offset-x
                  @offset-y
                  "Inventory:"
                  colors)
    (swap! offset-y inc)
    (if-let [eids (seq (w/query world q-inventory))]
      (doseq [eid eids]
        (s/put-string screen
                      offset-x
                      @offset-y
                      (str "  " (:name (w/component world eid Name)))
                      colors)
        (swap! offset-y inc))
      (do
        (s/put-string screen
                      offset-x
                      @offset-y
                      "  - empty -"
                      colors)
        (swap! offset-y inc)))
    (swap! offset-y inc)
    ;; Display available actions.
    (when-not (empty? (find-entities-at world (w/query world q-takeable) x y))
      (s/put-string screen
                      offset-x
                      @offset-y
                      "Press t to pick up."
                      colors)
      (swap! offset-y inc))
    (swap! offset-y inc)))


(defn rendering-system [screen]
  (fn [world dt]
    (when (>= dt 16)
      (println ";; Running rendering-system.")
      (clear-screen screen)
      (let [[w h] (s/get-size screen)]
        (if (and (>= w min-width) (>= h min-height))
          (do
            (render-entities world screen)
            (render-side-panel world screen))
          (s/put-string screen 1 1 (str "You must resize window to at least " min-width "x" min-height "."))))
      (s/redraw screen))))
