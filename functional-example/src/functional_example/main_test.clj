(ns functional-example.main-test
  (:require
   [clojure.test :refer [deftest is]]
   [functional-example.main :as main]))

;; Smoke Test to test the program end-to-end for the single case of deps.edn in the project.
;; We don't rely on smoke tests to catch errors for all use cases, those are
;; covered by the Narrow Sociable tests.
(deftest smoke-test
  (is (= "3\n" (with-out-str (main/-main)))))
