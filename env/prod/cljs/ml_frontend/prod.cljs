(ns ml-frontend.prod
  (:require [ml-frontend.core :as core]))

;;ignore println statements in prod
(set! *print-fn* (fn [& _]))

(core/init!)
