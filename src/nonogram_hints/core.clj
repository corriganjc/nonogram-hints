(ns nonogram-hints.core
  (:require [clojure.data.json :as json]
            [clojure.string :as string]
            [clojure.tools.cli :refer [parse-opts]]
            [nonogram-hints.image :as img])
  (:gen-class))

(defn find-runs
  "Calculate the runs of ground and not ground valuesi, i.e., 0, in a
   sequence. The first number in the returned sequence is the first
   run of ground values. This can be zero. An empty array will return
   [0 0]."
  [arr]
  (loop [a arr
         cnt 0 
         curr-seq-value 0
         acc []]
    (if (empty? a)
        ; Pad the array so there is at least one entry for figure and
        ; ground counts.
        (if (= 0 (count acc))
            [cnt 0]
            (conj acc cnt))
        (if (not= curr-seq-value (first a))
            (recur (rest a) 1 (first a) (conj acc cnt))
            (recur (rest a) (+ 1 cnt) curr-seq-value acc)))))

(defn find-figure-runs
  "Calculate runs of the 1s in a sequence."
  [arr]
  (->> (find-runs arr)
       (drop 1)
       (take-nth 2)))

(defn find-ground-runs
  "Calculate runs of the 0s in a sequence."
  [arr]
  (->> (find-runs arr)
       (take-nth 2)))

(defn transpose
  "Convert a list of lists representing rows in a matrix into a
   list of lists representing the columns in the same matrix."
  [rows]
  (apply map vector rows))

(defn calc-nonogram-hints [find-runs-fn rows]
  {:x (map find-runs-fn rows)
   :y (->> rows
           (transpose)
           (map find-runs-fn))})

(defn load-image-json
  "This loads in the image from a file. It is expected that the
   data will be in a list of lists, that each value will be 1 or 0,
   and that each list of values will be the same length."
  [file-name]
  (-> (slurp file-name)
      (json/read-str)))

(defn load-image-data
  "Get the array of 0s and 1s representing the image. The method depends 
  on the type."
  [file-name file-type]
  (if (= file-type "JSON")
      (load-image-json file-name)
      (img/get-pixels file-name)))

;; The CLI entry point and supporting CLI arg parsing code. 
(def cli-opts-spec [["-g" "--ground" "Calculate runs based on background pixels."]
                    ["-t" "--type FILETYPE" "The type of the input file, either JSON or PNG."
                     :default "JSON"
                     :validate [#(get #{"JSON" "PNG"} % nil) "type must be either \"JSON\" or \"PNG\"."]]])

(defn render-error-msg
  [errors opts-summary]
  (string/join \newline ["Errors:" ""
                         (string/join \newline errors)
                         ""
                         "Available options:" "" opts-summary]))

(defn validate-args [args]
  (let [opts (parse-opts args cli-opts-spec)]
    (if (not (:errors opts))
        (assoc (:options opts) :filename (first (:arguments opts)))
        (throw (Exception. (render-error-msg (:errors opts) (:summary opts)))))))

(defn -main [& args]
  (try
    (let [opts (validate-args args)
          rows (load-image-data (:filename opts) (:type opts))
          find-runs-fn (if (:ground opts) find-ground-runs find-figure-runs)]
      (println (json/write-str (calc-nonogram-hints find-runs-fn rows))))
    (catch Exception e 
      (println (.getMessage e)))))
