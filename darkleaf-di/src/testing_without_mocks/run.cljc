(ns testing-without-mocks.run
  (:require [testing-without-mocks.app :as app]
            [darkleaf.di.core :as di]))

(defn -main [& _]
  ;; Ignoring args to demonstrate a principle.
  (di/start `app/run))
