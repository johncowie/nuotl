(ns sandpit.views.welcome
  (:use [noir.core :only [defpage defpartial]]
        [noir.response :only [redirect]]
        [noir.util.test :only [send-request]]
        [hiccup.core :only [html]]
		[hiccup.page-helpers :only [include-css html5]]
        [sandpit.dao.dao :only [get-events]]
        [clj-time.format :only [unparse formatter]]
        [clj-time.core :only [now year month]]
        [sandpit.helpers.events :only [to-month]]
        ))

(defn format-date [date] (unparse (formatter "HH:mm") date))

(defpartial event-row [event]
                 [:tr
                   [:td {:class "time"} (format-date (event :start))]
                   [:td {:class "time"} (format-date (event :end))]
                   [:td {:class "text"} (event :text)]
                   [:td {:class "name"} ((event :tweeter) :display-name)]
                  ])

(defpartial day-table [day events]
  		[:table 
          [:tr [:th {:colspan 4} day]] 
			(for [event events]
				(event-row event))])


(defpartial event-page [y m]
   (let [month-map (to-month (get-events (read-string y) (read-string m)) (read-string y) (read-string m))]
		(html5
            [:head
               [:title (format "Next Up On The Left %s %s" y m)]
               (include-css "/css/reset.css")]
            [:body
               [:div#wrapper
                	[:h1 "events"]
                 (for [[day events] month-map]
					(day-table day events))]])))

(send-request "/2012/12" {})

(defpage "/events/:y/:m" {:keys [y m]}
	(event-page y m))

(defpage "/" {}
	(redirect (format "/events/%s/%s" (year (now)) (month (now)))))










