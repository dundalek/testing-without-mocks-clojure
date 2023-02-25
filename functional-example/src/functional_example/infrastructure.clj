(ns functional-example.infrastructure
  (:refer-clojure :exclude [slurp])
  (:import
   (java.io FileNotFoundException)))

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

;; ## Higher-level infrastructure

(defn home-dir
  ([]
   (home-dir getenv))
  ([getenv]
   (getenv "HOME")))

;; Parameterless Instantiation pattern helps to reduce boilerplate in test code.
;; Enabled by Embedded Stub pattern with sensible defaults and kwargs.
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
