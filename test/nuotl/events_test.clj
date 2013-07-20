(ns nuotl.events-test
  (:use midje.sweet)
  (:require [nuotl.events :as e]
            [clj-time.core :as t]))

(facts
 "Test function that compares dates (ignoring times)"
 (e/compare-dates (t/date-time 2013 1 14) (t/date-time 2013 1 15)) => -1
 (e/compare-dates (t/date-time 2013 1 15) (t/date-time 2013 1 15)) => 0
 (e/compare-dates (t/date-time 2013 1 16) (t/date-time 2013 1 15)) => 1
 )

(facts
 "Test function that checks whether date is in a given month"
 (e/date-in-month? (t/date-time 2013 1 15) 2013 1) => true
 (e/date-in-month? (t/date-time 2013 1 15) 2013 2) => false
 (e/date-in-month? (t/date-time 2013 1 15) 2012 1) => false
 )

(facts
 "Test function that splits events that cross over days into multiple events"
 (let [event {:start (t/date-time 2013 1 29 7) :end (t/date-time 2013 2 2 9)}]
   (let [jan-events (e/split-long-event event 2013 1)
         feb-events (e/split-long-event event 2013 2)]
     (count jan-events) => 3
     (jan-events 0) => {:start (t/date-time 2013 1 29 7) :start-rolled false
                        :end (t/date-time 2013 1 29 23 59 59) :end-rolled true}
     (jan-events 1) => {:start (t/date-time 2013 1 30 0 0 0) :start-rolled true
                        :end (t/date-time 2013 1 30 23 59 59) :end-rolled true}
     (jan-events 2) => {:start (t/date-time 2013 1 31 0 0 0) :start-rolled true
                        :end (t/date-time 2013 1 31 23 59 59) :end-rolled true}
     (count feb-events) => 2
     (feb-events 0) => {:start (t/date-time 2013 2 1 0 0 0) :start-rolled true
                        :end (t/date-time 2013 2 1 23 59 59) :end-rolled true}
     (feb-events 1) => {:start (t/date-time 2013 2 2 0 0 0) :start-rolled true
                        :end (t/date-time 2013 2 2 9) :end-rolled false}
     )))


(def test-events [
                  {:id 101
                   :start (t/date-time 2013 1 30 7)
                   :end (t/date-time 2013 2 1 9)}
                  {:id 102
                   :start (t/date-time 2012 12 25 5)
                   :end (t/date-time 2013 1 1 10)}
                  {:id 103
                   :start (t/date-time 2013 1 15 17 15)
                   :end (t/date-time 2013 1 15 18)
                   }
                  {:id 104
                   :start (t/date-time 2013 1 15 17 4)
                   :end (t/date-time 2013 1 15 19)
                   }
                  ])

(facts
 "Test to-month function"
 (let [month (e/to-month test-events 2013 1)]
   (defn attr [day i key] ((nth (month day) i) key))
   (count month) => 4
   (count (month 1)) => 1
   (count (month 15)) => 2
   (count (month 30)) => 1
   (count (month 31)) => 1
   (attr 1 0 :id) => 102
   (attr 1 0 :start) => (t/date-time 2013 1 1 0 0 0)
   (attr 1 0 :start-rolled) => true
   (attr 1 0 :end) => (t/date-time 2013 1 1 10)
   (attr 1 0 :end-rolled) => false
   (attr 15 0 :id) => 104
   (attr 15 0 :start) => (t/date-time 2013 1 15 17 4)
   (attr 15 0 :start-rolled) => false
   (attr 15 1 :id) => 103
   (attr 30 0 :id) => 101
   (attr 31 0 :id) => 101
   (keys month) => (contains '(1 15 30 31))
   ))
