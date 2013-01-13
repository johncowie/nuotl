(ns nuotl.index
  (:require [clj-time.core :as time]
            [nuotl.areas :as areas]
            [ring.adapter.jetty :as jetty]
            [ring.util.response :as ring-response]
            [clojure.tools.logging :as log]
            [nuotl.laser-helper :as laser])
  (:use
       [compojure.core]
       [compojure.route :only [not-found resources]]
       [compojure.handler :only [site]]
        [hiccup.core :only [html]]
        [hiccup.page :only [include-css html5]]
        [hiccup.util :only [url with-base-url]]
        [hiccup.middleware :only [wrap-base-url]]
        [nuotl.middleware :only [request-printer wrap-uri-prefix]]
        [nuotl.dao :only [get-events get-features]]
        [nuotl.date-helper :only [month-name day-name get-int-suffix]]
        [clj-time.format :only [unparse formatter]]
        [nuotl.events :only [to-month]]
        [ring.middleware.resource :only [wrap-resource]])
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

(defn day-table [day month year events]
                [:table
          [:tr {:class "day-header"}
           [:th {:colspan 4}
            (format "%s %s%s"(day-name day month year) day (get-int-suffix day))]]
                        (for [event events]
                                (event-row event))])

(defn get-relative-month-url [y m diff]
  (let [rel-month (time/plus (time/date-time y m 10 0 0 0) (time/months diff))]
    (let [year (time/year rel-month) month (time/month rel-month)]
      (url (format "/events/%s/%s" year month)))))

(defn page-container [title & content]
  (laser/index-with-content "public/templates/index.html" title (html content)))

(defn event-page [y m]
  (let [yr (read-string y) mth (read-string m)]
    (let [month-map (to-month (get-events yr mth) yr mth)]
      (page-container "Next Up On The Left"
       [:h1 (format "%s %s" (month-name mth) y)]
       [:a {:href (get-relative-month-url yr mth -1)} "Previous"]
       " "
       [:a {:href (get-relative-month-url yr mth +1)} "Next"]
       (for [[day events] month-map]
         (day-table day mth yr events))))))

(defn feature-page []
  (page-container "Feature Requests"
   [:h1 "Feature Requests"]
   [:table
    (for [feature (get-features)]
      [:tr
       [:td (unparse (formatter "dd/MM/yyyy HH:mm") (feature :created-at))]
       [:td (format "@%s"(feature :username))]
       [:td (feature :text)]])]))

(def release-html (slurp (clojure.java.io/resource "public/templates/releases.html")))
(def instructions-html (slurp (clojure.java.io/resource "public/templates/instructions.html")))

(defn release-page []
  (page-container "Releases"
   [:h1 "Releases"]
   release-html ))

(defn instructions-page []
  (page-container "Instructions"
   instructions-html))

(defn redirect-to-current-month []
  (let [n (time/now)]
    (ring-response/redirect
     (format "/events/%s/%s" (time/year n) (time/month n)))))

(defroutes app-routes
  (GET "/" [] (redirect-to-current-month))
  (GET "/events/:y/:m" [y m]  (event-page y m))
  (GET "/features" [] (feature-page))
  (GET "/releases" [] (release-page))
  (GET "/instructions" [] (instructions-page))
  (resources "/")
  (not-found "These aren't the droids you are looking for..."))

(def app
  (->
   (site app-routes)
   (wrap-resource "/public")))

(defn -main [& args]
  (if (not (empty? args))
    (jetty/run-jetty app {:port (read-string (first args))})
    (jetty/run-jetty app {:port 3000})))
