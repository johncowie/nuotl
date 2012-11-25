(ns sandpit.views.welcome
  (:require [sandpit.views.common :as common])
  (:use [noir.core :only [defpage]]
        [hiccup.core :only [html]]))

(defpage "/welcome" []
         (common/layout
           [:p "Welcome to sandpit"]))

(defpage "/" []             
		(html5
            [:head
               [:title "My Noir WebPage"]
               (include-css "/css/reset.css")]
              [:body
               [:div#wrapper
                [:h1 "Hello World!"]
				[:p "It works.  Here is some more text.  Fa la la la]
				]]))
