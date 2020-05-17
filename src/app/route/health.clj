(ns app.route.health)

(def route
  ["/health"
   ["/alive" {:summary "Is the server running?"
              :get {:handler (fn [_] {:status 200})}}]])
