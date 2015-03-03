(ns clecs-example-roguelike.maps)


(def legend {\# [:wall]
             \. [:floor]
             \< [:stairs-up]
             \> [:stairs-down]
             \@ [:floor :player]
             \t [:floor :sword]
             \b [:floor :potion]})


(def level-1 ["            "
              " ########## "
              " #.......># "
              " #........# "
              " #.....b..# "
              " #t.......# "
              " #...@....# "
              " #........# "
              " ########## "
              "            "])


(defn read-cell [x y cell legend]
  [x y (get legend cell)])


(defn read-row [y row legend]
  (->> row
       (map-indexed #(read-cell %1 y %2 legend))
       (remove #(nil? (% 2)))))


(defn read-map [m legend]
  (apply concat (map-indexed #(read-row %1 %2 legend) m)))


(def map-1 (read-map level-1 legend))
