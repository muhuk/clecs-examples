(ns clecs-tetris.world.shape)


(defn- make-shape [shape-name & tiles-strs]
  (let [coords-if-filled (fn
                           [idx c]
                           (when (= c \#)
                             [(mod idx 4)
                              (quot idx 4)]))
        tiles (->> tiles-strs
                   (map (partial keep-indexed coords-if-filled))
                   (map vec)
                   (vec))]
    (->> tiles
         (map-indexed (fn [idx tiles] {:shape-name shape-name
                                       :shape-index idx
                                       :tiles tiles}))
         doall
         vec)))


(def I (make-shape "I"
                   (str "...."
                        "...."
                        "####"
                        "....")
                   (str ".#.."
                        ".#.."
                        ".#.."
                        ".#..")))


(def J (make-shape "J"
                   (str "...."
                        "###."
                        "..#."
                        "....")
                   (str "..#."
                        "..#."
                        ".##."
                        "....")
                   (str "...."
                        "#..."
                        "###."
                        "....")
                   (str "##.."
                        "#..."
                        "#..."
                        "....")))


(def L (make-shape "L"
                   (str "...."
                        "###."
                        "#..."
                        "....")
                   (str ".##."
                        "..#."
                        "..#."
                        "....")
                   (str "...."
                        "..#."
                        "###."
                        "....")
                   (str "#..."
                        "#..."
                        "##.."
                        "....")))


(def O (make-shape "O"
                   (str "...."
                        ".##."
                        ".##."
                        "....")))


(def S (make-shape "S"
                   (str "...."
                        ".##."
                        "##.."
                        "....")
                   (str "...."
                        ".#.."
                        ".##."
                        "..#.")))


(def T (make-shape "T"
                   (str "...."
                        "###."
                        ".#.."
                        "....")
                   (str ".#.."
                        "##.."
                        ".#.."
                        "....")
                   (str ".#.."
                        "###."
                        "...."
                        "....")
                   (str ".#.."
                        ".##."
                        ".#.."
                        "....")))


(def Z (make-shape "Z"
                   (str "...."
                        "##.."
                        ".##."
                        "....")
                   (str "...."
                        "..#."
                        ".##."
                        ".#..")))


(def shapes {"I" I
             "J" J
             "L" L
             "O" O
             "S" S
             "T" T
             "Z" Z})


(defn rotate [shape-name shape-index]
  (let [shape (get shapes shape-name)
        new-idx (mod (inc shape-index) (count shape))]
    (shape new-idx)))


(def shape-bag
  (let [shape-seq (list I J L O S T Z)]
    (map rand-nth (mapcat shuffle (repeat shape-seq)))))


(defn tiles [shape-name shape-index]
  (get-in shapes [shape-name shape-index :tiles]))
