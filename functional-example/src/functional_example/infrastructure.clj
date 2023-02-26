(ns functional-example.infrastructure
  (:refer-clojure :exclude [println slurp])
  (:require
   [clojure.java.io :as io]
   [clojure.string :as str])
  (:import
   (java.io File FileNotFoundException)
   (java.nio.file Path)))

;; ## Infrastructure Wrappers

;; It is not that userful to wrap Clojure API, but wrapping it for completeness.
;; In practice we are likely to work with less stable 3rd party libraries/services.
;; We will have a place in the wrapper to shield ourselves from some potential
;; cases of breakage with adapter code.
(def slurp clojure.core/slurp)

(defn make-null-slurp [& {:as file-map}]
  (fn [filename]
    (if (contains? file-map filename)
      (get file-map filename)
      (throw (FileNotFoundException.
              (str filename " (No such file or directory)"))))))

;; Thin Wrapper pattern: We don't cover the whole surface of what Java provides,
;; but only the functionality we use.
(defn get-system-property [prop]
  (System/getProperty prop))

(defn make-null-get-system-property [& {:as prop-map}]
  (fn [prop]
    (get prop-map prop)))

(defn getenv [name]
  (System/getenv name))

(defn make-null-getenv [& {:as env-map}]
  (fn [name]
    (get env-map name)))

(def println clojure.core/println)

(defn make-null-println []
  (let [!lines (atom [])]
    ;; Experiment of just using ad-hoc map of functions without the need to
    ;; introduce additional protocols for test implementations.
    {:println (fn [line]
                ;; In practice we would need to support also zero or multiple arguments.
                (swap! !lines conj line)
                (swap! !lines conj "\n"))
     :get-output (fn []
                   (str/join "" @!lines))}))

;; ## Higher-level infrastructure

(defn home-dir
  ([]
   (home-dir getenv))
  ([getenv]
   (getenv "HOME")))

;; Parameterless Instantiation pattern enabled by sensible defaults and kwargs
;; helps to reduce boilerplate in test code.
(defn make-null-home-dir [& {:keys [path]}]
  (let [path (or path "/home/someuser")]
    (partial home-dir (make-null-getenv {"HOME" path}))))

(defn cwd
  ([]
   (cwd get-system-property))
  ([get-system-property]
   (get-system-property "user.dir")))

(defn make-null-cwd [& {:keys [path]}]
  (let [path (or path "/some/current/directory")]
    (partial cwd (make-null-get-system-property
                  {"user.dir" path}))))

;; ## Embedded Stubs example below

;; This example can be adapted to create infrastructure wrappers for 3rd party
;; libraries, SDKs, HTTP clients, etc.

;; We can make stubbed instances that implement interfaces with `reify`.
(defn- make-stubbed-path [pathname]
  (reify Path
    (^Path resolve [_this ^String other]
      (make-stubbed-path (str pathname "/" other)))
    (toString [_this]
      pathname)))

;; Make stubbed class instances with `proxy`. Note that `proxy` still calls the
;; base class constructor. In this example we rely on the fact that File
;; complies with Zero-Impact Instantiation and doesn't perform visible side
;; effects in its constructor.
;; When this is not possible we will need to create a custom protocol with two
;; implementations (real and nulled), but should keep it as small as possible
;; according to the Thin Wrapper pattern.
(defn- make-stubbed-file [pathname]
  (proxy [File] [""]
    (toPath []
      (make-stubbed-path pathname))))

;; Following is a bit involved way to join file paths. The implementation uses
;; Java classes directly to demonstrate usage of the Embedded Stub pattern.
(defn- join-paths-impl [make-file base & others]
  (let [base-path (-> (make-file base)
                      (.toPath))]
    (-> (reduce (fn [path other]
                  (.resolve path other))
                base-path
                others)
        str)))

(defn join-paths [base & others]
  (apply join-paths-impl io/file base others))

(defn make-null-join-paths []
  (partial join-paths-impl make-stubbed-file))
