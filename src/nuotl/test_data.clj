(ns nuotl.test-data
  (:use [clj-time.core :only [date-time]]))

(def data [
	{
		:_id 1
     	:start (date-time 2012 11 23 7)
     	:end (date-time 2012 11 24 9)
     	:text "Luke I am your father"
     	:tweeter {:name "darthvadar" :display-name "Darth Vadar" 
                  :_id "1" :approved "yes"}
     	:tags "environment"
     	:area "S"
    }
    {
		:_id 2
     	:start (date-time 2012 12 2 10)
     	:end (date-time 2012 12 3 12)
     	:text "Coffee on tatooine"
     	:tweeter {:name "lukeskywalker" :display-name "Luke Skywalker" 
                  :_id "2" :approved "yes"}
     	:tags "feminism"
     	:area "CF"
    }
    {
		:_id 3
     	:start (date-time 2012 12 6 17)
     	:end (date-time 2012 12 6 20)
     	:text "Out on the razz with C3P0 and R2D2"
     	:tweeter {:name "lukeskywalker" :display-name "Luke Skywalker" 
                  :_id "2" :approved "yes"}
     	:tags "cuts"
     	:area "G"
    }
    {
		:_id 4
     	:start (date-time 2012 12 6 18)
     	:end (date-time 2012 12 6 20)
     	:text "Dinner at the Cantina"
     	:tweeter {:name "hanssolo" :display-name "Hans Solo" 
                  :_id "3" :approved "yes"}
     	:tags "trade"
     	:area "N"
    }
    {
		:_id 5
     	:start (date-time 2012 12 25)
     	:end (date-time 2012 12 26)
     	:text "Christmas in the swamp"
     	:tweeter {:name "yoda" :display-name "Yoda" 
                  :_id "4" :approved "yes"}
     	:tags "other"
     	:area "S"
    }
])
