(ns app.route.api
  (:require [clojure.spec.alpha :as s]
            [taoensso.timbre :as log]))

(s/def ::x int?)
(s/def ::y int?)
(s/def ::total int?)

(s/def ::plus-request (s/keys :req-un [::x ::y]))
(s/def ::plus-response (s/keys :req-un [::total]))

(def route
  ["/api"
   ["/minus" {:get {:handler (fn [{{:strs [x y] :as params} :query-params :as req}]
                               (log/infof "Parameter received: %s" params)
                               {:status 200
                                :body {:total (- (Long/parseLong x) (Long/parseLong y))}})}}]
   ["/plus"
    {:get
     {:summary    "plus with spec query parameters"
      :parameters {:query ::plus-request}
      :responses  {200 {:body ::plus-response}}
      :handler    (fn [{{{:keys [x y]} :query} :parameters}]
                    {:status 200
                     :body {:total (+ x y)}})}}]])
