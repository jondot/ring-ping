(ns ring.middleware.ping
  (:require [clj-http.client :as client]))


(defn- ping-error [error-res error]
  (assoc-in error-res [:headers "x-ping-error"] error))

(defn wrap-ping [app & [opts]]
  (fn [req]
    (let [sopts (merge { :ok-text    "ok"     :ok-code    200
                         :error-text "error"  :error-code 500
                         :ok-regex   #"body"  :version "0"     }
                       opts)
          res { :status  (:ok-code sopts)
                :headers { "content-type"   "text/html"
                           "x-app-version"  (:version sopts)
                           "Cache-Control"  "no-cache, no-store, max-age=0, must-revalidate"
                           "Pragma"         "no-cache"
                           "Expires"        "Tue, 8 Sep 1981 08:42:00 UTC" }
                :body    (:ok-text sopts) }
          error-res (assoc res :body   (:error-text sopts)
                               :status (:error-code sopts))
          check (:check sopts)
          check-url (:check-url sopts)]
      (cond
        check
          (if (check) res  (ping-error error-res "logic"))

        check-url
            (let [body  (:body (client/get check-url))
                  regex (:ok-regex sopts)]
              (try
                (if (re-find regex body) res (ping-error error-res "regex"))
                (catch Exception e (ping-error error-res "timeout"))))

        :otherwise res))))



