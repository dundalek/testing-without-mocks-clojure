(ns functional-example.app-test
  (:require
   [clojure.test :refer [deftest is testing]]
   [functional-example.app :refer [make-null-count-aliases
                                   make-null-count-deps-aliases]]
   [functional-example.infrastructure :as infra]))

(deftest count-aliases-test
  ;; This test case demonstates configuring the nullable with all possible options.
  ;; But notice in test cases below it is not necessary to pass full configuration thanks to the sensible defaults pattern.
  (testing "complete nullable configuration"
    (is (= 1 ((make-null-count-aliases
               {:cwd {:path "/another/directory"}
                :home-dir {:path "/home/otheruser"}
                :count-deps-aliases
                {:slurp {"/another/directory/deps.edn" "{:aliases {:a {}}}"}}})))))

  ;; Signature Shielding
  (testing "aliases are counted for deps in current directory"
    (is (= 1 ((make-null-count-aliases
               {:count-deps-aliases
                {:slurp {"/some/current/directory/deps.edn" "{:aliases {:a {}}}"}}})))))

  (testing "aliases are counted for deps in home directory"
    (is (= 2 ((make-null-count-aliases
               {:count-deps-aliases
                {:slurp {"/home/someuser/.clojure/deps.edn" "{:aliases {:a {} :b {}}}"}}})))))

  (testing "when both current and home directory are present, current directory takes precedence"
    (is (= 1 ((make-null-count-aliases
               {:count-deps-aliases
                {:slurp {"/some/current/directory/deps.edn" "{:aliases {:a {}}}"
                         "/home/someuser/.clojure/deps.edn" "{:aliases {:a {} :b {}}}"}}})))))

  ;; Parameterless Instantiation
  (testing "returns 42 when neither dep file is available"
    (is (= 42 ((make-null-count-aliases))))))

(deftest make-null-count-deps-aliases-test
  (testing "Parameterless Instantiation works, although not that useful"
    (is (= nil ((make-null-count-deps-aliases)
                "x"))))

  (testing "can be configured with pre-made dependency"
    (is (= 1 ((make-null-count-deps-aliases
               {:slurp (infra/make-null-slurp {"x" "{:aliases {:a {}}}"})})
              "x"))))

  (testing "experiment: parameters passed to nested nullable maker"
    (is (= 1 ((make-null-count-deps-aliases
               {:slurp {"x" "{:aliases {:a {}}}"}})
              "x")))))

;; https://www.jamesshore.com/v2/projects/nullables/testing-without-mocks#sociable-tests
;; > Write Narrow Tests that are focused on the behavior of the code under test, not the behavior of its dependencies.Each dependency should have its own thorough set of Narrow Tests.
(deftest count-deps-aliases-test
  (testing "non-existing file means no value"
    (is (= nil ((make-null-count-deps-aliases
                 {:slurp {}})
                "x"))))

  (testing "invalid edn means no value"
    (is (= nil ((make-null-count-deps-aliases
                 {:slurp {"x" "}"}})
                "x"))))

  (testing "empty file means zero aliases"
    (is (= 0 ((make-null-count-deps-aliases
               {:slurp {"x" ""}})
              "x"))))

  (testing "missing aliases key means zero aliases"
    (is (= 0 ((make-null-count-deps-aliases
               {:slurp {"x" "{}"}})
              "x"))))

  (testing "empty aliases map means zero aliases"
    (is (= 0 ((make-null-count-deps-aliases
               {:slurp {"x" "{:aliases {}}"}})
              "x"))))

  (testing "aliases are counted"
    (is (= 3 ((make-null-count-deps-aliases
               {:slurp {"x" "{:aliases {:a {} :b {} :c {}}}"}})
              "x")))))
