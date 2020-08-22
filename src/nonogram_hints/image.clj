(ns nonogram-hints.image
  (:require [clojure.java.io :as io])
  (:import (javax.imageio ImageIO)
           (java.awt.image DataBuffer)))

(defn get-num-bands
  [image]
  (-> image
      (.getSampleModel)
      (.getNumBands)))

(defn get-scale-factor
  "The scale factor is the value that can be used to normalize values in
   a pixel. If the data type of the image data is not an interger data type
   then an exception will be thrown."
  [raster]
  (let [data-buf (.getDataBuffer raster)
        integer-image-types #{DataBuffer/TYPE_BYTE DataBuffer/TYPE_SHORT
                              DataBuffer/TYPE_USHORT DataBuffer/TYPE_INT}
        data-type (.getDataType data-buf)
        data-bits (DataBuffer/getDataTypeSize data-type)]
    (if (get integer-image-types data-type)
        (- (reduce * (repeat data-bits 2)) 1)
        (throw (Exception. "The image type is unsupported.")))))

(defn extract-pixel-data
  "Extract the pixel data from the image, normalize it, and apply the
   pixel-tx-fn to ach pixel. The output will be a list of lists 
   containing the outputs of the pixel-tx-fn for each pixel."
  [image pixel-tx-fn]
  (let [num-bands (get-num-bands image)
        raster (.getData image)
        scale-factor (get-scale-factor raster)
        height (.getHeight image)
        width (.getWidth image)]
    (for [y (range height)]
      (for [x (range width)]
        (->> (.getPixel raster x y (double-array num-bands))
             (map #(/ % scale-factor))
             (pixel-tx-fn))))))

(defn pixel->pixel
  "A transform for pixel data that transforms the pixel data array
   to a Clojure list. This is not used in the rest of the program
   but is included to make testing easier."
  [pixel]
  (map identity pixel))

(defn pixel->intensity
  "Calculate the intesity of an image ignoring the alpha channel."
  [pixel]
  (let [num-bands (count pixel)]
    (if (>= num-bands 3)
        (/ (reduce + (take 3 pixel)) 3)
        (first pixel))))

(defn pixel->bit
  "Transform data for a pixel into a single value that is either 0 or 1.
   Dark pixels will result in a 1 and light ones will result in 0. Alpha
   channels are ignored."
  [pixel]
  (if (< 0.5 (pixel->intensity pixel)) 0 1))

(defn get-pixels
  "Extract pixel data from the image contained in the input-stream"
  [input-stream]
  (let [image (ImageIO/read input-stream)]
    (extract-pixel-data image pixel->bit)))
