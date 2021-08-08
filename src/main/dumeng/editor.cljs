(ns dumeng.editor
  (:require [clojure.string :as str]))

;; according the property of an attribute, the editor type can be largely derived.
;; the edit component type can be :
;; - switch
;; - single line input
;; - select (nullable?)
;; - single element select (nullable?)
;; - multiple element select (nullable?)
;; -

(def edit-type
  [
   ])

(def primitive-types ["Boolean" "String" "Integer"])

(defn- get-type [attr]
  (last (str/split (:type attr) #"#")))

(defn get-edit-type
  ;; TODO: the edit type can not derived only by the attribute itself.
  ;; would need the information of the whole context.
  [attr ]
  (let [t (get-type attr)]
    (cond
      (= t "Boolean") :switch
      (= t "String") :single-line-text
      (not (contains? primitive-types t)) (let [upper (get-in attr [:multiplicity :upper])]
                                            (if (= upper 1)
                                              :single-select
                                              :multiple-select))
      )))