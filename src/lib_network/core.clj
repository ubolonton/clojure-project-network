(ns lib-network.core
  (:require [lib-network.pom2clj :as p2c]
            [fs.core :as fs]

            [clojure.java.io :as io]
            [clojure.xml :as xml]
            [clojure.zip :as zip]))

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
  (str (:groupId info) "/" (:artifactId info)))

(defn edges [info]
  (let [from (lib-name info)]
    (map (fn [i] [from (lib-name i)]) (:dependencies info))))

(comment
  (def data
    (->> (map info (pom-files *clojar-dir*))
         (map edges)
         (reduce into #{})))

  (with-open [out (io/writer "libs.ncol")]
    (doseq [[from to] data]
      (doto out
        (.write (str from " " to))
        (.newLine))))

  )
