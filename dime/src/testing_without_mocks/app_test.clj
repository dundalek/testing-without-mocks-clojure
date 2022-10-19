(ns testing-without-mocks.app-test
  (:require
   [clojure.test :refer [deftest is testing]]
   [dime.core :as di]
   [dime.var :as dv]
   [testing-without-mocks.app :as app]
   [testing-without-mocks.command-line :as cli]))

(deftest rot-13-app
  (testing "encodes command-line argument and outputs the result"
    (let [cli (cli/create-null "my_cli_arg")]
      (app/run cli)
      (is (= "zl_pyv_net" (cli/get-last-output cli)))))

  (testing "encodes command-line argument and outputs the result with di"
    (let [graph (dv/ns-vars->graph ['testing-without-mocks.app
                                    'testing-without-mocks.command-line])
          cli (cli/create-null "my_cli_arg")
          seed {:cli cli}
          {:keys [run]} (di/inject-all graph seed)]
      (run)
      (is (= "zl_pyv_net" (cli/get-last-output cli))))))
