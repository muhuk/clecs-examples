(ns clecs-example-rougelike.components
  (:require [clecs.component :refer [component]]))


(def components [(component :Inventory nil)
                 (component :Location {:x Int :y Int})
                 (component :MoveIntent {:direction Str})
                 (component :Name {:name Str})
                 (component :Renderable {:sprite Str})
                 (component :Takeable nil)
                 (component :TakeIntent nil)
                 (component :Walkable nil)])
