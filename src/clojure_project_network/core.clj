(ns clojure-project-network.core
  (:require [clojure-project-network.pom2clj :as p2c]
            [fs.core :as fs]

            [clojure.java.io :as io]
            [clojure.xml :as xml]
            [clojure.zip :as zip]))

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

  (def data
    (->> infos
         (map edges)
         (reduce into #{})))

  ;; Dependency network
  (with-open [out (io/writer "dependencies.ncol")]
    (doseq [[from to] data]
      (doto out
        (.write (str from " " to))
        (.newLine))))

  ;; Projects often used together
  (with-open [out (io/writter "libs.ncol")]
    )

  ;; Projects using similar libraries
  (with-open [out (io/writer "apps.ncol")]
    )

  )
