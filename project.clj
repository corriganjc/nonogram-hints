(defproject nonogram-hints "0.1.0-SNAPSHOT"
  :description "A utility for generating hints for nonogram puzzles when given image data."
  :url "https://github.com/corriganjc/nonogram-hints"
  :main ^:skip-aot nonogram-hints.core
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [org.clojure/data.json "1.0.0"]
                 [org.clojure/tools.cli "1.0.194"]]
  :profiles {:uberjar {:aot :all}}
  :test-paths ["test" "test-resources"])
