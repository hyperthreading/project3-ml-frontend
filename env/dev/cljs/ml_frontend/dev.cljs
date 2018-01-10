(ns ^:figwheel-no-load ml-frontend.dev
  (:require
    [ml-frontend.core :as core]
    [devtools.core :as devtools]))

(devtools/install!)

(enable-console-print!)

(core/init!)
