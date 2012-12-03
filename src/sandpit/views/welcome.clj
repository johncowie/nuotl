(ns sandpit.views.welcome
  (:require [sandpit.views.common :as common])
  (:use [noir.core :only [defpage]]
        [hiccup.core :only [html]]
		    [hiccup.page-helpers :only [include-css html5]]
        [sandpit.dao.dao :only [get-event]]
        ))

(defpage "/welcome" []
         (common/layout
           [:p "Welcome to sandpit"]))

(defpage "/" []
    (let [event (get-event 1)]
		(html5
            [:head
               [:title "My Noir WebPage"]
               (include-css "/css/reset.css")]
              [:body
               [:div#wrapper
                [:h1 "Hello World!"]
                [:ul
                 [:li (event :id)]
                 [:li (event :start)]
                 [:li (event :end)]
                 [:li (event :html)]
                 [:li (event :tags)]]
				[:p "It works.  Here is some more text.  Can I change it dynamically when running locally?"]
				]])))

