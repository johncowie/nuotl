(ns nuotl.events
  (:use [clj-time.core :only [date-time day month year hour minute sec plus days]]))

(defn- get-day [event] (day (event :start)))

(defn- get-day-seconds [event]
  (+ (sec (event :start)) (* (hour (event :start)) 3600) (* (minute (event :start)) 60)))

(defn compare-dates [date1 date2]
  (let [day1 (date-time (year date1) (month date1) (day date1))
        day2 (date-time (year date2) (month date2) (day date2))]
    (compare day1 day2)))

(defn- sub-event-dates [event current start end start-rolled? end-rolled?]
  (let [s (if start-rolled? (date-time (year current) (month current) (day current) 0 0 0) start)
        e (if end-rolled? (date-time (year current) (month current) (day current) 23 59 59) end)]
    (merge event {:start s :end e :start-rolled start-rolled? :end-rolled end-rolled?})))

(defn date-in-month? [date y m]
  (and (= (year date) y) (= (month date) m)))

(defn split-long-event [event this-year this-month]
    (loop [date (event :start) events (transient [])]
                (if (<= (compare-dates date (event :end)) 0)
                  (do
            (if (date-in-month? date this-year this-month)
              (conj! events (sub-event-dates event
                                             date
                                             (event :start)
                                             (event :end)
                                             (not= (compare-dates date (event :start)) 0)
                                             (not= (compare-dates date (event :end)) 0)
                                             )))
            (recur (plus date (days 1)) events))
                  (persistent! events))))

(defn- split-long-events [events y m]
  (flatten (map (fn [e] (split-long-event e y m)) events)))

(defn to-month [events y m]
        (into (sorted-map) (map (fn [event-group] [(event-group 0)
       (sort-by get-day-seconds (event-group 1))])
          (group-by get-day (split-long-events events y m)))))
