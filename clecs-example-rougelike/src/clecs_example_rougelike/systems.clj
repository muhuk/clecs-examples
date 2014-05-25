(ns clecs-example-rougelike.systems
  (:require [clecs-example-rougelike.systems.input]
            [clecs-example-rougelike.systems.move]
            [clecs-example-rougelike.systems.rendering]
            [clecs-example-rougelike.systems.take]))


(def input-system clecs-example-rougelike.systems.input/input-system)


(def move-system clecs-example-rougelike.systems.move/move-system)


(def take-system clecs-example-rougelike.systems.take/take-system)


(def rendering-system clecs-example-rougelike.systems.rendering/rendering-system)
