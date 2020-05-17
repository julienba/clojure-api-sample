(ns app.server-test
  (:require [clojure.test :refer :all]
            [app.server :refer :all]
            [app.config :as config]))

(def app (build-app config/config))

(deftest health-route-test
  (testing "Simple route test"
    (is (= {:status 200}
           (app {:request-method :get, :uri "/health/alive"})))
    (is (= {:status 404, :body "", :headers {}}
           (app {:request-method :get, :uri "wrong-url"})))))
