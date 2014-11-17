(ns web-bookmarks.core
  (:require [pani.cljs.core :as pani]
            [om-bootstrap.panel :as boot-pl]
            [om-bootstrap.button :as boot-bt]
            [om-bootstrap.random :as boot-r]
            [om-tools.dom :as boot-d :include-macros true]
            [cljs.core.async :refer [<!]]
            [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true])
  (:require-macros [cljs.core.async.macros :refer [go]]))

(def firebase-app-url "https://ciw-bookmarks.firebaseio.com/")
(enable-console-print!)
(def app-state (atom {}))

;;; om components
(defn tag-view [tag]
  (om/component
    (boot-bt/button {:bs-size "xsmall"} tag)))

(defn bookmark-view [[_ bookmark]]
  (om/component
   (boot-pl/panel {}
                  (boot-d/h4 {} (dom/a #js {:href (:url bookmark) :target "_blank"} (:title bookmark)))
                  (dom/div #js {:className "note"} (:notes bookmark))
                  (dom/div #js {:className "tags"} (apply boot-bt/toolbar {} (om/build-all tag-view (:tags bookmark))))
                  (dom/div #js {:className "date"} (boot-r/label {:bs-style "default"} (:date bookmark))))))

(defn bookmarks-view [app owner {:keys [r]}]
  (om/component
   (dom/div nil
            (apply dom/div nil (om/build-all bookmark-view (:bookmarks app))))))

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
