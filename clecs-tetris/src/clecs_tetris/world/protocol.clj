(ns clecs-tetris.world.protocol)


(defprotocol IScreen
  (render [this data]))
