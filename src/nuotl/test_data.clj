(ns nuotl.test-data
  (:require [nuotl.dao :as dao])
  (:use [clj-time.core :only [date-time]]))

(def data [
    {
        :_id (rand-int 100000)
        :start (date-time 2013 1 20 18 03)
        :end (date-time 2013 1 20 20)
        :text "Party <a href=\"bbc.co.uk\">the beeb</a>   "
        :tweeter {:name "thoughtworks" :display-name "ThoughtWorks"
                  :_id "1" :approved "Y"}
        :tags "feminism"
        :area "N"
     }
])
