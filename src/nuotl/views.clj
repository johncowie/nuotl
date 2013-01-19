(ns nuotl.views
  (:require [clj-time.core :as time]
            [clj-time.format :as format]
            [nuotl.areas :as areas]
            [ring.util.response :as ring-response]
            [clojure.tools.logging :as log]
            [nuotl.laser-helper :as laser])
  (:use
        [hiccup.core :only [html]]
        [hiccup.page :only [include-css html5]]
        [hiccup.util :only [url]]
        [nuotl.dao :only [get-events get-features]]
        [nuotl.date-helper :only [month-name day-name get-int-suffix format-date month-year-valid?]]
        [nuotl.events :only [to-month]]))


(defn- get-profile-pic-url [tweeter]
  (format
   "https://api.twitter.com/1/users/profile_image?screen_name=%s&size=normal"
   (tweeter :name)))

(defn- get-tweeter-url [tweeter]
  (url
   (format
         "https://twitter.com/%s"
         (tweeter :name))))

(defn- event-region [event]
  (name ((areas/get-area (event :area)) :region)))

(defn- expired? [end-time]
  (let [n (time/now)]
    (< (compare end-time n) 0)))

(defn- event-status [event]
  (let [end-time (event :end)]
    (if (expired? end-time)
      "expired"
      "")))


(defn- day-status [y m d]
  (if (expired? (time/plus (time/date-time y m d) (time/days 1)))
    "expired"
    "" ))

(defn- get-row-id [event]
  (if (event :start-rolled)
    nil
    (event :_id)
    ))

(defn- fix-tags [tags]
  (clojure.string/join " "
    (map #(format "tag-%s" %) (clojure.string/split tags #" "))))


(defn- event-row [event]
  [:tr {:id (get-row-id event) :class (format "event %s %s %s" (fix-tags (event :tags))
                       (event-status event) (event-region event))}
                   [:td {:class "time"} (format-date (event :start) (event :start-rolled))]
                   [:td {:class "time"} (format-date (event :end) (event :end-rolled))]
                   [:td {:class "text"} (event :text)]
                   [:td {:class "name"}
                    [:a {:href (get-tweeter-url (event :tweeter))}
                     ((event :tweeter) :display-name)]]
                  [:td {:class "area"} ((areas/get-area (event :area)) :name)]
                  [:td
                    [:a {:href (get-tweeter-url (event :tweeter))}
                    [:img {:class "profile" :src (get-profile-pic-url (event :tweeter))}]]
                    ]])

(defn- day-table [day month year events]
                [:table
          [:tr {:class "day-header"}
           [:th {:class (day-status year month day) :colspan 4}
            (format "%s %s%s"(day-name day month year) day (get-int-suffix day))]]
                        (for [event events]
                                (event-row event))])

(defn- get-relative-month-url [y m diff]
  (let [rel-month (time/plus (time/date-time y m 10 0 0 0) (time/months diff))]
    (let [year (time/year rel-month) month (time/month rel-month)]
      (url (format "/events/%s/%s" year month)))))

(defn page-container [title & content]
  (ring-response/content-type
   (ring-response/response
    (laser/index-with-content "public/templates/index.html" title (html content)))
   "text/html"
   ))

(defn event-page [y m]
  (let [yr (read-string y) mth (read-string m)]
    (if (month-year-valid? yr mth)
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
       [:td (format/unparse (format/formatter "dd/MM/yyyy HH:mm") (feature :created-at))]
       [:td (format "@%s"(feature :username))]
       [:td (feature :text)]])]))

(defn- release-html []
  (slurp (clojure.java.io/resource "public/templates/releases.html")))
(defn- instructions-html []
  (slurp (clojure.java.io/resource "public/templates/instructions.html")))

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


(clojure.java.io/resource "project.clj")
