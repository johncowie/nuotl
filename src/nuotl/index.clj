(ns nuotl.index
  (:require [clj-time.core :as time]
            [nuotl.areas :as areas]
            [ring.adapter.jetty :as jetty])
  (:use
       [compojure.core]
       [compojure.route :only [not-found resources]]
       [compojure.handler :only [site]]
        [hiccup.core :only [html]]
        [hiccup.page :only [include-css html5]]
        [hiccup.middleware :only [wrap-base-url]]
        [nuotl.dao :only [get-events get-features]]
        [clj-time.format :only [unparse formatter]]
        [nuotl.events :only [to-month]]
        [ring.util.response :only [redirect]])
  (:gen-class))


(defn format-date [date]
  (if (date :rolled)
    "--"
    (unparse (formatter "HH:mm") (date :value))))

(defn get-profile-pic-url [tweeter]
  (format
   "https://api.twitter.com/1/users/profile_image?screen_name=%s&size=bigger"
   (tweeter :name)))

(defn event-row [event]
  [:tr {:class (format "event %s %s" (event :tags)
                       (name ((areas/get-area (event :area)) :region)))}
                   [:td {:class "time"} (format-date (event :start))]
                   [:td {:class "time"} (format-date (event :end))]
                   [:td {:class "text"} (event :text)]
                  [:td {:class "name"} ((event :tweeter) :display-name)]
                  [:td {:class "area"} ((areas/get-area (event :area)) :name)]
                   [:td
                     [:img {:src (get-profile-pic-url (event :tweeter))}]
                   ]
                  ])

(def day-names {1 "Monday"
                2 "Tuesday"
                3 "Wednesday"
                4 "Thursday"
                5 "Friday"
                6 "Saturday"
                7 "Sunday"
                })

(def month-names {1 "January"
                  2 "February"
                  3 "March"
                  4 "April"
                  5 "May"
                  6 "June"
                  7 "July"
                  8 "August"
                  9 "September"
                  10 "October"
                  11 "November"
                  12 "December"
                  })

(defn day-name [d m y]
  (day-names (time/day-of-week (time/date-time y m d 12 0 0))))

(defn get-int-suffix [i]
  (case (rem i 100)
    (11 12 13) "th"
    (case (rem i 10)
      1 "st" 2 "nd" 3 "rd" "th")))

(defn day-table [day month year events]
                [:table
          [:tr {:class "header"}
           [:th {:colspan 4}
            (format "%s %s%s"(day-name day month year) day (get-int-suffix day))]]
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
        [:title (format "0.1.0 ALPHA - Next Up On The Left %s %s" y m)]
        (include-css "/css/reset.css")]
       [:body
        [:div {:class "container"}
         [:a {:href "/features"} "Feature Requests"]
         " " [:a {:href "/releases"} "Releases"]
         " " [:a {:href "/instructions"} "Instructions"]
         [:h1 (format "%s %s" (month-names mth) y)]
         [:a {:href (get-relative-month-url yr mth -1)} "Previous"]
         " "
         [:a {:href (get-relative-month-url yr mth +1)} "Next"]
         (for [[day events] month-map]
           (day-table day mth yr events))]]))))

(defn feature-page []
  (html5
   [:head
    [:title "Feature Requests"]
    (include-css "/css/reset.css")]
   [:body
    [:div {:class "container"}
     [:h1 "Feature Requests"]
     [:table
      (for [feature (get-features)]
        [:tr
         [:td (unparse (formatter "dd/MM/yyyy HH:mm") (feature :created-at))]
         [:td (format "@%s"(feature :username))]
         [:td (feature :text)]])]]]))

(defn release-page []
  (html5
   [:head
    [:title "Releases"]
    (include-css "/css/reset.css")]
   [:body
    [:div {:class "container"}
     [:h1 "Releases"]
     (slurp (clojure.java.io/resource "public/templates/releases.html"))
     ]]))

(defn instructions-page []
  (html5
   [:head
    [:title "Instructions"]
    (include-css "/css/reset.css")
    ]
   [:body
    (slurp (clojure.java.io/resource "public/templates/instructions.html"))]))

(defn current-month-url []
  (let [n (time/now)]
    (format "/events/%s/%s" (time/year n) (time/month n))))


(defroutes app-routes
  (GET "/" []  (redirect (current-month-url)))
  (GET "/events/:y/:m" [y m] (event-page y m))
  (GET "/features" [] (feature-page))
  (GET "/releases" [] (release-page))
  (GET "/instructions" [] (instructions-page))
  (resources "/")
  (not-found "These aren't the droids you are looking for..."))

(def app
  (->
   (site app-routes)
   (wrap-base-url)))

(defn -main [& args]
  (if (not (empty? args))
    (jetty/run-jetty app {:port (read-string (first args))})
    (jetty/run-jetty app {:port 3000})))
