(ns functional-example.main
  (:require [functional-example.app :as app]))

(defn -main []
  ;; Note 1: Notice that we don't pass any dependencies. Even though we inject
  ;; dependencies manually, it is encapsulated underneath invisibly to callers.
  ;; Note 2: I got lazy to wrap `println`, leaving it as a future exercise to
  ;; wrap it too for truly sociable tests :)
  (println (app/count-system-aliases)))
