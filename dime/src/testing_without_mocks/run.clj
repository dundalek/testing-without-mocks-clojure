(ns testing-without-mocks.run
  (:require
   [dime.core :as di]
   [dime.var :as dv]))

(defn -main [& _]
  ;; Ignoring args to demonstrate a principle.
  (let [graph (dv/ns-vars->graph ['testing-without-mocks.app
                                  'testing-without-mocks.command-line])
        seed {}
        {:keys [run]} (di/inject-all graph seed)]
    (run)))
