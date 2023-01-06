(ns testing-without-mocks.app-test
  (:require
   [clojure.test :refer [deftest is testing]]
   [init.core :as init]
   [init.discovery :as discovery]
   [testing-without-mocks.app :as app]
   [testing-without-mocks.command-line :as cli]))

(deftest rot-13-app
  (testing "encodes command-line argument and outputs the result"
    (let [cli (cli/create-null "my_cli_arg")]
      (app/run cli)
      (is (= "zl_pyv_net" (cli/get-last-output cli)))))

  (testing "encodes command-line argument and outputs the result with di"
    (let [cli (cli/create-null "my_cli_arg")
          config (-> (discovery/static-scan '[testing-without-mocks])
                     (assoc ::cli/command-line {:name ::cli/command-line
                                                :start-fn (constantly cli)}))]
      (init/start config)
      (is (= "zl_pyv_net" (cli/get-last-output cli))))))
