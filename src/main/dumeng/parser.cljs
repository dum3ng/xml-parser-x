(ns dumeng.parser
  (:require ["fast-xml-parser" :as parser]
            [dumeng.constants :refer [attr-prefix]]
            [oops.core :refer [ocall]]))

(defn parse-xmi
  "Take a xmi string, and return parsed json object."
  [xmi-data]
  (let [opts #js{"attributeNamePrefix" attr-prefix
                 "ignoreAttributes" false}]
    (ocall parser "parse" xmi-data opts)))









(defn main []
  ())