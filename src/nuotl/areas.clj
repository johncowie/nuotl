(ns nuotl.areas
  (:require [nuotl.dao :as dao]))

(def areas
  (read-string (slurp (clojure.java.io/resource "areas.edn"))))

(defn get-area [s]
  (let [key (keyword (clojure.string/lower-case s))]
    (areas key)))

(defn- convert-area-to-map [area]
  (let [k (area 0) v (area 1)]
    {:_id k :name (v :name) :region (v :region)}))

(doseq [area (map convert-area-to-map areas)]
  (dao/add-area area))
