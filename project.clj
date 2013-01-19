(defproject nuotl "0.1.2"
            :description "FIXME: write this!"
            :dependencies [[org.clojure/clojure "1.4.0"]
                           [hiccup "1.0.2"]
                           [com.novemberain/monger "1.4.0"]
                           [clj-time "0.4.4"]
                           [cheshire "5.0.0"]
                           [compojure "1.1.3"]
                           [ring/ring-jetty-adapter "1.1.4"]
                           [me.raynes/laser "0.1.17"]
                           [org.clojure/tools.logging "0.2.3"]
                           [midje "1.4.0"]
                           ]
             :main nuotl.index
             :plugins [[lein-ring "0.7.5"]
                       [lein-midje "2.0.1"]]
             :ring {:handler nuotl.index/app})
