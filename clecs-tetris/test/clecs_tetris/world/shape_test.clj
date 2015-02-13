(ns clecs-tetris.world.shape-test
  (:require [clecs-tetris.world.shape :refer :all]
            [midje.sweet :refer :all]))


(facts "rotate-shape cycles through orientations."
       (rotate-shape "O" 0) => (first O)
       (rotate-shape "I" 0) => (second I)
       (rotate-shape "I" 1) => (first I)
       (rotate-shape "T" 0) => (nth T 1)
       (rotate-shape "T" 1) => (nth T 2)
       (rotate-shape "T" 2) => (nth T 3)
       (rotate-shape "T" 3) => (nth T 0))


(facts "Shapes have multiple orientations."
       (doseq [shape shapes]
         (seq shape) =not=> nil?))


(facts "Shapes have shape-name, index & 4x4 tile grids."
       (doseq [shape (vals shapes)
               orientation shape]
         (keys orientation) => (just #{:shape-name
                                       :shape-index
                                       :tiles})
         (:shape-name orientation) => string?
         (:shape-index orientation) => integer?
         (count (:tiles orientation)) => 4
         (map count (:tiles orientation)) => [4 4 4 4]))


(fact "shape-bag generates shapes."
      (take 100 shape-bag) => (partial every? (partial contains? (set (apply concat (vals shapes))))))


(fact "shape-bag generates one of each shape before it repeats."
      (let [n (count shapes)
            shape-name-set (set (keys shapes))]
        (take (* 5 n) shape-bag) => #(->> (map :shape-name %)
                                          (partition n)
                                          (map set)
                                          (every? (partial = shape-name-set)))))


(fact "shape-bag randomizes orientations."
      (take 20 shape-bag) => #(-> (map :shape-index %)
                                  (set)
                                  (count)
                                  (> 2)))
