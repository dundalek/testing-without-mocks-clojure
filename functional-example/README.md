
# Deps.edn alias counter

This is kind of a contrived example program, but exercises function-based platform APIs with infrastructure wrappers like getting environment variables, accessing system properties and reading files.

- The program counts and prints the number of declared `:aliases` in deps file.
- It tries to use the `deps.edn` file in the current working directory. When it does not exist or is not valid EDN it fallbacks to the user file in `~/.clojure/deps.edn`.
- If neither dep file is readable it defaults to printing 42.

Run the command line program:
```sh
$ cd functional-example
$ clojure -M:run
```

To exercise the path of counting the aliases in user deps run in the root of the repo:  
(We could perhaps add a CLI argument switch?)
```sh
$ cd ..
$ clj -Sdeps '{:paths ["functional-example/src"]}' -M -m functional-example.main
```

Run tests with:
```sh
$ clojure -M:test
```

Run tests in watch mode:
```sh
$ clojure -M:test --watch
```

Generate code coverage report:
```sh
$ clojure -M:test:coverage
```
