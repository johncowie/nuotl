(ns sandpit.dao.dao
  (:require
    [monger.core :as mg]
    [monger.collection :as mc]
   	[monger.joda-time])
  (:use [clj-time.core :only [date-time plus months]]
        [sandpit.helpers.events :only [to-month]]
        [monger.operators]))

(mg/connect!)
(mg/set-db! (mg/get-db "noir-test-db"))

(defn add-event [event]
	(mc/insert "events" event))

(defn get-event [id]
  (mc/find-one-as-map "events" {:id id}))

(defn get-events [year month]
  (let [start-date (date-time year month) end-date (plus (date-time year month) (months 1))]
  (mc/find-maps "events" {:start {$gte start-date $lt end-date}})))


;(add-event { :id 4 :start (date-time 2012 12 12 15) :end (date-time 2012 12 15 17)
:tweeter {:id 4 :name "darthvadar" :display-name "Darth Vadar" :approved "Y"}
;    :html "I am your father Luke" :tags "death star" :area "S"})

