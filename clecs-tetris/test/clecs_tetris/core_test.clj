(ns clecs-tetris.core-test
  (:require [clecs-tetris.core :refer :all]
            [clecs-tetris.screen :refer [make-screen]]
            [clecs-tetris.world :refer [make-world]]
            [midje.sweet :refer :all]))


(fact "init initializes a world."
      (:world (init)) => ..world..
      (provided (make-screen anything anything) => ..screen..
                (make-world :default-tile anything
                            :escape-hatch anything
                            :event-queue anything
                            :glass-height anything
                            :glass-width anything
                            :gravity-countdown anything
                            :screen ..screen..
                            :update-shape-countdown-duration anything) => ..world..))


(fact "init returns an escape hatch."
      (-> (init) :escape-hatch type supers) => (contains [clojure.lang.IPending])
      (provided (make-screen anything anything) => ..screen..
                (make-world :default-tile anything
                            :escape-hatch anything
                            :event-queue anything
                            :glass-height anything
                            :glass-width anything
                            :gravity-countdown anything
                            :screen anything
                            :update-shape-countdown-duration anything) => ..world..))


(fact "make-timer returns a function that returns dt each time it's called."
      (let [dt (make-timer)] (vector (dt) (dt))) => [4 2]
      (provided (timestamp) =streams=> [1 5 7]))


(fact "run blocks until escape-hatch is realized."
      (let [escape-hatch (promise)
            app {:world --world--
                 :escape-hatch escape-hatch}
            run-thread (future (run app))]
        (Thread/sleep 50) => anything
        (realized? run-thread) => false
        (deliver escape-hatch nil) => anything
        (Thread/sleep 50) => anything
        (realized? run-thread) => true))


(fact "run repeatedly calls process! on world."
      (let [escape-hatch (promise)
            counter (atom 0)
            world (fn [dt] (swap! counter inc))
            app {:world world
                 :escape-hatch escape-hatch}
            run-thread (future (run app))]
        (Thread/sleep 50) => anything
        (realized? run-thread) => false
        ;; Stop the thread.
        (deliver escape-hatch nil) => anything
        @run-thread => nil
        (pos? @counter) => true))
