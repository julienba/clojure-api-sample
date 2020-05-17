(ns app.server
  (:require [app.config :as config]
            [app.exception-middleware :refer [exception-middleware]]
            [app.route.api :as route.api]
            [app.route.health :as route.health]
            [mount.core :as mount :refer [defstate]]
            [muuntaja.core :as m]
            [reitit.ring :as ring]
            [reitit.coercion.spec]
            [reitit.swagger :as swagger]
            [reitit.swagger-ui :as swagger-ui]
            [reitit.ring.coercion :as coercion]
            [reitit.dev.pretty :as pretty]
            [reitit.ring.middleware.muuntaja :as muuntaja]
            [reitit.ring.middleware.exception :as exception]
            [reitit.ring.middleware.multipart :as multipart]
            [reitit.ring.middleware.parameters :as parameters]
            [ring.adapter.jetty :as jetty]
            [taoensso.timbre :as log]))

(def swagger-route
  ["/swagger.json"
   {:get {:no-doc true
          :swagger {:info {:title "my-api"}}
          :handler (swagger/create-swagger-handler)}}])

(def routes
  [route.api/route
   route.health/route])

(defn build-routes
  "Helper to build the route depending on the configuration"
  [{{:keys [with-swagger?]} :features :as cfg}]
  (concat
    (when with-swagger?
      [swagger-route])
    routes))

(defn build-middlewares
  "Helper to build the middleware depending on the configuration (ie. removing debug information on a public facong setup)"
  [{{:keys [with-swagger? with-dev-handler?]} :features :as cfg}]
  (concat
    (when with-swagger?
      ;; swagger feature
      [swagger/swagger-feature])

    [;; query-params & form-params
     parameters/parameters-middleware
     ;; content-negotiation
     muuntaja/format-negotiate-middleware
     ;; encoding response body
     muuntaja/format-response-middleware
     ;; exception handling
     (exception-middleware with-dev-handler?)
     ;; decoding request body
     muuntaja/format-request-middleware
     ;; coercing response bodys
     coercion/coerce-response-middleware
     ;; coercing request parameters
     coercion/coerce-request-middleware
     ;; multipart
     multipart/multipart-middleware]))

(defn build-app
  "Create the WebServer"
  [{{:keys [with-swagger?]} :features :as cfg}]
  (ring/ring-handler
   (ring/router
    (build-routes cfg)
    {:exception pretty/exception
     :data {:muuntaja m/instance
            :coercion reitit.coercion.spec/coercion
            :middleware (build-middlewares cfg)}})
   (ring/routes
    (when with-swagger?
      (swagger-ui/create-swagger-ui-handler
       {:path "/"
        :config {:validatorUrl nil
                 :operationsSorter "alpha"}}))
    (ring/create-default-handler))))

(defn- start [{:keys [port] :as cfg}]
  (log/infof "Start server using port %s" port)
  (jetty/run-jetty (build-app config/config)
                   {:port port, :join? false, :async true}))

(defstate server :start (start config/config)
                 :stop (when server
                         (.stop server)))
