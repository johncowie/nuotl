(ns nuotl.laser-helper
  (:require [clojure.java.io :as io]
            [me.raynes.laser :as l]))

(defn index-with-content [resource title stuff]
  (l/document
   (l/parse
    (io/resource resource))
   (l/and (l/element= :div) (l/class= "content"))
   (l/html-content stuff)
   (l/element= :title)
   (l/content title)))
