(ns testing-without-mocks.run
  (:require [init.core :as init]
            [init.discovery :as discovery]
            [testing-without-mocks.app]))

(defn -main [& _]
  ;; Ignoring args to demonstrate a principle.
  ;; How do we know what is started? It is just root of the toposort?
  (-> (discovery/static-scan '[testing-without-mocks])
      (init/start)
      (init/stop-on-shutdown)))
