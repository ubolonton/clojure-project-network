# clojure-project-network

Collection and analysis of Clojure projects network

## Collection

Data was collected from clojars.org

## Analysis

### Dependencies
clojure, clojure-contrib, lein-clojars, swank-clojure are excluded

Betweenness Centrality: nodes with high centrality are expected to be
middle-level infrastructural libraries that abstract many lower level
libraries, and are used by many other libraries/projects (in other
words, generic abstractions)
- Leiningen and related projects: build tool
- Doc tools: autodoc, marginalia
- REPL tools: reply, drawbridge
- Web frameworks: ring, compojure, noir, clj-http...

Closeness centrality does not make much sense for this network.

Nodes with high page ranks or eigenvector centralities are important
libries, but they are not necessarily the ones that should be used
directly, since they may be low level libraries that form the basis
for other higher level, more convenient libraries: commons-io, junit,
java.classpath, commons-codec.

The most important factor to look at is thus betweenness centrality.

### TODO Used-together libraries


### TODO Using-the-same-libs projects


## License

Distributed under the Eclipse Public License, the same as Clojure.
