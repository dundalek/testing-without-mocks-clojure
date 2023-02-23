# Testing Without Mocks Example in Clojure

This is an example of James Shore's [Testing Without Mocks](https://www.jamesshore.com/v2/projects/nullables/testing-without-mocks)  approach in Clojure.

## Original example

Re-implementation of the original [example](https://github.com/jamesshore/testing-without-mocks-example).

Manual dependency injection:
- [testing-without-mocks](testing-without-mocks/src/testing_without_mocks) (clj+cljs)

Also experimented with some Clojure DI frameworks:
- [darkleaf-di](darkleaf-di/src/testing_without_mocks) (clj) using [darkleaf/di](https://github.com/darkleaf/di)
- [dime](dime/src/testing_without_mocks) (clj) using [dime](https://github.com/kumarshantanu/dime)
- [fbeyer-init](fbeyer-init/src/testing_without_mocks) (clj) using [fbeyer/init](https://github.com/ferdinand-beyer/init)

Run the command line program (in a given subfolder):
```sh
$ cd testing-without-mocks
$ clojure -M:run "Hello World"
Uryyb Jbeyq
```

Run tests with:
```sh
$ clojure -M:test
```

#### ClojureScript version

Run the command line program:
```sh
$ clojure -Mshadow compile main && node out/main.js "Hello World"
Uryyb Jbeyq
```

Run tests with:
```sh
$ clojure -Mshadow compile test && node out/node-tests.js
```
