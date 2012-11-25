(ns sandpit.server
  (:require [noir.server :as server]
	    [sandpit.views
	     common
	     welcome])
  (:gen-class))

(server/load-views-ns 'sandpit.views)

(defn -main [& m]
  (let [mode (keyword (or (first m) :dev))
        port (Integer. (get (System/getenv) "PORT" "8080"))]
    (server/start port {:mode mode
                        :ns 'sandpit})))

(def base-handler 
  (server/gen-handler 
    {:mode :prod, 
     :ns 'sandpit
     :session-cookie-attrs {:max-age 1800000}}))

(defn fix-base-url [handler]
  (fn [request]   
    (with-redefs [noir.options/resolve-url 
                  (fn [url]                   
                    ;prepend context to the relative URLs
                    (if (.contains url "://")
                      url (str (:context request) url)))]
      (handler request))))

(def handler (-> base-handler fix-base-url))

