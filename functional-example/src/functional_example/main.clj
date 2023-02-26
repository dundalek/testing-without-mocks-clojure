(ns functional-example.main
  (:require [functional-example.app :as app]))

(defn -main []
  ;; Note: Notice that we don't pass any dependencies. Even though we inject
  ;; dependencies manually, it is encapsulated underneath invisibly to callers.
  (app/run))
