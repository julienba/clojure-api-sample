(ns app.config
  (:require [aero.core :as aero]
            [clojure.java.io :as io]
            [mount.core :refer [defstate]]))

(defn read-config []
  (-> "config.edn"
      io/resource
      (aero/read-config)))

(defstate config :start (read-config))
