(ns fastest
  (:refer-clojure :exclude [some or and]))

(defmacro some-or-any* [pred & exprs]
  (if exprs
    (let [!pred (gensym "pred__")
          !final-result (gensym "done__")
          !task-count (gensym "count__")]
      `(let [~!pred ~pred
             ~!final-result (promise)
             ~!task-count (atom ~(count exprs))
             tasks# [~@(for [expr exprs]
                         `(future (locking (promise)
                                    (let [result# ~expr]
                                      (if (~!pred result#)
                                        (deliver ~!final-result result#)
                                        (if (zero? (swap! ~!task-count dec))
                                          (deliver ~!final-result result#)))))))]]
         @~!final-result
         (doseq [t# tasks#] (future-cancel t#))
         @~!final-result))))

(defmacro any [& exprs]
  `(some-or-any* (constantly true) ~@exprs))

(defmacro any* [& exprs]
  `(some-or-any* (constantly false) ~@exprs))

(defmacro or [& exprs]
  `(some-or-any* identity ~@exprs))

(defmacro and [& exprs]
  (if exprs
    `(some-or-any* not ~@exprs)
    true))

(defmacro some [pred & exprs]
  `(let [pred# ~pred
         result# (some-or-any* pred# ~@exprs)]
     (if (pred# result#)
       result#)))