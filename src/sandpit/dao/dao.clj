(ns sandpit.dao.dao
  (:require
    [monger.core :as mg]
    [monger.collection :as mc]
   	[monger.joda-time])
  (:use [clj-time.core :only [date-time]]))

(mg/connect!)
(mg/set-db! (mg/get-db "noir-test-db"))

(defn add-event [event]
	(mc/insert "events" event))

(defn get-event [id]
  (mc/find-one-as-map "events" {:id id}))

(defn get-all-events []
  	(mc/find-maps "events"))


;(add-event { :id 3 :start (date-time 2012 12 12 15) :end (date-time 2012 12 15 17)
;    :tweeter {:id 2 :name "lukeskywalker" :display-name "Luke Skywalker" :approved "Y"}
;    :html "May the force be with you" :tags "tag2 tag3" :area "BL"})

