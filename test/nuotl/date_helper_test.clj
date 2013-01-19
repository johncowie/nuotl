(ns nuotl.date-helper-test
  (:use midje.sweet)
  (:require [nuotl.date-helper :as d]))

(facts
 (d/get-int-suffix 1) => "st"
 (d/get-int-suffix 2) => "nd"
 (d/get-int-suffix 3) => "rd"
 (d/get-int-suffix 10) => "th")

(facts
 (d/month-valid? 0) => false
 (d/month-valid? 1) => true
 (d/month-valid? 12) => true
 (d/month-valid? 13) => false
 (d/month-valid? -4) => false)
