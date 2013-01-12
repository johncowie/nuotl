(ns nuotl.middleware)

(defn request-printer [handler]
  (fn [request]
    (println (format "INCOMING REQUEST: %s" request))
    (println (format "SERVER: %s:%s" (request :server-name) (request :server-port)))
    (handler request)))


(defn wrap-uri-prefix [handler prefix]
  (fn [request]
    (let [response (handler (assoc request
                              :uri (clojure.string/replace-first (:uri request)
                                                                 (re-pattern (str "^" prefix "/?"))
                                                                 "/")))]
      (if (<= 300 (:status response) 308) ; Only rewrite redirects
        (assoc response
          :headers (assoc (:headers response)
                     "Location" (clojure.string/replace-first (get-in response [:headers "Location"])
                                                              #"^/"
                                                              (str prefix "/"))))
        response))))
