(ns functional-example.logic-test
  (:require
   [clojure.test :refer [deftest is testing]]
   [functional-example.logic :refer [count-aliases]]))

(deftest count-aliases-test
  (testing "missing aliases key means zero aliases"
    (is (= 0 (count-aliases {}))))

  (testing "nil punning, also zero aliases"
    (is (= 0 (count-aliases nil))))

  (testing "empty aliases map means zero aliases"
    (is (= 0 (count-aliases {:aliases {}}))))

  (testing "aliases are counted"
    (is (= 1 (count-aliases {:aliases {:a {}}})))
    (is (= 3 (count-aliases {:aliases {:a {} :b {} :c {}}})))))
