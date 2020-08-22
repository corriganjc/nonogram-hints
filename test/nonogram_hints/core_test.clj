(ns nonogram-hints.core-test
  (:require [clojure.test :refer :all]
            [nonogram-hints.core :refer :all]
            [clojure.java.io :as io]))

(deftest test-load-image-data
  (testing "Loading images:"
    (testing "image data in JSON format"
      (let [img-resource (io/resource "nonogram.json")
            data (load-image-data (io/input-stream img-resource) "JSON")]
        (is (and (= 23 (count data))
                 (= 75 (count (first data)))))))
    (testing "image data in PNG format"
      (let [img-resource (io/resource "hw.png")
            data (load-image-data (io/input-stream img-resource) "PNG")]
        (is (and (= 22 (count data))
                 (= 32 (count (first data)))))))))

(deftest test-transpose
  (testing "Transpose a 2-d matrix"
    (is (= (transpose [[:a :b :c] [1 2 3]])
           [[:a 1] [:b 2] [:c 3]]))))

(deftest test-calc-nonogram-hints
  (testing "Calculate nonogram hints:"
    (testing "figure runs from PNG data"
      (let [image-data (-> "hw.png" 
                           (io/resource) 
                           (io/input-stream) 
                           (load-image-data "PNG"))
            expected {:x [[0] [0] [8 2 2] [2 2 1 1] [2 2 2 1 1 3]
                          [6 1 1 1 1 2 2] [2 2 4 1 1 2 2] [2 2 2 1 1 2 2]
                          [8 9 3] [0] [0] [0] [0] [2 2 1] [1 2 1]
                          [7 3 4 1 4 1] [1 2 2 2 1 1 2 2 1] [1 2 2 2 1 1 2 2]
                          [2 1 2 2 1 1 2 2] [1 3 3 3 4 1] [0] [0]]
                      :y [[0] [0] [1 1 1] [7 4] [7 1 2] [1 1 1 3] [1 1 1 4]
                          [7 1] [7 1] [1 1 3] [2 5] [5 1 1] [1 1 1 5] [2 1 3]
                          [1 1 1 1] [7 5] [1 1 1] [1 1 1] [7 1 1] [1 7] [3 1]
                          [5 3] [1 1 5] [5 1 1] [3 7] [7] [0] [0] [4 1] [0]
                          [0] [0]]}]
        (is (= expected (calc-nonogram-hints find-figure-runs image-data)))))
    (testing "ground runs from PNG data"
      (let [image-data (-> "hw.png"
                           (io/resource)
                           (io/input-stream)
                           (load-image-data "PNG"))
            expected {:x [[32] [32] [2 4 1 13] [3 2 6 2 13] [3 2 2 2 2 2 8]
                          [3 2 1 1 2 1 1 7] [3 2 1 1 2 1 1 7] [3 2 1 3 2 1 1 7]
                          [2 1 1 8] [32] [32] [32] [32] [18 4 2 3] [19 4 2 3]
                          [2 1 1 1 2 2 3] [3 1 2 1 1 3 1 1 2 3] [3 1 2 1 1 3 1 1 6]
                          [3 1 2 1 1 3 1 1 6] [4 5 1 1 1 2 3] [32] [32]]
                      :y [[22] [22] [2 5 6 6] [2 6 3] [2 6 2 2] [2 2 2 6 4]
                          [2 2 2 6 3] [2 6 6] [2 6 6] [2 5 7 3] [6 7 2] [4 6 3 2]
                          [4 1 1 6 2] [5 1 7 3] [2 5 6 3 2] [2 6 2] [8 6 3 2]
                          [2 5 6 6] [2 4 5 2] [8 4 2] [5 11 2] [4 7 3] [4 3 6 2]
                          [4 6 3 2] [5 5 2] [13 2] [22] [22] [13 2 2] [22] [22] [22]]}]
        (is (= expected (calc-nonogram-hints find-ground-runs image-data)))))))
