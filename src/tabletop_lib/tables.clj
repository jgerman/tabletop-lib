(ns tabletop-lib.tables
  (:gen-class
   :methods [#^{:static true} [roll_on_table [String] String]])
  (:require [cheshire.core :as json]
            [instaparse.core :as insta]
            [tabletop-lib.dice :as dice]))

(def range-notation
  (insta/parser
   "<range> = number #'-' number | number
    <number> = #'[1-9][0-9]*'"))

(defn evaluate-range [range-parse]
  "Expects a tree parsed from range notation"
  (cond
   (> (count range-parse) 1) (range (read-string (first range-parse))
                            (+ 1 (read-string (last range-parse))))
   :else (map read-string range-parse)))

(defn in-range [x r]
  (some #{x} r))

(defn in-keyrange [x key]
  "Expects a number and a range expressed as a keyword e.x 5, and :3-6"
  (let [r (evaluate-range (range-notation (name key)))]
    (in-range x r))
  )

(defn table-lookup [roll table]
  "A roll is an integer a table is a set of key value pairs, keys are ranges, values are the table entries"
  (let [key (first (filter #(in-keyrange roll %) (keys table)))]
      (table key)))

(defn roll-on-table [table]
  "Expects a table definition as json, returns a random entry from that table"
  (let [table-def (json/parse-string table true)
        d (table-def :roll)
        entries (table-def :entries)]
    (table-lookup (dice/roll d) entries))
  )

(defn -roll_on_table [table]
  (roll-on-table table))
