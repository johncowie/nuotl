(ns nuotl.index
  (:require [clj-time.core :as time]
            [ring.adapter.jetty :as jetty]
            [ring.util.response :as ring-response]
            [clojure.tools.logging :as log]
            [nuotl.views :as views])
  (:use
       [compojure.core]
       [compojure.route :only [not-found resources]]
       [compojure.handler :only [site]]
       [ring.middleware.resource :only [wrap-resource]])
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
  (GET "/features" [] (views/feature-page))
  (GET "/releases" [] (views/release-page))
  (GET "/instructions" [] (views/instructions-page))
  (resources "/")
  (ANY "*" []  (views/page-404)))

(def app
  (->
   (site app-routes)
   (wrap-resource "/public")))

(defn -main [& args]
  (if (not (empty? args))
    (jetty/run-jetty app {:port (read-string (first args))})
    (jetty/run-jetty app {:port 3000})))
