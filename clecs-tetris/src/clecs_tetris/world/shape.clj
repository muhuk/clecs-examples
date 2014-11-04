(ns clecs-tetris.world.shape)


(def ^:private legend {\. :empty
                       \# :filled})


(defn- make-shape [shape-name & tiles-strs]
  (let [tiles (map #(->> (map legend %)
                         (partition 4)
                         (map vec)
                         vec)
                   tiles-strs)]
    (->> tiles
         (map-indexed (fn [idx tiles] {:shape-name shape-name
                                       :tiles tiles
                                       :idx idx}))
         doall
         vec)))


(def I (make-shape 'I
                   (str "...."
                        "...."
                        "####"
                        "....")
                   (str ".#.."
                        ".#.."
                        ".#.."
                        ".#..")))


(def J (make-shape 'J
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


(def L (make-shape 'L
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


(def O (make-shape 'O
                   (str "...."
                        ".##."
                        ".##."
                        "....")))


(def S (make-shape 'S
                   (str "...."
                        ".##."
                        "##.."
                        "....")
                   (str "...."
                        ".#.."
                        ".##."
                        "..#.")))


(def T (make-shape 'T
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


(def Z (make-shape 'Z
                   (str "...."
                        "##.."
                        ".##."
                        "....")
                   (str "...."
                        "..#."
                        ".##."
                        ".#..")))


(def shapes [I J L O S T Z])


(def shape-bag
  (map rand-nth (mapcat shuffle (repeat shapes))))


(let [this-ns *ns*]
  (defn rotate-shape [shape-instance]
    (let [shape @((:shape-name shape-instance) (ns-publics this-ns))
          new-idx (mod (inc (:idx shape-instance)) (count shape))]
      (shape new-idx))))
