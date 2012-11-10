(ns clojure-project-network.core
  (:require [clojure-project-network.pom2clj :as p2c]
            [fs.core :as fs]

            [clojure.java.io :as io]
            [clojure.xml :as xml]
            [clojure.zip :as zip]
            [clojure.math.combinatorics :as cb]
            ))

;;; Data was collected by getting a copy of clojars.org repository. An
;;; alternative is requesting the pom files from clojars.org, as
;;; described at https://github.com/ato/clojars-web/wiki/Data
;;; rsync -av clojars.org::clojars clojars-copy

(def ^:dynamic *clojar-dir*
  "/Volumes/Lion Data/ubolonton/Programming/clojars-copy")

(defn pom-files [dir]
  (fs/find-files dir #"^.*\.pom$"))

(defn info [f]
  (try
    (let [z (-> f xml/parse zip/xml-zip)]
     (merge (p2c/attr-map z [:groupId :artifactId])
            {:dependencies (p2c/dependencies z)}))
    (catch Exception e
      (println (.getPath f) e)
      {})))

(defn lib-name [info]
  (:artifactId info)
  ;; (str (:groupId info) "/" (:artifactId info))
  )

(defn edges [info]
  (let [from (lib-name info)]
    (map (fn [i] [from (lib-name i)]) (:dependencies info))))

(comment
  (def infos
    (map info (pom-files *clojar-dir*)))

  ;; Set of [from to] tuples
  (def dependencies
    (->> infos
         (map edges)
         (reduce into #{})))

  (def apps
    )

  ;; Map {#{lib1 lib2} count}
  (def libs
    (->> infos
         (map (fn [info]
                (map set
                     (cb/combinations
                      (map :artifactId (:dependencies info)) 2))))
         (map (fn [pairs]
                (zipmap pairs (repeat 1))))
         (reduce (fn [dict dict-add]
                   (merge-with + dict dict-add)) {}))
    ;; (let [pair-list (map (fn [info]
    ;;                        (map set
    ;;                             (cb/combinations
    ;;                              (map :artifactId (:dependencies info)) 2)))
    ;;                      infos)]
    ;;   (reduce (fn [dict dict-add]
    ;;             (merge-with + dict dict-add)) {}
    ;;             (zipmap pairs (repeat 1))))
    )

  ;; Dependency network
  (with-open [out (io/writer "data/dependencies.ncol")]
    (doseq [[from to] dependencies]
      (doto out
        (.write (str from " " to))
        (.newLine))))

  ;; Projects often used together
  (with-open [out (io/writer "data/libs.ncol")]
    (doseq [[libs count] libs]
      (doto out
        (.write (str (first libs) " " (second libs) " " count))
        (.newLine))))

  ;; Projects using similar libraries
  (with-open [out (io/writer "data/apps.ncol")]
    )

  )
