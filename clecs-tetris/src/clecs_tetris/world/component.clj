(ns clecs-tetris.world.component
  (:require [clecs.component :refer [component]]))




(def components
  [(component :KeyboardInputEvent {:id Integer
                                   :key-code String})
   (component :LevelComponent {:level Integer})
   (component :LinesDroppedComponent {:lines Integer})
   (component :ScoreComponent {:score Integer})])

