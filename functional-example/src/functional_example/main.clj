(ns functional-example.main
  (:require [functional-example.app :as app]))

(defn -main []
  ;; I was lazy to wrap `println`, but for truly sociable tests we probably should.

  ;; "business functions" encapsulated
  (println (app/count-aliases)))
