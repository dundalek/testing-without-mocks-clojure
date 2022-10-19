(ns testing-without-mocks.command-line)

(defprotocol ICommandLine
  (arg [this])
  (output [this data])
  (get-last-output [this]))

(deftype CommandLine [args log !last-output]
  ICommandLine
  (arg [_] (first args))
  (output [_ data]
    (log data)
    (reset! !last-output data))
  (get-last-output [_] @!last-output))

(defn create []
  (->CommandLine *command-line-args* println (atom nil)))

(defn create-null [& args]
  (->CommandLine args (fn [_]) (atom nil)))
