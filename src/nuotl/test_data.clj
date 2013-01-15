(ns nuotl.test-data
  (:require [nuotl.dao :as dao])
  (:use [clj-time.core :only [date-time]]))

(def data [
    {
        :_id 76
        :start (date-time 2013 1 15 9)
        :end (date-time 2013 1 15 9 1)
        :text "Rememberance of Aaron Swartz"
        :tweeter {:name "thought works" :display-name "ThoughtWorks"
                  :_id "1" :approved "yes"}
        :tags "environment"
        :area "N"
     }
])

(defn add-test-data []
  (map dao/add-event data))
