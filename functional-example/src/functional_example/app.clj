(ns functional-example.app
  (:require
   [clojure.edn :as edn]
   [functional-example.infrastructure :as infra]))

;; If we had only trivial example we would probably separate the reading
;; (impure) and calculation (pure) into separate functions.
;; However, as an experiment I wrote it as a single function to
;; demonstrate real-world challenges such as logic interleaved with database
;; queries or when the whole responsibility is encapsulated in a separate service.
(defn count-deps-aliases
  ([filename]
   (count-deps-aliases infra/slurp filename))
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
                (infra/make-null-slurp slurp))]
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
  (count-aliases-impl infra/cwd infra/home-dir count-deps-aliases))

(defn make-null-count-aliases [& {:keys [cwd home-dir count-deps-aliases]}]
  (partial count-aliases-impl
           (infra/make-null-cwd cwd)
           (infra/make-null-home-dir home-dir)
           (make-null-count-deps-aliases count-deps-aliases)))
