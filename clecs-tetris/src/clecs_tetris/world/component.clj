(ns clecs-tetris.world.component
  (:require [clecs.component :refer [component]]))




(def components [(component :CurrentShapeComponent {:x Integer
                                                    :y Integer
                                                    :shape-name String
                                                    :shape-index Integer})
                 (component :CollisionComponent {:collision? Boolean})
                 (component :GlassTileComponent {:x Integer
                                                 :y Integer
                                                 :tile-type String})
                 (component :LevelComponent {:level Integer})
                 (component :LinesDroppedComponent {:lines Integer})
                 (component :NextShapeComponent {:shape-name String
                                                 :shape-index Integer})
                 (component :ScoreComponent {:score Integer})
                 (component :TargetLocationComponent {:x Integer
                                                      :y Integer
                                                      :countdown Integer})])
