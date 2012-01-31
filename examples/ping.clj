(ns examples
  (:use ring.adapter.jetty)
  (:use [ring.middleware.ping])
  (:import java.util.Date java.text.SimpleDateFormat))


(def my-handler (fn [req]
              {:status  200
              :headers {"Content-Type" "text/html"}
              :body    (str "<h3>Hello World from Ring</h3>"
                            "<p>The current time is "
                            (.format (SimpleDateFormat. "HH:mm:ss") (Date.))
                            ".</p>")}))

(def app
  (wrap-ping my-handler,
        {:ok-text "ok"
         ;:check-url "http://google.com"
         :check (constantly false)
         :ok-regex #"gargoil" }))

(run-jetty app {:port 8080})

