(ns nuotl.dao
  (:require
    [clj-http.client :as client]
    [cheshire.core :as json]
    [clj-time.format :as timef]
    [nuotl.config :refer [load-config! config]]))

(load-config! "config/local-acceptance.yml")

(defn root-url []
  (format "%s://%s:%s"
          (get-in (config) [:cache :scheme])
          (get-in (config) [:cache :host])
          (get-in (config) [:cache :port])))

; TODO is there some sort of http plugin to deal with parsing dates?
(defn get-events [y m]
  (map
   #(merge % {:start (timef/parse (:start %)) :end (timef/parse (:end %))})
   (json/parse-string
    (:body (client/get (format "%s/events/%s/%s" (root-url) y m))) #(keyword %))))
;change path params to query params

(defn get-tweeters []
  (json/parse-string (:body (client/get (format "%s/tweeters" (root-url)))) #(keyword %)))

(defn get-areas []
  (json/parse-string (:body (client/get (format "%s/areas" (root-url)))) #(keyword %)))
