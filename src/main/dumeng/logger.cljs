(ns dumeng.logger
  (:require [oops.core :refer [oapply]]))

(defn log
  [& args]
  (oapply js/console "log" args))
