(ns ml-frontend.middleware
  (:require [ring.middleware.defaults :refer [site-defaults wrap-defaults]]
            [ring.middleware.cors :refer [wrap-cors]]))

(defn wrap-middleware [handler]
  (wrap-defaults handler site-defaults)
  (wrap-cors :access-control-allow-origin [#"http://localhost:5000"]
             :access-control-allow-methods [:get :put :post :delete]))
