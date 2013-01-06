(defproject nuotl "0.1.0-SNAPSHOT"
            :description "FIXME: write this!"
            :dependencies [[org.clojure/clojure "1.4.0"]
                           [hiccup "1.0.2"]
                           [com.novemberain/monger "1.4.0"]
                           [clj-time "0.4.4"]
                           [cheshire "5.0.0"]
                           [compojure "1.1.3"]
                           [enlive "1.0.1"]]
            :main sandpit.server
                        :plugins [[lein-ring "0.7.5"]]
                        :ring {:handler nuotl.index/app})
