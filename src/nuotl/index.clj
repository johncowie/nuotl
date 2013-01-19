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


(defn format-date [date rolled?]
  (if rolled?
    "--"
    (unparse (formatter "HH:mm") date)))

(defn get-profile-pic-url [tweeter]
  (format
   "https://api.twitter.com/1/users/profile_image?screen_name=%s&size=normal"
   (tweeter :name)))

(defn event-region [event]
  (name ((areas/get-area (event :area)) :region)))

(defn expired? [end-time]
  (let [n (time/now)]
    (< (compare end-time n) 0)))

(defn event-status [event]
  (let [end-time (event :end)]
    (if (expired? end-time)
      "expired"
      "")))


(defn day-status [y m d]
  (if (expired? (time/plus (time/date-time y m d) (time/days 1)))
    "expired"
    "" ))

(defn get-row-id [event]
  (if (event :start-rolled)
    nil
    (event :_id)
    ))

(defn event-row [event]
  [:tr {:id (get-row-id event) :class (format "event %s %s %s" (event :tags)
                       (event-status event) (event-region event))}
                   [:td {:class "time"} (format-date (event :start) (event :start-rolled))]
                   [:td {:class "time"} (format-date (event :end) (event :end-rolled))]
                   [:td {:class "text"} (event :text)]
                  [:td {:class "name"} ((event :tweeter) :display-name)]
                  [:td {:class "area"} ((areas/get-area (event :area)) :name)]
                   [:td
                     [:img {:class "profile" :src (get-profile-pic-url (event :tweeter))}]
                    ]])

(html5 [:div {:id nil} "a"])

(defn day-table [day month year events]
                [:table
          [:tr {:class "day-header"}
           [:th {:class (day-status year month day) :colspan 4}
            (format "%s %s%s"(day-name day month year) day (get-int-suffix day))]]
                        (for [event events]
                                (event-row event))])

(defn get-relative-month-url [y m diff]
  (let [rel-month (time/plus (time/date-time y m 10 0 0 0) (time/months diff))]
    (let [year (time/year rel-month) month (time/month rel-month)]
      (url (format "/events/%s/%s" year month)))))

(defn page-container [title & content]
  (ring-response/content-type
   (ring-response/response
    (laser/index-with-content "public/templates/index.html" title (html content)))
   "text/html"
   ))

(defn monthValid? [m]
  (and (= (class m) Long) (> m 0) (< m 13)))

(defn yearValid? [y]
  (and (= (class y) Long) (> y 2000) (< y 3000)))

(defn monthAndYearValid? [y m]
  (and (monthValid? m) (yearValid? y)))

(defn event-page [y m]
  (let [yr (read-string y) mth (read-string m)]
    (if (monthAndYearValid? yr mth)
      (let [month-map (to-month (get-events yr mth) yr mth)]
        (page-container "Next Up On The Left"
                        [:h1 (format "%s %s" (month-name mth) y)]
                        [:div {:class "month-nav"}
                         [:a {:class "previous" :href (get-relative-month-url yr mth -1)}
                          "Previous"]
                         " "
                         [:a {:class "next" :href (get-relative-month-url yr mth +1)} "Next"]]
                        [:div {:class "clear-float"}]
                        (if (empty? month-map)
                          [:p  "No events have been submitted yet."]
                          (for [[day events] month-map]
                            (day-table day mth yr events)))))
      (page-container "Invalid URL" [:p "Invalid year/month"]))))

(defn feature-page []
  (page-container "Feature Requests"
   [:h1 "Feature Requests"]
   [:table
    (for [feature (get-features)]
      [:tr
       [:td (unparse (formatter "dd/MM/yyyy HH:mm") (feature :created-at))]
       [:td (format "@%s"(feature :username))]
       [:td (feature :text)]])]))

(defn release-html []  (slurp (clojure.java.io/resource "public/templates/releases.html")))
(defn instructions-html []  (slurp (clojure.java.io/resource "public/templates/instructions.html")))

(defn release-page []
  (page-container "Releases"
   [:h1 "Releases"]
   (release-html) ))

(defn instructions-page []
  (page-container "Instructions"
                  (instructions-html)))

(defn page-404 []
  (ring-response/status
   (page-container "Not found" [:p "ERROR 404: These aren't the droids you are looking for"])
   404))

(defn redirect-to-current []
  (let [n (time/now)]
    (ring-response/redirect
     (format "/events/%s/%s" (time/year n) (time/month n)))))

(defn redirect-to-current-month [year]
  (let [n (time/now)]
    (ring-response/redirect
     (format "/events/%s/%s" year (time/month n)))))

(defroutes app-routes
  (GET "/" [] (redirect-to-current))
  (GET "/events/:y/" [y] (redirect-to-current-month y))
  (GET "/events/:y" [y] (redirect-to-current-month y))
  (GET "/events/:y/:m" [y m]  (event-page y m))
  (GET "/features" [] (feature-page))
  (GET "/releases" [] (release-page))
  (GET "/instructions" [] (instructions-page))
  (resources "/")
  (ANY "*" []  (page-404)))

(def app
  (->
   (site app-routes)
   (wrap-resource "/public")))

(defn -main [& args]
  (if (not (empty? args))
    (jetty/run-jetty app {:port (read-string (first args))})
    (jetty/run-jetty app {:port 3000})))
