(ns com.robertmark.image3)

(import 'java.awt.image.BufferedImage)
(import java.io.File)
(use 'clojure.java.io)

(defmacro time-print
  "Evaluates expr and prints the time it took.  Returns the value of
 expr."
  {:added "1.0"}
  [printStr, expr]
  `(let [start# (. System (nanoTime))
         ret# ~expr]
     (prn (str ~printStr " - Elapsed time: " (/ (double (- (. System (nanoTime)) start#)) 1000000.0) " msecs"))
     ret#))

(def n-threads (.availableProcessors (Runtime/getRuntime)))

(defn base-path []
  (.getAbsolutePath (new File ""))
  )

(defn path [p]
  (clojure.string/join "/../../../" [(base-path), p]))

(defn gray-pixel
  [^BufferedImage bufferedImage, x, y]
  (let [
        rgb (.getRGB bufferedImage x y),
        r (bit-and (bit-shift-right rgb 16) 0xFF),
        g (bit-and (bit-shift-right rgb 8) 0xFF),
        b (bit-and rgb 0xFF),
        grayLevel (int (/ (+ r g b) 3)),
        gray (+ (bit-shift-left grayLevel 16) (bit-shift-left grayLevel 8) grayLevel)
        ]
    (.setRGB bufferedImage x y gray)
    )
  )

(defn read-buffered-image [fileName]
  (javax.imageio.ImageIO/read (as-file (path fileName))))

(defn save-buffered-image [^BufferedImage img, name]
  (javax.imageio.ImageIO/write
    img "jpg" (new File (path name))
    ))

(defn gray-serial [fileName, outputFileName]
  (let [
        img ^BufferedImage (read-buffered-image fileName)
        h (.getHeight img)
        w (.getWidth img)
        width-range (range w)
        height-range (range h)
        ]
    (doseq [x width-range y height-range]
      (gray-pixel ^BufferedImage img x y)
      )
    (save-buffered-image img outputFileName)
    )
  )

(defn gray-parallel [fileName, outputFileName]
  (let [
        img ^BufferedImage (read-buffered-image fileName)
        h (.getHeight img)
        w (.getWidth img)
        partition-size (Math/ceil (/ w n-threads))
        width-range (range w)
        height-range (range h)
        partitions (partition-all partition-size width-range)
        ]
    (doall
      (pmap
        (fn [partition]
          (doseq [x partition y height-range]
            (gray-pixel ^BufferedImage img x y)
            ))
        partitions)
      )
    (save-buffered-image img outputFileName)
    )
  )

(println "Using " n-threads " threads.")
(println "")
(time-print "Small - Single Threaded" (gray-serial "vissenkom.jpg" "gray_clojure_vissenkom_serial.jpg"))
(time-print "Small - Multi-Threaded" (gray-parallel "vissenkom.jpg" "gray_clojure_vissenkom_parallel.jpg"))
(println "")
(time-print "Medium - Single Threaded" (gray-serial "hond.jpg" "gray_clojure_hond_serial.jpg"))
(time-print "Medium - Multi-Threaded" (gray-parallel "hond.jpg" "gray_clojure_hond_parallel.jpg"))
(println "")
(time-print "Large - Single Threaded" (gray-serial "image.jpg" "gray_clojure_image_serial.jpg"))
(time-print "Large - Multi-Threaded" (gray-parallel "image.jpg" "gray_clojure_image_parallel.jpg"))
