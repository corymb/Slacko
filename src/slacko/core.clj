(ns slacko.core
  (:gen-class))

(use 'slack-rtm.core)
(use '[clojure.java.shell :only [sh]])
(require '[clojure.string :as str])

(if-let
  [token (System/getenv "SLACK_TOKEN")]
  (def rtm-conn (connect token))
  (throw (Exception. "Set API key as $SLACK_TOKEN environment variable.")))


(defn parse-deploy-string [deploy-string]
  (zipmap [:deploy :target :branch] (str/split deploy-string #" "))
)

;; (defn dispatcher [func args]
  ;; (let [args (subs args 1) commands
  ;;     { :deploy (deploy args), }]
  ;; (func commands))
;; )

(defn deploy [message]
  (let [vars (parse-deploy-string (:text message))]
  (get :errors (sh "/bin/sh" "deploy.sh" (:target vars) (:branch vars) "Deploy successful.")))

(defn message-handler [message]
  ;; Routes commands to functions
  (let [message-text (:text message)]
    (case (first (re-find #"^!([\w]*)" message-text))
      "!deploy" (deploy message)
      nil)))


(def events-publication (:events-publication rtm-conn))
(def message-receiver message-handler)

;; (defn send-message [channel]
;;   "Sends a message to the channel."
;;   (println (conj "Hi" message))
;; )

(defn -main
  [& args]
  ;; (sub-to-event events-publication :message message-receiver)
  (println (deploy {:text "!deploy staging" :dave "PEW"}))
)
