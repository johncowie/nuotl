(ns nuotl.events
  (:use [clj-time.core :only [date-time day month year hour plus days]]))

(defn- get-day [event] (day (event :start)))

(defn- get-hour [event] (hour (event :start)))

(defn- compare-dates [date1 date2]
  (let [day1 (date-time (year date1) (month date1) (day date1))
        day2 (date-time (year date2) (month date2) (day date2))]
    (compare day1 day2)))

(defn- sub-event-dates [event start end]
 (assoc (assoc event :start start) :end end))

(defn- split-long-event [event this-year this-month]
    (loop [date (event :start) events (transient [])]
		(if (<= (compare-dates date (event :end)) 0)
		  (do
            (if (and (= (month date) this-month) (= (year date) this-year))
          		(conj! events (sub-event-dates event 
                                               (if (== (compare-dates date (event :start)) 0) 
                                                 (event :start) 
                                                 (date-time this-year this-month (day date))
                                                 )
                                               (if (== (compare-dates date (event :end)) 0)
                                                 (event :end)
                                               	(date-time this-year this-month (day date) 23 59 59)
                                                )
                                               )))
          	(recur (plus date (days 1)) events))
         (persistent! events))))

(defn- split-long-events [events y m] 
       (flatten (map (fn [e] (split-long-event e y m)) events)))

(defn to-month [events y m]
	(into {} (map (fn [event-group] [(event-group 0) 
       (sort-by get-hour (event-group 1))]) 
     (group-by get-day (split-long-events events y m)))))
