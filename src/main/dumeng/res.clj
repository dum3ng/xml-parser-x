(ns dumeng.res
  (:require [clojure.java.io :as io]))

(defmacro get-res [path]
  (-> (io/resource path)
      slurp))