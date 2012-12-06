(defproject sandpit "0.1.0-SNAPSHOT"
            :description "FIXME: write this!"
            :dependencies [[org.clojure/clojure "1.4.0"]
                           [noir "1.2.1"]
                           [com.novemberain/monger "1.4.0"]
                           [clj-time "0.4.4"]
                           [cheshire "5.0.0"]]
            :main sandpit.server
			:plugins [[lein-ring "0.7.5"]] 
			:ring {:handler sandpit.server/handler} )