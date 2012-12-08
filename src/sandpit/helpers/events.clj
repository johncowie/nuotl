(ns sandpit.helpers.events
  (:use [clj-time.core :only [date-time day hour]]))

(defn- get-day [event] (day (event :start)))

(defn- get-hour [event] (hour (event :start)))

(defn to-month [events]
	(into {} (map (fn [event-group] [(event-group 0) 
       (sort-by get-hour (event-group 1))]) 
     (group-by get-day events))))