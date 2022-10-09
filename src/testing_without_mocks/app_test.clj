(ns testing-without-mocks.app-test
  (:require
   [clojure.test :refer [deftest is testing]]
   [testing-without-mocks.app :as app]
   [testing-without-mocks.command-line :as cli]))

(deftest rot-13-app
  (testing "encodes command-line argument and outputs the result"
    (let [cli (cli/create-null "my_cli_arg")]
      (app/run cli)
      (is (= "zl_pyv_net" (cli/get-last-output cli))))))
