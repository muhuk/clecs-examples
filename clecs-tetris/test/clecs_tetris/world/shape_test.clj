(ns clecs-tetris.world.shape-test
  (:require [clecs-tetris.world.shape :refer :all]
            [midje.sweet :refer :all]))


(facts "rotate-shape cycles through orientations."
       (rotate-shape (first O)) => (first O)
       (rotate-shape (first I)) => (second I)
       (rotate-shape (second I)) => (first I)
       (rotate-shape (nth T 0)) => (nth T 1)
       (rotate-shape (nth T 1)) => (nth T 2)
       (rotate-shape (nth T 2)) => (nth T 3)
       (rotate-shape (nth T 3)) => (nth T 0))


(facts "Shapes have multiple orientations."
       (doseq [shape shapes]
         (seq shape) =not=> nil?))


(facts "Shapes have shape-name, index & 4x4 tile grids."
       (doseq [shape shapes
               orientation shape]
         (keys orientation) => (just #{:shape-name
                                       :tiles
                                       :idx})
         (:shape-name orientation) => symbol?
         (:idx orientation) => integer?
         (count (:tiles orientation)) => 4
         (map count (:tiles orientation)) => [4 4 4 4]))


(fact "shape-bag generates shapes."
      (take 100 shape-bag) => (partial every? (partial contains? (set (apply concat shapes)))))


(fact "shape-bag generates one of each shape before it repeats."
      (let [n (count shapes)
            shape-name-set (set (map (comp :shape-name first) shapes))]
        (take (* 5 n) shape-bag) => #(->> (map :shape-name %)
                                          (partition n)
                                          (map set)
                                          (every? (partial = shape-name-set)))))


(fact "shape-bag randomizes orientations."
      (take 20 shape-bag) => #(-> (map :idx %)
                                  (set)
                                  (count)
                                  (> 2)))
