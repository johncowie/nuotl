(ns nuotl.events-test
  (:use midje.sweet)
  (:require [nuotl.events :as e]
            [clj-time.core :as t]
            ))

(def test-events [
                  {:id 101
                   :start (t/date-time 2013 1 15 7)
                   :end (t/date-time 2013 1 16 9)
                   :tweeter {:approved "Y"}}
                  {:id 102
                   :start (t/date-time 2013 1 15 7)
                   :end (t/date-time 2013 1 16 9)
                   :tweeter {:approved "N"}}
                  ])


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
 (let [approved (e/get-approved test-events)]
   (count approved) => 1
   ((nth approved 0) :id) => 101
   ))
