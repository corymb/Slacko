(ns slacko.core
  (:gen-class))

(use 'slack-rtm.core)
(def rtm-conn (connect ""))

(def events-publication (:events-publication rtm-conn))

(defn message-handler [message]
  (println (:text message)))

(def message-receiver message-handler)


(defn -main
  ;; (println (System/getenv "SLACK_TOKEN")))
  [& args]
  (sub-to-event events-publication :message message-receiver))
