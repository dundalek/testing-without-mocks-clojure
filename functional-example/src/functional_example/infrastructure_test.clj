(ns functional-example.infrastructure-test
  (:require
   [clojure.test :refer [deftest is testing]]
   [functional-example.infrastructure :refer [make-null-cwd make-null-home-dir]]))

(deftest make-null-home-dir-test
  (testing "nullable uses embedded stub with a sensible default"
    (is (= "/home/someuser" ((make-null-home-dir)))))

  (testing "nullable can be configured"
    (is (= "/home/otheruser" ((make-null-home-dir {:path "/home/otheruser"}))))))

(deftest make-null-cwd-test
  (testing "nullable uses embedded stub with a sensible default"
    (is (= "/some/current/directory"  ((make-null-cwd)))))

  (testing "nullable can be configured"
    (is (= "/another/directory" ((make-null-cwd {:path "/another/directory"}))))))
