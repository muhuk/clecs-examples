(ns clecs-example-rougelike.components
  (:require [clecs.component :refer [component]]))


(def components [(component :Inventory nil)
                 (component :Location {:x Integer :y Integer})
                 (component :MoveIntent {:direction String})
                 (component :Name {:name String})
                 (component :Renderable {:sprite String})
                 (component :Takeable nil)
                 (component :TakeIntent nil)
                 (component :Walkable nil)])
