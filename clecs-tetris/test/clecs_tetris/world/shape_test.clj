(ns clecs-tetris.world.shape-test
  (:require [clecs-tetris.world.shape :refer :all]
            [midje.sweet :refer :all]))


(facts "rotate cycles through orientations."
       (rotate "O" 0) => (first O)
       (rotate "I" 0) => (second I)
       (rotate "I" 1) => (first I)
       (rotate "T" 0) => (nth T 1)
       (rotate "T" 1) => (nth T 2)
       (rotate "T" 2) => (nth T 3)
       (rotate "T" 3) => (nth T 0))


(facts "Shapes have multiple orientations."
       (doseq [shape shapes]
         (seq shape) =not=> nil?))


(facts "Shapes have shape-name, index & 4 filled tiles each with x & y coordinates."
       (doseq [shape (vals shapes)
               orientation shape]
         (keys orientation) => (just #{:shape-name
                                       :shape-index
                                       :tiles})
         (:shape-name orientation) => string?
         (:shape-index orientation) => integer?
         (count (:tiles orientation)) => 4
         (map count (:tiles orientation)) => [2 2 2 2]))


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
