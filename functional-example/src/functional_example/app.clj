(ns functional-example.app
  (:require
   [clojure.edn :as edn]
   [functional-example.infrastructure :as infra]
   [functional-example.logic :as logic]))

;; A-Frame Architecture: app layer coordinates the logic and infrastructure
;; layers, which do not have dependencies between each other.

;; Logic Sandwich pattern: Application layer reads inputs using infrastructure
;; layer and processes them using pure logic layer.
(defn count-deps-aliases
  ([filename]
   (count-deps-aliases infra/slurp filename))
  ([slurp filename]
   (try
     ;; reading whole file into memory, wrapping IO readers left as a future exercise
     (-> (slurp filename)
         (edn/read-string)
         (logic/count-aliases))
     (catch Exception _
       nil))))

(defn make-null-count-deps-aliases [& {:keys [slurp]}]
  ;; Experiment: support passing a pre-made function for extra flexibility that
  ;; gets used as is.
  ;; Otherwise we pass a given parameter to our nullable constructor.
  (let [slurp (if (fn? slurp)
                slurp
                (infra/make-null-slurp slurp))]
    (partial count-deps-aliases slurp)))

;; Note that we could apply Logic Sandwich pattern by evaluating all inputs
;; before passing them to the function.
;; But imagine that these calls could go across services in a real app, in which
;; case short-circuiting is desirable to avoid making unnecessary requests.
(defn- count-system-aliases-impl [cwd home-dir count-deps-aliases]
  (or
   (count-deps-aliases (str (cwd) "/deps.edn"))
   (count-deps-aliases (str (home-dir) "/.clojure/deps.edn"))
   42))

;; Experimenting with injecting using a separate -impl helper.
;; Compared to partial or multiple-arity approaches it adds extra var as
;; boilerplate, but the advantage is cleaner public API without dependencies
;; leaking in the signature.
(defn count-system-aliases []
  ;; Note: in practice we would probably pass these down as map.
  ;; But it is less code to pass positionally and given it is hidden from
  ;; public interface it seems fine.
  (count-system-aliases-impl infra/cwd infra/home-dir count-deps-aliases))

(defn make-null-count-system-aliases [& {:keys [cwd home-dir count-deps-aliases]}]
  (partial count-system-aliases-impl
           (infra/make-null-cwd cwd)
           (infra/make-null-home-dir home-dir)
           (make-null-count-deps-aliases count-deps-aliases)))
