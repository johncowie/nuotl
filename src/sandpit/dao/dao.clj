(ns sandpit.dao.dao
  (:require
    [monger.core :as mg]
    [monger.collection :as mc]))

;(mg/connect!)
;(mg/set-db! (mg/get-db "noir-test-db"))
;
;(mc/insert "documents" {:first_name "John"  :last_name "Lennon"})
;(mc/insert "documents" {:first_name "Paul"  :last_name "McCartney"})
;(mc/insert "documents" {:first_name "Ringo"  :last_name "Starr"})
;(mc/insert "documents" {:first_name "George"  :last_name "Harrison"})
;
;(mg/connect!)

(defn get-event [q]
  {
    :id 123
    :start "Start"
    :end "End"
    :tweeter {:id 1 :name "johnacowie" :display-name "John Cowie" :approved "Y"}
    :html "This is an event"
    :tags "tag1 tag2 tag3"
    :area "N"
  }
)

