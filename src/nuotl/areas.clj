(ns nuotl.areas)

(def areas
  (read-string (slurp (clojure.java.io/resource "areas.edn"))))

(defn get-area [s]
  (let [key (keyword (clojure.string/lower-case s))]
    (areas key)))
