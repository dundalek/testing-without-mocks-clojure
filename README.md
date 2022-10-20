# Testing Without Mocks Example in Clojure

This is a reimplementation of James Shore's Testing Without Mocks ([code](https://github.com/jamesshore/testing-without-mocks-example), [article](http://www.jamesshore.com/Blog/Testing-Without-Mocks.html)) in Clojure.

Run the command line program:
```sh
$ clojure -M:run "Hello World"
Uryyb Jbeyq
```

Run tests with:
```sh
$ clojure -M:test
```

### ClojureScript version

Run the command line program:
```sh
$ clojure -Mshadow compile main && node out/main.js "Hello World"
Uryyb Jbeyq
```

Run tests with:
```sh
$ clojure -Mshadow compile test && node out/node-tests.js
