(ns nuotl.index
  (:require [clj-time.core :as time])
  (:use
       [compojure.core]
       [compojure.route :only [not-found resources]]
       [compojure.handler :only [site]]
        [hiccup.core :only [html]]
        [hiccup.page :only [include-css html5]]
        [nuotl.dao :only [get-events]]
        [clj-time.format :only [unparse formatter]]
        [nuotl.events :only [to-month]]
        [ring.util.response :only [redirect]]
        ))

(defn format-date [date] (unparse (formatter "HH:mm") date))

(defn get-profile-pic-url [tweeter]
  (format
   "https://api.twitter.com/1/users/profile_image?screen_name=%s&size=bigger"
   (tweeter :name)))

(defn event-row [event]
                 [:tr {:class (format "event %s" (event :tags))}
                   [:td {:class "time"} (format-date (event :start))]
                   [:td {:class "time"} (format-date (event :end))]
                   [:td {:class "text"} (event :text)]
                   [:td {:class "name"} ((event :tweeter) :display-name)]
                   [:td
                     [:img {:src (get-profile-pic-url (event :tweeter))}]
                   ]
                  ])

(defn day-table [day events]
                [:table
          [:tr {:class "header"}
           [:th {:colspan 4} day]]
                        (for [event events]
                                (event-row event))])

(defn get-relative-month-url [y m diff]
  (let [rel-month (time/plus (time/date-time y m 10 0 0 0) (time/months diff))]
    (let [year (time/year rel-month) month (time/month rel-month)]
      (format "/events/%s/%s" year month))))

(defn event-page [y m]
  (let [yr (read-string y) mth (read-string m)]
    (let [month-map (to-month (get-events yr mth) yr mth)]
      (html5
       [:head
        [:title (format "Next Up On The Left %s %s" y m)]
        (include-css "/css/reset.css")]
       [:body
        [:div {:class "container"}
         [:a {:href (get-relative-month-url yr mth -1)} "Previous"]
         [:a {:href (get-relative-month-url yr mth +1)} "Next"]
         [:h1 (format "%s/%s" y m)]
         (for [[day events] month-map]
           (day-table day events))]]))))

(defn current-month-url []
  (let [n (time/now)]
    (format "/events/%s/%s" (time/year n) (time/month n))))


(defroutes app-routes
  (GET "/" []  (redirect (current-month-url)))
  (GET "/events/:y/:m" [y m] (event-page y m))
  (resources "/")
  (not-found "These aren't the droids you are looking for..."))

(def app
  (site app-routes))
