(ns clecs-tetris.world.glass-test
  (:require [clecs-tetris.world.glass :refer :all]
            [clecs.query :as query]
            [clecs.test.mock :as mock]
            [midje.sweet :refer :all]))


(fact "find-glass-tile returns the tile with given coordinates."
      (let [w (mock/mock-editable-world)]
        (find-glass-tile w ..x.. ..y..) => ..n..
        (provided (query/all :GlassTileComponent) => ..q..
                  (mock/query w ..q..) => [..n-2.. ..n-1.. ..n.. ..n+1..]
                  (mock/component w
                                  ..n-2..
                                  :GlassTileComponent) => {:x ..x..
                                                           :y ..y-1..
                                                           :tile-type anything}
                  (mock/component w
                                  ..n-1..
                                  :GlassTileComponent) => {:x ..x+1..
                                                           :y ..y-1..
                                                           :tile-type anything}
                  (mock/component w
                                  ..n..
                                  :GlassTileComponent) => {:x ..x..
                                                           :y ..y..
                                                           :tile-type ..other-tile..}
                  (mock/component w
                                  ..n+1..
                                  :GlassTileComponent) => {:x ..x+1..
                                                           :y ..y..
                                                           :tile-type anything})))
