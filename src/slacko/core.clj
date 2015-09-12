(ns slacko.core
  (:gen-class))

(use 'slack-rtm.core)
(use '[clojure.java.shell :only [sh]])
(require '[clojure.string :as str])

(if-let
  [token (System/getenv "SLACK_TOKEN")]
  (def rtm-conn (connect token))
  (throw (Exception. "Set API key as $SLACK_TOKEN environment variable")))


(defn parse-deploy-string [deploy-string]
  (zipmap [:deploy :target :branch] (str/split deploy-string #" "))
)

(defn send-message [message]
  "Sends a message to the channel."
  (send-event (:dispatcher rtm-conn)
              {:type "message"
               :text message
               :channel "C0AFV93U6"
               :user "U0AFVUBLM"}))

(defn deploy [message]
  (let [vars (parse-deploy-string (:text message))]
  (get :errors (sh "/bin/sh" "deploy.sh" (:target vars) (:branch vars) (send-message "Deploy successful.")))))

(defn message-handler [message]
  ;; Routes commands to functions
  (let [message-text (:text message)]
    (case (first (re-find #"^!([\w]*)" message-text))
      "!deploy" (deploy message)
      nil)))

(def events-publication (:events-publication rtm-conn))
(def message-receiver message-handler)

(defn -main
  [& args]
  (sub-to-event events-publication :message message-receiver)
)
