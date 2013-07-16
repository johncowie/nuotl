(ns nuotl.config
  (:require [clj-yaml.core :as yaml]))

(def config-map (atom {}))

(defn load-config! [file-name]
  (swap! config-map #(merge % (yaml/parse-string (slurp file-name)))))

(defn config [] @config-map)
