(ns testing-without-mocks.app
  (:require [testing-without-mocks.command-line :as cli]))

;; Following function courtesy of https://rosettacode.org/wiki/Rot-13#Clojure
;; Distributed under https://creativecommons.org/licenses/by-sa/4.0/
(let [A (into #{} "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ")
      Am (->> (cycle A) (drop 26) (take 52) (zipmap A))]
  (defn rot13 [^String in]
    (->> (replace Am in)
         (apply str))))

;; Here we just use a function, which deviates from the OOP way of the original.
;; However, it still demonstrates the principle of Infrastructure Wrappers.
(defn run
  ([]
   (run (cli/create)))
  ([cli]
   (->> (cli/arg cli)
        (rot13)
        (cli/output cli))))
