(ns nuotl.index
  (:use
       [compojure.core]
       [compojure.route :only [not-found resources]]
       [compojure.handler :only [site]]
        [hiccup.core :only [html]]
        [hiccup.page :only [include-css html5]]
        [nuotl.dao :only [get-events]]
        [clj-time.format :only [unparse formatter]]
        [clj-time.core :only [now year month]]
        [nuotl.events :only [to-month]]
        ))

(defn format-date [date] (unparse (formatter "HH:mm") date))

(defn event-row [event]
                 [:tr {:class (format "event %s" (event :tags))}
                   [:td {:class "time"} (format-date (event :start))]
                   [:td {:class "time"} (format-date (event :end))]
                   [:td {:class "text"} (event :text)]
                   [:td {:class "name"} ((event :tweeter) :display-name)]
                  ])

(defn day-table [day events]
                [:table
          [:tr {:class "header"}
           [:th {:colspan 4} day]]
                        (for [event events]
                                (event-row event))])


(defn event-page [y m]
   (let [month-map (to-month (get-events (read-string y) (read-string m)) (read-string y) (read-string m))]
                (html5
            [:head
               [:title (format "Next Up On The Left %s %s" y m)]
               (include-css "/css/reset.css")]
            [:body
               [:div {:class "container"}
                        [:h1 (format "%s/%s" y m)]
                 (for [[day events] month-map]
                                        (day-table day events))]])))

(defroutes app-routes
  (GET "/events/:y/:m" [y m] (event-page y m))
  (resources "/")
  (not-found "These aren't the droids you are looking for..."))

(def app
  (site app-routes))
