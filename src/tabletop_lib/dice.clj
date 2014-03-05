(ns tabletop-lib.dice
  (:require [instaparse.core :as insta]))

(defn d [size]
  "Basic building block for polyhedral die rolling"
  (+ 1 (rand-int size)))

; insta parse is overkill for simple rolls but eventually I'll expand it into a
; fuller die roll notation (e.x. 2d6 + 1 etc.)
(def die-notation
  (insta/parser
   "<roll> = number #'d' sides
    <number> = #'[1-9][0-9]*'
    <sides>  = #'[1-9][0-9]*' "))

(defn evaluate-roll [roll-parse]
  (reduce + (repeatedly (read-string (first roll-parse))
                        #(d (read-string (last roll-parse))))))


(defn roll [roll-str]
  (evaluate-roll (die-notation roll-str)))
