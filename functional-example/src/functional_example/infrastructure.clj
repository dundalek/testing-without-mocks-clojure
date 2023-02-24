(ns functional-example.infrastructure
  (:refer-clojure :exclude [slurp])
  (:import
   (java.io FileNotFoundException)))

;; Infrastructure wrappers

;; in case of Less stable 3rd party - can adapt
(def slurp clojure.core/slurp)

(defn make-null-slurp [& {:as file-map}]
  (fn [filename]
    (if (contains? file-map filename)
      (get file-map filename)
      (throw (FileNotFoundException.
               (str filename " (No such file or directory)"))))))

;; Thin Wrapper pattern
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

(defn cwd
  ([]
   (cwd get-system-property))
  ([get-system-property]
   (get-system-property "user.dir")))

(defn make-null-cwd [& {:keys [path]}]
  (let [path (or path "/some/current/directory")]
    (partial cwd (make-null-get-system-property
                  {"user.dir" path}))))


