(ns slacko.core
  (:gen-class))

(use 'slack-rtm.core)
(def rtm-conn (connect "xoxb-10539963701-hosor4JglBTaEn1ZPoQvtWuJ"))

(def events-publication (:events-publication rtm-conn))

(defn message-handler [message]
  (println (:text message)))

(def message-receiver message-handler)

(use '[clojure.java.shell :only [sh]])
(require '[clojure.string :as str])

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
  ;; (println (System/getenv "SLACK_TOKEN")))
  [& args]
  (sub-to-event events-publication :message message-receiver)
  ;; (-> (deploy "ls") println )

  ;; (println (:deploy dispatcher))
  (println (message-handler "!deploy staging master"))
)
