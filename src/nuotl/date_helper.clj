(ns nuotl.date-helper
  (:require [clj-time.core :as time]
            [clj-time.format :as format]))

(def day-names {1 "Monday"
                2 "Tuesday"
                3 "Wednesday"
                4 "Thursday"
                5 "Friday"
                6 "Saturday"
                7 "Sunday" })

(def month-names {1 "January"
                  2 "February"
                  3 "March"
                  4 "April"
                  5 "May"
                  6 "June"
                  7 "July"
                  8 "August"
                  9 "September"
                  10 "October"
                  11 "November"
                  12 "December" })

(defn month-name [m]
  (month-names m))

(defn day-name [d m y]
  (day-names (time/day-of-week (time/date-time y m d 12 0 0))))

(defn get-int-suffix [i]
  (case (rem i 100)
    (11 12 13) "th"
    (case (rem i 10)
      1 "st" 2 "nd" 3 "rd" "th")))

(defn month-valid? [m]
  (and (= (class m) Long) (> m 0) (< m 13)))

(defn year-valid? [y]
  (and (= (class y) Long) (> y 2000) (< y 3000)))

(defn month-year-valid? [y m]
  (and (month-valid? m) (year-valid? y)))

(defn format-date [date rolled?]
  (if rolled?
    "--"
    (format/unparse (format/formatter "HH:mm") date)))
