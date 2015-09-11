(ns slacko.core-test
  (:require [clojure.test :refer :all]
            [slacko.core :refer :all]))

(deftest successful-deploy
  (testing "Successful deployment."
    (is (= (deploy "deploy string") "Deploy successful."))))

(deftest parse-deploy-string-without-branch
  (testing "Deploy string parsing without branch."
    (is (= (count (parse-deploy-string "deploy staging")) 2))
    (is (= (:target (parse-deploy-string "deploy staging")) "staging"))
    (is (= (:branch (parse-deploy-string "deploy staging") nil) nil))))

(deftest parse-deploy-string-with-branch
  (testing "Deploy string parsing with branch."
    (is (= (count (parse-deploy-string "deploy staging test-branch")) 3))
    (is (= (:target (parse-deploy-string "deploy staging")) "staging"))
    (is (= (:branch (parse-deploy-string "deploy staging test-branch")) "test-branch"))))
