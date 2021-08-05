(ns dumeng.core
  (:require [dumeng.parser :as parser]
            [dumeng.db :as db]
            [dumeng.excel :as excel]
            [dumeng.logger :refer [log]]
            ["path" :as path]
            ["fs" :as fs]
            ["process" :as process]
            ["he" :as he]
            [oops.core :refer [ocall]]))

(defn test-path []
  (.join path js/__dirname "resources"))

#_(ocall path "resolve" js/__dirname "resources/UML20161101.xmi")

(defn fill-db! [file]
  ;; the `__dirname` in the root directory
  (let [data-str (ocall fs "readFileSync" file #js{:encoding "utf8"})
        data (parser/parse-xmi data-str)]
    (db/prepare-db! data)))

(defn get-attribute-detail
  "for every attr, get
  - comment
  - multiplicity
  - etc. "
  [eid]
  (let [q-comment '[:find ?body
                    :in $ ?owner
                    :where
                    [?e :entity/type :ownedComment]
                    [?e :owner ?owner]
                    [?e :body ?body]]
        q-mul '[:find ?e ?type ?value
                :in $ ?owner
                :where
                (or [?e :entity/type :lowerValue]
                    [?e :entity/type :upperValue])
                [?e :owner ?owner]
                [?e :entity/type ?type]
                [(get-else $ ?e :value 0) ?value]]
        comment (-> (db/q q-comment eid)
                    (into [])
                    (ffirst))
        muls (db/q q-mul eid)
        lower (if-let [l (some #(if (= (nth % 1) :lowerValue) %) muls)]
                (nth l 2) 1)
        upper (if-let [l (some #(if (= (nth % 1) :upperValue) %) muls)]
                (nth l 2) 1)
        ]
    {:comment comment :multiplicity {:lower lower :upper upper}}))

(defn get-class-detail
  [cls-name eid]
  (let [q '[:find ?e ?c
            :in $ ?owner
            :where
            [?e :entity/type :ownedComment]
            [?e :owner ?owner]
            [?e :body ?c]]
        qAttr '[:find (pull ?e [*])
                :in $ ?owner
                :where
                [?e :entity/type :ownedAttribute]
                [?e :owner ?owner]]
        comments (into [] (db/q q eid))
        attrs (map #(nth % 0) (db/q qAttr eid))
        attrs (map #(merge %1 %2)
                   (map get-attribute-detail
                        (map :db/id attrs))
                   attrs)]
    {:class-name cls-name
     :comment    (ocall he "decode" (get (get comments 0) 1))
     :attributes (into [] attrs)}))

(defn get-all-class
  []
  (let [q '[:find ?e ?xmi-id ?name
            :keys db/id xmi-id name
            :where
            [?e :xmi:type "uml:Class"]
            [?e :xmi:id ?xmi-id]
            [?e :name ?name]]
        clss (db/q q)]
    (into [] clss)))

(defn get-data
  []
  (let [clss (get-all-class)
        details (map-indexed #(do
                                (log "[query] " %1 (first %2))
                                (apply get-class-detail %2))
                             (map (fn [c] [(:name c) (:db/id c)]) clss))]
    details))

(defn main
  []
  (let [file (nth (.-argv process) 2)
        file (ocall path "resolve" (ocall process "cwd") file)]
    (fill-db! file)
    (log "database ready.")
    (let [data (get-data)]
      (print data)
      (excel/write-excel! data))))