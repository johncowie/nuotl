(ns sandpit.views.welcome
  (:use [noir.core :only [defpage]]
        [hiccup.core :only [html]]
		[hiccup.page-helpers :only [include-css html5]]
        [sandpit.dao.dao :only [get-all-events]]
        [clj-time.format :only [unparse formatter]]
        ))

(defn format-date [date] (unparse (formatter "yyyyMMdd") date))

(defpage "/" []
    (let [events (get-all-events)]
		(html5
            [:head
               [:title "My Noir WebPage"]
               (include-css "/css/reset.css")]
              [:body
               [:div#wrapper
                [:h1 "Hello World!"]
                [:table
                 (for [event events]
                 [:tr
                   [:td (format-date (event :start))]
                   [:td (format-date (event :end))]
                   [:td (event :html)]
                   [:td ((event :tweeter) :display-name)]])]]])))


