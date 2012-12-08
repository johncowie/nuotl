(ns sandpit.server
  (:require [noir.server :as server]
	    [sandpit.views welcome])
  (:gen-class))

(server/load-views-ns 'sandpit.views)

(defn -main [& m]
  (let [mode (keyword (or (first m) :dev))
        port (Integer. (get (System/getenv) "PORT" "8080"))]
    (server/start port {:mode mode
                        :ns 'sandpit})))

(def handler 
  (server/gen-handler 
    {:mode :prod, 
     :ns 'sandpit
     :session-cookie-attrs {:max-age 1800000}}))