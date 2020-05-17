(ns dev
  (:require [clojure.tools.namespace.repl :as repl]
            [mount.core :as mount]
            [mount-up.core :as mu]
            app.server))

; log defstate start & stop
(mu/on-upndown :info mu/log :before)

(defn start []
  (mount/start))

(defn stop []
  (mount/stop))

(defn refresh []
  (stop)
  (repl/refresh))

(defn refresh-all []
  (stop)
  (repl/refresh-all))

(defn go
  "starts all states defined by defstate"
  []
  (start)
  :ready)

(defn reset
  "stops all states defined by defstate, reloads modified source files, and restarts the states"
  []
  (stop)
  (repl/refresh :after 'dev/start))
