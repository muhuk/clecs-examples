(defproject clecs-example-rougelike "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "GNU GPL v3"
            :url "http://www.gnu.org/licenses/gpl-3.0.en.html"}
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [clecs "0.2.0"]
                 [clojure-lanterna "0.9.4"]
                 [com.stuartsierra/component "0.2.1"]]
  :profiles {:dev {:source-paths ["dev"]
                   :dependencies [[org.clojure/tools.namespace "0.2.4"]
                                  [org.clojure/java.classpath "0.2.2"]]}}
  :main clecs-example-rougelike.core)
