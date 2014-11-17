(ns web-bookmarks.core
  (:require [pani.cljs.core :as pani]
            [cljs.core.async :refer [<!]]
            [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true])
  (:require-macros [cljs.core.async.macros :refer [go]]))

;; TODO: Set this to a firebase app URL
(def firebase-app-url "https://ciw-bookmarks.firebaseio.com/")
(enable-console-print!)
(def app-state (atom {}))

;;; om components
(defn bookmark-view [[_ bookmark] owner]
  (om/component
   (dom/div nil (:notes bookmark))))

(defn bookmarks-view [app owner {:keys [r]}]
  (om/component
   (dom/div nil
            (apply dom/div nil (om/build-all bookmark-view (:bookmarks app) {:key :value})))))

(defn app-view [app owner]
  (reify
    om/IWillMount
    (will-mount [_]
      (let [r (pani/root firebase-app-url)
            b (pani/bind r :value :bookmarks)]
        ;; wire up counter, via channel
        (go (let [{:keys [val] :as v} (<! b)]
              (om/transact! app #(assoc % :bookmarks val))))
        (om/set-state! owner :fb-root r)))
    om/IRender
    (render [_]
      (let [r (om/get-state owner :fb-root)]
        (dom/div nil
                 (om/build bookmarks-view app {:opts {:r r}}))))))

(om/root app-view app-state
         {:target (.getElementById js/document "app")})
