(defproject clecs-tetris "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "GNU GPL v3"
            :url "http://www.gnu.org/licenses/gpl-3.0.en.html"}
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [clecs "2.0.1"]
                 [seesaw "1.4.5"]]
  :main ^:skip-aot clecs-tetris.core
  :target-path "target/%s"
  :profiles {:dev {:dependencies [[midje "1.6.3"]]}
             :uberjar {:aot :all}}
  :plugins [[lein-midje "3.1.3"]])
