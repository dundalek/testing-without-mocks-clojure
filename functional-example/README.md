
# Deps.edn alias counter program

It is kind of contrived example, but exercises platform APIs with infrastructure wrappers, for example getting environment variables, accessing system properties and reading files.

- The program counts and prints the number of declared deps `:aliases` in deps file.
- It tries to use the `deps.edn` file in the current working directory. When it does not exist or is not valid EDN it fallbacks to the user file in `~/.clojure/deps.edn`.
- If neither dep file is readable it defaults to printing 42.

Run the command line program:
```sh
$ clojure -M:run
```

Run tests with:
```sh
$ clojure -M:test
```
