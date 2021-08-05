(ns dumeng.test-db
  (:require
    [shadow.resource :refer-macros [inline]]
    [dumeng.db :as db]
    [dumeng.parser :as parser]
    [oops.core :refer [ocall]]
    ["fs" :as fs]
    ["path" :as path]))


(def xmi-str (inline "/state.xmi"))
(def small-xmi
  (ocall fs "readFileSync"
         (ocall path "resolve" js/__dirname "resources/state.xmi")
         #js{:encoding "utf8"}))

(defn test-parser []
  (js->clj (parser/parse-xmi small-xmi) :keywordize-keys true))

(defn test-gen-datoms []
  (db/gen-datoms (test-parser) nil))


