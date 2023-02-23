(ns functional-example.app
  (:require
   [clojure.edn :as edn]
   [clojure.test :refer [deftest is testing]]))

;; Infrastructure wrappers

(defn make-null-slurp [file-map]
  (fn [filename]
    (if (contains? file-map filename)
      (get file-map filename)
      ;; maybe throw FileNotFoundException properly
      (Exception. "No such file or directory"))))

(defn get-system-property [prop]
  (System/getProperty prop))

(defn make-null-get-system-property [prop-map]
  (fn [prop]
    (get prop-map prop)))

(defn getenv [name]
  (System/getenv name))

(defn make-null-getenv [env-map]
  (fn [name]
    (get env-map name)))

;; Somewhat higher-level infrastructure

(defn home-dir
  ([]
   (home-dir getenv))
  ([getenv]
   (getenv "HOME")))

;; Embedded Stub with a sensible default
;; https://www.jamesshore.com/v2/projects/nullables/testing-without-mocks#embedded-stub
(defn make-null-home-dir [& {:keys [path]}]
  (let [path (or path "/home/someuser")]
    (partial home-dir (make-null-getenv {"HOME" path}))))

(deftest make-null-home-dir-test
  (testing "nullable uses embedded stub with a sensible default"
    (is (= "/home/someuser" ((make-null-home-dir)))))

  (testing "nullable can be configured"
    (is (= "/home/otheruser" ((make-null-home-dir {:path "/home/otheruser"}))))))

(defn cwd
  ([]
   (cwd get-system-property))
  ([get-system-property]
   (get-system-property "user.dir")))

(defn make-null-cwd [& {:keys [path]}]
  (let [path (or path "/some/current/directory")]
    (partial cwd (make-null-get-system-property
                  {"user.dir" path}))))

(deftest make-null-cwd-test
  (testing "nullable uses embedded stub with a sensible default"
    (is (= "/some/current/directory"  ((make-null-cwd)))))

  (testing "nullable can be configured"
    (is (= "/another/directory" ((make-null-cwd {:path "/another/directory"}))))))

;; Logic

;; If we had only trivial example we would probably separate the reading
;; (impure) and calculation (pure) into separate functions.
;; However, as an experiment I wrote it as a single function to
;; demonstrate real-world challenges such as logic interleaved with database
;; queries or when the whole responsibility is encapsulated in a separate service.
(defn count-deps-aliases
  ([filename]
   (count-deps-aliases slurp filename))
  ([slurp filename]
   (try
     (-> (slurp filename)
         (edn/read-string)
         :aliases
         count)
     (catch Exception _
       nil))))

(defn make-null-count-deps-aliases [& {:keys [slurp]}]
  (let [slurp (if (fn? slurp)
                slurp
                (make-null-slurp slurp))]
    (partial count-deps-aliases slurp)))

;; Note that we could make this logic pure by evaluating all inputs before
;; passing them to the function.
;; But imagine that these calls could go across services in a real app, in which
;; case short-circuiting is desirable to avoid making unnecessary requests.
(defn- count-aliases-impl [cwd home-dir count-deps-aliases]
  (or
   (count-deps-aliases (str (cwd) "/deps.edn"))
   (count-deps-aliases (str (home-dir) "/.clojure/deps.edn"))
   42))

;; Experimenting with injecting using a separate -impl helper.
;; Compared to partial or multiple-arity approaches it adds extra var as
;; boilerplate, but the advantage is cleaner public API without dependencies
;; leaking in the signature.
(defn count-aliases []
  ;; Note: in practice we would probably pass these down as map.
  ;; But it is less code to pass positionally and given it is hidden from
  ;; public interface it seems fine as is.
  (count-aliases-impl cwd home-dir count-deps-aliases))

(defn make-null-count-aliases [& {:keys [cwd home-dir count-deps-aliases]}]
  (partial count-aliases-impl
           (make-null-cwd cwd)
           (make-null-home-dir home-dir)
           (make-null-count-deps-aliases count-deps-aliases)))

(defn -main []
  ;; I was lazy to wrap `println`, but for truly sociable tests we probably should.

  ;; "business functions" encapsulated
  (println (count-aliases)))

(deftest count-aliases-test
  (testing "complete nullable configuration"
    (is (= 1 ((make-null-count-aliases
               {:cwd {:path "/another/directory"}
                :home-dir {:path "/home/otheruser"}
                :count-deps-aliases
                 {:slurp {"/another/directory/deps.edn" "{:aliases {:a {}}}"}}})))))

  (testing "aliases are counted for deps in current directory"
    (is (= 1 ((make-null-count-aliases
               {:count-deps-aliases
                 {:slurp {"/some/current/directory/deps.edn" "{:aliases {:a {}}}"}}})))))

  ;; add spy to ensure it short circuits
  (testing "aliases are counted for deps in home directory"
    (is (= 2 ((make-null-count-aliases
               {:count-deps-aliases
                {:slurp {"/home/someuser/.clojure/deps.edn" "{:aliases {:a {} :b {}}}"}}})))))

  (testing "when both current and home directory are present, current directory takes precedence"
    (is (= 1 ((make-null-count-aliases
               {:count-deps-aliases
                 {:slurp {"/some/current/directory/deps.edn" "{:aliases {:a {}}}"
                          "/home/someuser/.clojure/deps.edn" "{:aliases {:a {} :b {}}}"}}})))))

  (testing "returns 42 when neither dep file is available"
    (is (= 42 ((make-null-count-aliases))))))

(deftest make-null-count-deps-aliases-test
  (testing "Parameterless Instantiation works, although not that useful"
    (is (= nil ((make-null-count-deps-aliases)
                "x"))))

  (testing "can be configured with pre-made dependency"
    (is (= 1 ((make-null-count-deps-aliases
               {:slurp (make-null-slurp {"x" "{:aliases {:a {}}}"})})
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
