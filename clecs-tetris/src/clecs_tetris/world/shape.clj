(ns clecs-tetris.world.shape)


(def ^:private legend {\. "empty"
                       \# "filled"})


(defn- make-shape [shape-name & tiles-strs]
  (let [tiles (map #(->> (map legend %)
                         (partition 4)
                         (map vec)
                         vec)
                   tiles-strs)]
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


(defn offset [coords x y]
  (map (fn [[a b c]] [(+ a x) (+ b y) c]) coords))


(defn rotate-shape [shape-name shape-index]
  (let [shape (get shapes shape-name)
        new-idx (mod (inc shape-index) (count shape))]
    (shape new-idx)))


(def shape-bag
  (let [shape-seq (list I J L O S T Z)]
    (map rand-nth (mapcat shuffle (repeat shape-seq)))))


(defn tiles [shape-name shape-index]
  (:tiles (get-in shapes [shape-name shape-index])))


(defn with-coordinates [tiles]
  (let [rows (count tiles)
        reverse-y #(- (dec rows) %)]
    (->> tiles
         (map-indexed (fn [y row]
                        (map-indexed (fn [x tile]
                                       [x (reverse-y y) tile])
                                     row)))
         (apply concat))))
