(ns testing-without-mocks.run
  (:require [testing-without-mocks.app :as app]))

(defn -main [& _]
  ;; Ignoring args to demonstrate a principle.
  (app/run))
