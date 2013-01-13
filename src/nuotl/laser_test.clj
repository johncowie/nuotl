(ns laser-test
  (:use [clojure.java.io :only [resource]])
  (:require [me.raynes.laser :as l]
            [hickory.zip :as hzip]))

(defn test []
  (l/document
   (l/parse (resource "public/templates/index.html"))
   (l/and (l/element= :div) (l/class= "content"))
   (l/content "Hi, I'm a paragraph")))
