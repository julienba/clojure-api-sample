(ns app.config
  (:require [aero.core :as aero]
            [clojure.java.io :as io]
            [mount.core :refer [defstate]]))

(defn read-config
  "Read the configuration resources/config.edn"
  []
  (-> "config.edn"
      io/resource
      (aero/read-config)))

; Store the configuration in a mount state to be available from other part of your application
(defstate config :start (read-config))
