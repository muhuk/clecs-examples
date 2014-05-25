(ns clecs-example-rougelike.components
  (:require [clecs.component :refer :all]))


(defcomponent Inventory [eid])


(defcomponent Location [eid x y])


(defcomponent MoveIntent [eid direction])


(defcomponent Name [eid name])


(defcomponent Renderable [eid sprite])


(defcomponent Takeable [eid])


(defcomponent TakeIntent [eid])


(defcomponent Walkable [eid])
