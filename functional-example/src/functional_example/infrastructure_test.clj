(ns functional-example.infrastructure-test
  (:require
   [clojure.test :refer [deftest is testing]]
   [functional-example.infrastructure :as infra])
  (:import
   (java.io FileNotFoundException)))

;; Contract Tests to ensure faithfulnes of our infrastructure test doubles,
;; checking they mimic the behavior of the third-party code exactly.

;; In this example program we use resources provided by the local
;; Operating System which are reliable enough that we can have unit tests to
;; compare test doubles to the outputs directly.

;; In real scenarios when using services across network boundaries, we can:
;; 1) Encode the contract as specs or malli schemas.
;; 2) Have unit tests to check that test doubles produce outputs according to the contract.
;; 3) Set up instrumentation in production with Paranoic Telemetry to get
;; alerted in case our model of the contract no longer matches the reality.

(deftest slurp-test
  (doseq [[title wrapped-slurp] [["real" infra/slurp]
                                 ["null" (infra/make-null-slurp {"README.md" "abc"})]]]
    (testing title
      (testing "returns string when file exists"
        ;; using README.md file, in practice could have dedicated fixtures
        (is (string? (wrapped-slurp "README.md"))))

      (testing "throws when file does not exist"
        (try
          (wrapped-slurp "__NoN_eXiSTiNG_FiLe__")
          (is false "did not throw")
          (catch Exception e
            (is (= "__NoN_eXiSTiNG_FiLe__ (No such file or directory)" (.getMessage e)))
            (is (instance? FileNotFoundException e))))))))

(deftest get-system-property-test
  (testing "returns string for existing property"
    (let [property "user.dir"
          value (infra/get-system-property property)]
      (is (string? value))
      (is (= value ((infra/make-null-get-system-property {property value})
                    property)))))

  (testing "returns nil for non-existing property"
    (is (= nil (infra/get-system-property "__NoN_eXiSTiNG_PRoPeRTy__")))
    (is (= nil ((infra/make-null-get-system-property) "__NoN_eXiSTiNG_PRoPeRTy__")))))

(deftest getenv-test
  (testing "returns string for existing env var"
    (let [env-var "HOME"
          value (infra/getenv env-var)]
      (is (string? value))
      (is (= value ((infra/make-null-getenv {env-var value})
                    env-var)))))

  (testing "returns nil for non-existing env var"
    (is (= nil (infra/getenv "__NoN_eXiSTiNG_eNV__")))
    (is (= nil ((infra/make-null-getenv) "__NoN_eXiSTiNG_eNV__")))))

(deftest home-dir-test
  (testing "nullable uses embedded stub with a sensible default"
    (is (= "/home/someuser" ((infra/make-null-home-dir)))))

  (testing "nullable can be configured"
    (is (= "/home/otheruser" ((infra/make-null-home-dir {:path "/home/otheruser"}))))))

(deftest cwd-test
  (testing "nullable uses embedded stub with a sensible default"
    (is (= "/some/current/directory"  ((infra/make-null-cwd)))))

  (testing "nullable can be configured"
    (is (= "/another/directory" ((infra/make-null-cwd {:path "/another/directory"}))))))
