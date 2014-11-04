(ns clecs-tetris.world.component
  (:require [clecs.component :refer [defcomponent]]))


(defcomponent CurrentShapeComponent [eid x y shape])


(defcomponent GlassTileComponent [eid x y tile-type])


(defcomponent LevelComponent [eid level])


(defcomponent LinesDroppedComponent [eid lines])


(defcomponent NextShapeComponent [eid shape])


(defcomponent ScoreComponent [eid score])


(defcomponent TargetLocationComponent [eid x y countdown])
