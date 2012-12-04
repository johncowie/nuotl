(ns sandpit.dao.dao
  (:require
    [monger.core :as mg]
    [monger.collection :as mc])
  (:use [clj-time.core :only [date-time]]))

;(mg/connect!)
;(mg/set-db! (mg/get-db "noir-test-db"))
;
;(mc/insert "documents" {:first_name "John"  :last_name "Lennon"})
;(mc/insert "documents" {:first_name "Paul"  :last_name "McCartney"})
;(mc/insert "documents" {:first_name "Ringo"  :last_name "Starr"})
;(mc/insert "documents" {:first_name "George"  :last_name "Harrison"})
;
;(mg/connect!)

(defn get-event []
  {
    :id 123
    :start (date-time 2012 12 2 7)
    :end (date-time 2012 12 2 9)
    :tweeter {:id 1 :name "johnacowie" :display-name "John Cowie" :approved "Y"}
    :html "This is an event"
    :tags "tag1 tag2 tag3"
    :area "N"
  }
)

(defn get-events []
  [
    {
      :id 1
      :start (date-time 2012 12 5 17)
      :end (date-time 2012 12 8 19)
      :tweeter {:id 1 :name "johnacowie" :display-name "John Cowie" :approved "Y"}
      :html "This is an event"
      :tags "tag1 tag2 tag3"
      :area "N"}
    {
      :id 2
      :start (date-time 2012 12 2 7)
      :end (date-time 2012 12 2 9)
      :tweeter {:id 1 :name "johnacowie" :display-name "John Cowie" :approved "Y"}
      :html "This is an another event"
      :tags "tag4 tag5 tag6"
      :area "N"}
    {
      :id 3
      :start (date-time 2012 12 25)
      :end (date-time 2012 12 26)
      :tweeter {:id 1 :name "johnacowie" :display-name "John Cowie" :approved "Y"}
      :html "Christmas!!"
      :tags "christmas boxingday"
      :area "N"}
    ])

