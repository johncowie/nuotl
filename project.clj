(defproject sandpit "0.1.0-SNAPSHOT"
            :description "FIXME: write this!"
            :dependencies [[org.clojure/clojure "1.3.0"]
                           [noir "1.2.2-SNAPSHOT"]
                           [com.novemberain/monger "1.3.4"]
                           [clj-time "0.4.4"]]
            :main sandpit.server
			:plugins [[lein-ring "0.7.5"]] 
			:ring {:handler sandpit.server/handler} )

