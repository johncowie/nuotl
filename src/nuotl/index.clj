(ns nuotl.index
  (:require [clj-time.core :as time]
            [ring.adapter.jetty :as jetty]
            [ring.util.response :as ring-response]
            [clojure.tools.logging :as log]
            [nuotl.views :as views]
            [nuotl.config :refer load-config!]
            [compojure.core :refer [GET ANY defroutes]]
            [compojure.route :refer [not-found resources]]
            [compojure.handler :refer [site]]
            [ring.middleware.resource :refer [wrap-resource]]
            )
  (:gen-class))

(defn redirect-to-current []
  (let [n (time/now)]
    (ring-response/redirect
     (format "/events/%s/%s" (time/year n) (time/month n)))))

(defn redirect-to-current-month [year]
  (let [n (time/now)]
    (ring-response/redirect
     (format "/events/%s/%s" year (time/month n)))))

(defroutes app-routes
  (GET "/" [] (redirect-to-current))
  (GET "/events/:y/" [y] (redirect-to-current-month y))
  (GET "/events/:y" [y] (redirect-to-current-month y))
  (GET "/events/:y/:m" [y m]  (views/event-page y m))
  ;(GET "/features" [] (views/feature-page))
  ;(GET "/releases" [] (views/release-page))
  (GET "/instructions" [] (views/instructions-page))
  (resources "/")
  (ANY "*" []  (views/page-404)))

(def app
  (->
   (site app-routes)
   (wrap-resource "/public")))

(defn -main [& args]
  (jetty/run-jetty app (get-in (load-config! (first args)) [:http :port])))
