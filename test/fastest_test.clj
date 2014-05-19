(ns fastest-test
  (:require [clojure.test :refer :all]
            [fastest :as f]))

(defmacro slow [expr]
  `(do
     (Thread/sleep 50)
     ~expr))

(defn hang []
  (when-not (Thread/interrupted)
    (recur)))

(defmacro is-invariant [a b & args]
  `(is (= (~a ~@args)
          (~b ~@args))))

(deftest tests
  (is-invariant or f/or false nil :x (slow true) (hang))
  (is-invariant or f/or nil false nil nil nil (slow false))
  (is-invariant or f/or)
  (is-invariant and f/and :y true false (slow nil) :x (hang) true)
  (is-invariant and f/and :y true :x :y (slow :z))
  (is-invariant and f/and)
  (is (= 1 (f/some number? (hang) :a :b (slow 1) :c)))
  (is (nil? (f/some number? :a :b :c)))
  (is (= :y (f/any (hang) (hang) (slow :x) :y)))
  (is (= 3 (f/any* (slow 1) 2 (slow (slow 3)) (slow 4) (slow :five))))
  (is (= 4 (let [!x (atom 0)]
             (f/any* (do (Thread/sleep 20) (swap! !x inc))
                     (do (Thread/sleep 100) (swap! !x inc))
                     (do (Thread/sleep 50) (swap! !x inc))
                     (do (Thread/sleep 20) (swap! !x inc)))
             @!x)))
  (is (not (f/and (hang) true (slow false))))
  (is (f/or (hang) false (slow true))))

(let [this-ns-name (ns-name *ns*)]
  (defn test-fastest []
    (require [this-ns-name] :reload-all)
    (run-tests this-ns-name)))