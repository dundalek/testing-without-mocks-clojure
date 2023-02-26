(ns functional-example.app-test
  (:require
   [clojure.test :refer [deftest is testing]]
   [functional-example.app :as app :refer [make-null-count-system-aliases
                                           make-null-count-deps-aliases]]
   [functional-example.infrastructure :as infra]))

;; Overlapping Sociable Tests pattern, James Shore writes:
;; > Write Narrow Tests that are focused on the behavior of the code under test,
;; > not the behavior of its dependencies.
;; > Each dependency should have its own thorough set of Narrow Tests.

;; Therefore we test only high-level behavior with limited set of inputs in
;; `count-system-aliases-test`. We test additional input variations
;; in `count-deps-aliases-test` and `logic-test/count-aliases-test` respectively.
(deftest count-system-aliases-test

  ;; This test case demonstates configuring the Nullable with all possible
  ;; options for full control. But we strive to minimize test boilerplate,
  ;; notice the techniques in test cases bellow that make it unneccessary to
  ;; pass all of the possible configuration parameters.
  (testing "complete nullable configuration"
    (is (= 1 ((make-null-count-system-aliases
               {:cwd {:path "/another/directory"}
                :home-dir {:path "/home/otheruser"}
                :count-deps-aliases
                {:slurp {"/another/directory/deps.edn" "{:aliases {:a {}}}"}}})))))

  ;; Parameterless Instantiation pattern:
  ;; Avoiding difficulty of setting up multi-level dependency chains by
  ;; supporting constructors to be called without paramaters.
  (testing "returns 42 when neither dep file is available"
    (is (= 42 ((make-null-count-system-aliases)))))

  ;; Signature Shielding pattern:
  ;; Configuring nullables with less parameters saves code and reduces amount
  ;; of refactoring as a response to changes in the system.
  (testing "aliases are counted for deps in current directory"
    (is (= 1 ((make-null-count-system-aliases
               {:count-deps-aliases
                {:slurp {"/some/current/directory/deps.edn" "{:aliases {:a {}}}"}}})))))

  (testing "aliases are counted for deps in home directory"
    (is (= 2 ((make-null-count-system-aliases
               {:count-deps-aliases
                {:slurp {"/home/someuser/.clojure/deps.edn" "{:aliases {:a {} :b {}}}"}}})))))

  (testing "when both current and home directory are present, current directory takes precedence"
    (is (= 1 ((make-null-count-system-aliases
               {:count-deps-aliases
                {:slurp {"/some/current/directory/deps.edn" "{:aliases {:a {}}}"
                         "/home/someuser/.clojure/deps.edn" "{:aliases {:a {} :b {}}}"}}}))))))

;; Narrow Tests pattern:
;; Testing additional input variations on lower level.
(deftest count-deps-aliases-test
  (testing "Parameterless Instantiation works, although not that useful in this case"
    (is (= nil ((make-null-count-deps-aliases)
                "x"))))

  (testing "experiment: can be configured with a pre-made dependency"
    (is (= 1 ((make-null-count-deps-aliases
               {:slurp (infra/make-null-slurp {"x" "{:aliases {:a {}}}"})})
              "x"))))

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

  (testing "aliases are counted"
    (is (= 1 ((make-null-count-deps-aliases
               {:slurp {"x" "{:aliases {:a {}}}"}})
              "x")))))

(deftest run-test
  (let [{:keys [run get-output]} (app/make-null-run)]
    (run)
    (is (= "42\n" (get-output))))

  (let [{:keys [run get-output]} (app/make-null-run
                                  {:count-system-aliases
                                   {:count-deps-aliases
                                    {:slurp {"/some/current/directory/deps.edn" "{:aliases {:a {}}}"}}}})]
    (run)
    (is (= "1\n" (get-output)))))
