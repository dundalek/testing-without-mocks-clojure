(ns functional-example.logic)

;; logic is pure
(defn count-aliases [deps]
  (-> deps :aliases count))
