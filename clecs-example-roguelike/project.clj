(defproject clecs-example-roguelike "0.1.0-SNAPSHOT"
  :description "A simple rogue-like game written using clecs."
  :url "https://github.com/muhuk/clecs-examples"
  :license {:name "GNU GPL v3"
            :url "http://www.gnu.org/licenses/gpl-3.0.en.html"}
  :dependencies [[org.clojure/clojure "1.7.0-alpha5"]
                 [clecs "2.0.0-SNAPSHOT"]
                 [clojure-lanterna "0.9.4"]
                 [com.stuartsierra/component "0.2.2"]]
  :profiles {:dev {:source-paths ["dev"]
                   :dependencies [[org.clojure/tools.namespace "0.2.9"]
                                  [org.clojure/java.classpath "0.2.2"]]}}
  :main clecs-example-roguelike.core)
