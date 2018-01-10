(ns ml-frontend.core
    (:require [reagent.core :as reagent :refer [atom]]
              [secretary.core :as secretary :include-macros true]
              [accountant.core :as accountant]
              [cljs-http.client :as http]
              [cljs.core.async :refer [go <!]]
              [cljs.reader :as reader]))

;; -------------------------
;; Views

(defn home-page []
  [:div [:h2 "Welcome to ml-frontend"]
   [:div [:a {:href "/upload"} "Upload your photos"]]])

(defonce image-file (reagent/atom nil))
(defonce preview-image (reagent/atom ""))
(defonce show-result (reagent/atom false))

(defn reset-image-result []
  (reset! show-result false)
  (reset! image-file nil)
  (reset! preview-image ""))

(defn on-image-select [e]
  (let [file (-> e
              .-currentTarget
              .-files
              (aget 0))
        reader (js/FileReader.)]
    (set! (.-target e) "")
    (reset-image-result)
    (reset! image-file file)
    (set! (.-onloadend reader) #((reset! preview-image (.-result reader))))
    (.readAsDataURL reader file)))

(defn image-uploader [test-prop second third]
  [:div
   [:input {:type "file"
            :on-change on-image-select}]
   [:img {:width 300 :src @preview-image}]])

(defn String->Number [numeric-string]
  (reader/read-string numeric-string))

(defn parse-response [response]
  (let [probable (String->Number (:body response))]
    (str (if (< probable 0.5)
           (str "Dog " (* 100 (- 1 probable)))
           (str "Cat " (* 100 probable)))
         "%")))

(defn result-page [result-id]
  (let [result (reagent/atom "")]
    (go (let [response (<! (http/post
                            (str "http://143.248.36.226:5000/result/" result-id)
                            {:multipart-params [["file" @image-file]]}))]
          (prn response)
          (reset! result (parse-response response))))
    (fn [result-id]
      [:div
       [:h2 "Your animal is " @result]
       [:a {:href "#" :on-click #(reset-image-result)} "Upload other photos"]])))

(defn upload-page []
  [:div [:h2 "Upload a photo, and see which animal you resemble"]
   [:div
    [:a {:style {:display "block"} :href "/"} "go to the home page"]
    [:a {:href "#" :on-click #(reset! show-result true)} "show me result"]
    [image-uploader]
    (and @show-result
         [result-page 123])]])

(defonce result-id "")


;; -------------------------
;; Routes

(defonce page (atom #'home-page))

(defn current-page []
  [:div [@page]])

(secretary/defroute "/" []
  (reset! page #'home-page))

(secretary/defroute "/result/:id" [id]
  (reset! result-id 0)
  (reset! page #'result-page))

(secretary/defroute "/upload" []
  (reset! page #'upload-page))

;; -------------------------
;; Initialize app

(defn mount-root []
  (reagent/render [current-page] (.getElementById js/document "app")))

(defn init! []
  (accountant/configure-navigation!
    {:nav-handler
     (fn [path]
       (secretary/dispatch! path))
     :path-exists?
     (fn [path]
       (secretary/locate-route path))})
  (accountant/dispatch-current!)
  (mount-root))
