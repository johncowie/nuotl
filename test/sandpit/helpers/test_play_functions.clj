(ns sandpit.helpers.test-play-functions
  (:use clojure.test)
  (:use sandpit.helpers.play-functions))

(deftest test-square
  (is (= 9 (square 3)))
  (is (= 16 (square -4))))

(deftest test-cube
  (is (= 27 (cube 3)))
  (is (= -64 (cube -4))))

