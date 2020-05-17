(ns app.exception-middleware
  (:require [reitit.ring.middleware.exception :as exception]
            [ring.adapter.jetty :as jetty]
            [taoensso.timbre :as log]))

(defn wrap-exception [handler ^Throwable e request]
  (log/errorf "For URL %s => Error: %s - %s" (select-keys request [:request-method :uri]) (str (.getClass e)) (.getMessage e))
  (handler e request))

(defn exception-handler
  "Customize error return and prevent error leaking on non dev setup"
  [dev? message exception {:keys [uri request-method] :as request}]
  (if dev?
    {:status 500
     :body {:message message
            :exception (.getClass exception)
            :data (ex-data exception)
            :request-method request-method
            :uri uri}}
    {:status 500
     :body "Oops an error occurred"}))

(defn exception-middleware
  "Manage error return by the WebServer"
  [with-dev-handler?]
  (exception/create-exception-middleware
   (merge exception/default-handlers
          {;; override the default handler
           ::exception/default (partial exception-handler with-dev-handler? "default")
           ;; Log error
           ::exception/wrap wrap-exception}
          ; Don't return coercion error in production
          (when-not with-dev-handler?
            {:reitit.coercion/request-coercion (partial exception-handler with-dev-handler? "coercion")
             :reitit.coercion/response-coercion (partial exception-handler with-dev-handler? "coercion")}))))
