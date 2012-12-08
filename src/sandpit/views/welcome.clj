(ns sandpit.views.welcome
  (:use [noir.core :only [defpage defpartial]]
        [hiccup.core :only [html]]
		[hiccup.page-helpers :only [include-css html5]]
        [sandpit.dao.dao :only [get-events]]
        [clj-time.format :only [unparse formatter]]
        [sandpit.helpers.events :only [to-month]]
        ))

(defn format-date [date] (unparse (formatter "HH:mm") date))

(defpartial event-row [event]
                 [:tr
                   [:td (format-date (event :start))]
                   [:td (format-date (event :end))]
                   [:td (event :html)]
                   [:td ((event :tweeter) :display-name)]])

(defpartial day-table [day events]
  		[:table 
          [:tr [:th day]] 
			(for [event events]
				(event-row event))])

(defpage "/:year/:month" {:keys [year month]}
    (let [month-map (to-month (get-events (read-string year) (read-string month)))]
		(html5
            [:head
               [:title (format "Next Up On The Left %s %s" year month)]
               ;(include-css "/css/reset.css")
             ]
              [:body
               [:div#wrapper
                [:h1 "events"]
                [:table
                 (for [[day events] month-map]
					(day-table day events))]]])))


