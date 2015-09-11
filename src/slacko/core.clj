(ns slacko.core
  (:gen-class))

(use 'slack-rtm.core)
(use '[clojure.java.shell :only [sh]])
(require '[clojure.string :as str])

(if-let
  [token (System/getenv "SLACK_TOKEN")]
  (def rtm-conn (connect token))
  (throw (Exception. "Set API key as $SLACK_TOKEN environment variable.")))

(def events-publication (:events-publication rtm-conn))

(defn message-handler [message]
  (println [123 (:text message)]))

(def message-receiver message-handler)

(defn parse-deploy-string [deploy-string]
  (zipmap [:deploy :target :branch] (str/split deploy-string #" "))
)

(defn deploy [command]
  (println (parse-deploy-string command))
  (println "Deploying...")
  ;; (get :errors (sh command) "Deploy successful.")
  (get :errors (sh "ls") "Deploy successful.")
)

(defn dispatcher [func args]
  (let [args (subs args 1) commands
      { :deploy (deploy args), }]
  (func commands))
)

(defn message-handler [message]
  "Parses mesages for keywords."
  (case (re-find #"^!([\w]*)" message)
    "!deploy" (dispatcher :deploy message)
    nil
  )
)

;; (defn send-message [channel]
;;   "Sends a message to the channel."
;;   (println (conj "Hi" message))
;; )

(defn -main
  [& args]
  (sub-to-event events-publication :message message-receiver)
  ;; (-> (deploy "ls") println )

  (println (:deploy dispatcher))
  ;; (println (message-handler "!deploy staging master"))
  ;; (println rtm-conn)
)
