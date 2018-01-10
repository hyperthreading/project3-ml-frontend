(ns ml-frontend.middleware
  (:require [ring.middleware.defaults :refer [site-defaults wrap-defaults]]
            [prone.middleware :refer [wrap-exceptions]]
            [ring.middleware.reload :refer [wrap-reload]]
            [ring.middleware.cors :refer [wrap-cors]]))

(defn wrap-middleware [handler]
  (-> handler
      (wrap-defaults site-defaults)
      (wrap-cors :access-control-allow-origin [#"http://localhost:5000"]
                 :access-control-allow-methods [:get :put :post :delete])
      wrap-exceptions
      wrap-reload))
