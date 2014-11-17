(ns clojure-conj-talk.core
  (:refer-clojure :exclude [map reduce into partition partition-by take merge])
  (:require [clojure.core.async :refer :all :as async]
            [clojure.pprint :refer [pprint]]))

(def c (chan))

(take! a (fn [v] (println v)))

(put! a 52)

(put! c "Hello World" (fn [] (println "Done putting")))
        
(take! c (fn [v] (println "Got " v)))

(go 42)

(<!! (go 42))

(def c (chan))

(put! c 52)

(go-loop [v (<! c)]
  (println "test" v))

(go (println "no loop" (<! c)))
