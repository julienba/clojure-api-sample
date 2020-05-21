(defproject app "0.1.0-SNAPSHOT"
  :description "API Sample using reitit and mount"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [[org.clojure/clojure "1.10.0"]
                 ; logging
                 [com.taoensso/timbre "4.10.0"]
                 ; State management like webservice, database connection and such
                 [mount "0.1.16"]
                 ; Logging the state
                 [tolitius/mount-up "0.1.3"]
                 ; Server routing and extra HTTP feature
                 [metosin/reitit "0.4.2"]
                 ; Rich configuration
                 [aero "1.1.6"]
                 ; Web server adapter
                 [ring/ring-jetty-adapter "1.7.1"]]

  :profiles {:dev {:dependencies [[org.clojure/tools.namespace "1.0.0"]]
                   :repl-options {:init-ns dev
                                  :init (start)}
                   :source-paths ["dev" "src" "test"]}
             :uberjar {:aot :all}}
  :main ^:skip-aot app.core)
