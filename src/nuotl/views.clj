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
        [nuotl.dao :only [get-events]]
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
  (if (nil? (event :start))
    nil
    (event :_id)
    ))

; needed for backward data compatibility
(defn- get-tag-array [tags]
  (if (= (class tags) java.lang.String)
    (clojure.string/split tags #" ")
    tags))

(defn- fix-tags [tags]
  (let [tag-array (get-tag-array tags)]
    (clojure.string/join " " (map #(format "tag-%s" %) tag-array))))


(defn- event-row [event]
  [:div {:id (str "E" (get-row-id event)) :class (format "event %s %s %s" (fix-tags (event :tags))
                       (event-status event) (event-region event))}
                   [:span {:class "data time"} (format-date (event :start))]
                   [:span {:class "data time"} (format-date (event :end))]
                   [:span {:class "data text"} (event :text)]
                   [:span {:class "data name"}
                    [:a {:href (get-tweeter-url (event :tweeter))}
                     ((event :tweeter) :display-name)]]
                  [:span {:class "data area"} ((areas/get-area (event :area)) :name)]
                  [:span {:class "profile"}
                    [:a {:href (get-tweeter-url (event :tweeter))}
                    [:img {:src (get-profile-pic-url (event :tweeter))}]]
                    ]])

(defn- day-table [day month year events]
  [:div
   [:div {:class (format "day-header %s" (day-status year month day))}
    (format "%s %s%s"(day-name day month year) day (get-int-suffix day))]
   (for [event events]
     (event-row event))])

(defn- get-relative-month-url [y m diff]
  (let [rel-month (time/plus (time/date-time y m 10 0 0 0) (time/months diff))]
    (let [year (time/year rel-month) month (time/month rel-month)]
      (url (format "/events/%s/%s" year month)))))

(defn tidyHTML [html]
  (if false
    (let [is (java.io.ByteArrayInputStream. (. html (getBytes)))
          os (java.io.StringWriter. )
          tidy (org.w3c.tidy.Tidy.)]
      (. tidy (setTidyMark false))
      (. tidy (setAsciiChars false))
      (println (. tidy (getInputEncoding)))
      (println (. tidy (getOutputEncoding)))
      (. tidy (setOutputEncoding "UTF8"))
      (. tidy (setInputEncoding "UTF8"))
      (. tidy (setRawOut true))
      (. tidy (setQuiet true))
      (. tidy (setShowErrors 0))
      (. tidy (setShowWarnings false))
      (. tidy (setIndentContent true))
      (. tidy (setTrimEmptyElements false))
      (. tidy (parse is os))
      (. os (toString)))
    html
    ))

(defn page-container [title & content]
  (ring-response/content-type
   (ring-response/response
    (tidyHTML
     (laser/index-with-content "public/templates/index.html" title (html content))))
   "text/html"
   ))

(defn event-page [y m]
  (let [yr (read-string y) mth (read-string m)]
    (if (month-year-valid? yr mth)
      (let [month-map (to-month (get-events yr mth) yr mth)]
        (page-container "Next Up On The Left"
                        [:div {:class "month-nav"}
                         [:a {:class "prev" :href (get-relative-month-url yr mth -1)} "&larr;"]
                         [:h1 {:class "month-name"} (format "%s %s" (month-name mth) y)]
                         [:a {:class "next" :href (get-relative-month-url yr mth +1)} "&rarr;"]
                         ]
                        [:div {:class "clear-float"}]
                        (if (empty? month-map)
                          [:p  "No events have been submitted yet."]
                          (for [[day events] month-map]
                            (day-table day mth yr events)))))
      (page-container "Invalid URL" [:p "Invalid year/month"]))))

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
