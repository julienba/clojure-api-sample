(ns app.core
  (:require app.server
            ; to load mount state
            [mount.core :as mount]
            [taoensso.timbre :as log])
  (:gen-class))

(defn -main
  "The entry point for the uberjar.
   All mount component need to be add in the `require` to be started."
  [& _args]
  (log/info "Starting app...")
  (mount/start)
  (deref (promise)))
