(ns testing-without-mocks.command-line-test
  (:require
   [clojure.test :refer [deftest is testing]]
   [testing-without-mocks.command-line :as cli]))

(deftest command-line
  (testing "provides first command-line argument"
    #?(:clj (binding [*command-line-args* ["first_argument"]]
             (let [cli (cli/create)]
               (is (= "first_argument" (cli/arg cli)))))

       :cljs (let [saved-args js/process.argv]
               (set! js/process.argv #js ["ignore 1" "ignore 2" "first_argument"])
               (try
                 (let [cli (cli/create)]
                   (is (= "first_argument" (cli/arg cli))))
                 (finally
                   (set! js/process.argv saved-args))))))

  (testing "writes to console"
    (is (= "my output\n"
           (with-out-str
             (let [cli (cli/create)]
               (cli/output cli "my output"))))))

  (testing "provides last line written to console"
    (with-out-str
      (let [cli (cli/create)]
        (cli/output cli "my output")
        (is (= "my output" (cli/get-last-output cli))))))

  (testing "arg is nullable"
    (let [cli (cli/create-null "my_arg")]
      (is (= "my_arg" (cli/arg cli)))))

  (testing "console is nullable"
    (is (= ""
           (with-out-str
             (let [cli (cli/create-null)]
               (cli/output cli "my output"))))
        "should not actually output")))
