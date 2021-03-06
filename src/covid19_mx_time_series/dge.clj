(ns covid19-mx-time-series.dge
  (:require [clojure.data]
            [clojure.data.csv :as csv]
            [clojure.java.io :as io]
            [clj-time.core :as t]
            [clj-time.format :as f]
            [clj-time.local :as l]
            [covid19-mx-time-series.sinave :as sinave]))



(def bigtable
  (csv/read-csv
   (slurp "resources/200414COVID19MEXICO.csv")))


(def state-codes
  ["Aguascalientes"
   "Baja California"
   "Baja California Sur"
   "Campeche"
   "Coahuila"
   "Colima"
   "Chiapas"
   "Chihuahua"
   "Ciudad de México"
   "Durango"
   "Guanajuato"
   "Guerrero"
   "Hidalgo"
   "Jalisco"
   "México"
   "Michoacán"
   "Morelos"
   "Nayarit"
   "Nuevo León"
   "Oaxaca"
   "Puebla"
   "Queretaro"
   "Quintana Roo"
   "San Luis Potosí"
   "Sinaloa"
   "Sonora"
   "Tabasco"
   "Tamaulipas"
   "Tlaxcala"
   "Veracruz"
   "Yucatán"
   "Zacatecas"])


(defn death-date
  [r]
  (nth r 11))


(defn resultado
  [r]
  (nth r 29))


(defn entidad-res
  [r]
  (nth r 6))


(defn entidad-um
  [r]
  (nth r 3))


(defn state
  [r]
  (nth state-codes
       (- (Integer/parseInt (entidad-um r)) 1)))


(defn parse-date
  [s]
  (f/parse (f/formatter "yyyy-M-d") s))


(defn deaths
  [csvdata]
  (filter #(and (= (resultado %) "1")
                (not= (death-date %) "9999-99-99"))
          (rest csvdata)))


(defn confirmed
  [csvdata]
  (filter #(= (resultado %) "1") (rest csvdata)))


(defn suspects
  [csvdata]
  (filter #(= (resultado %) "3") (rest csvdata)))


(defn negatives
  [csvdata]
  (filter #(= (resultado %) "2") (rest csvdata)))


(defn death-counts
  [csvdata]
  (frequencies (map state (deaths csvdata))))


(defn confirmed-counts
  [csvdata]
  (frequencies (map state (confirmed csvdata))))


(defn suspect-counts
  [csvdata]
  (frequencies (map state (suspects csvdata))))


(defn negative-counts
  [csvdata]
  (frequencies (map state (negatives csvdata))))




#_(def s (sinave/fetch-daily-states))
#_(clojure.data/diff (death-counts bigtable) (sinave/death-counts s))
#_(clojure.data/diff (confirmed-counts bigtable) (sinave/confirmed-counts s))
#_(clojure.data/diff (suspect-counts bigtable) (sinave/suspect-counts s))
#_(clojure.data/diff (negative-counts bigtable) (sinave/negative-counts s)) ;; FAIL


;; errores
;; 0) numero total de casos negativos no corresponde con lo mostrado en el mapa
;; 1) no tenemos fecha de resultado
;; 2) fecha de actualización acutalizan todos en lugar de solo lso que tienen cambios

