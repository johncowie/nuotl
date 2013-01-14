(ns nuotl.dao
  (:require
    [monger.core :as mg]
    [monger.collection :as mc]
        [monger.joda-time])
  (:use [clj-time.core :only [date-time plus months now year month]]
        [nuotl.events :only [to-month]]
        [monger.operators]))

(mg/connect!)
(mg/set-db! (mg/get-db "nuotl"))

(defn- split-event [event]
        {:event (assoc event :tweeter ((event :tweeter) :_id)) :tweeter (event :tweeter)})

(defn add-event [event]
  (let [components (split-event event)]
        (mc/save "event" (components :event))
        (mc/save "tweeter" (components :tweeter))))

(defn add-area [area]
  (mc/save "area" area))

(defn get-event [id]
  (mc/find-one-as-map "event" {:_id id}))

(defn get-events [y m]
  (let [start-date (date-time y m) end-date (plus (date-time y m) (months 1))]
        (let [events (mc/find-maps "event" {:start {$gte start-date $lt end-date}}) updated-events (transient [])]
      (doseq [event events]
        (conj! updated-events (assoc event :tweeter (mc/find-one-as-map "tweeter" {:_id (event :tweeter)}))))
      (persistent! updated-events))))

(defn get-features []
  (sort-by #(% :created-at) (mc/find-maps "feature" {})))
