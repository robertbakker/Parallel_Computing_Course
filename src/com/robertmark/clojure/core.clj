(ns com.robertmark.core)

(import 'java.awt.image.BufferedImage)
(import java.io.File)
(use 'clojure.java.io)

(defmacro time-print
  "Adjusted time macro to allow separate comment. Evaluates expr and prints the time it took.  Returns the value of
 expr."
  {:added "1.0"}
  [printStr, expr]
  `(let [start# (. System (nanoTime))
         ret# ~expr]
     (prn (str ~printStr " - Elapsed time: " (/ (double (- (. System (nanoTime)) start#)) 1000000.0) " msecs"))
     ret#))

(def n-threads (.availableProcessors (Runtime/getRuntime))) ; Define the available cores

(defn base-path []
  "Get the absolute path of the file being executed"
  (.getAbsolutePath (new File ""))
  )

(defn path [p]
  "Helper function to get root path of the project + filename"
  (clojure.string/join "/../../../" [(base-path), p]))

(defn gray-pixel
  [^BufferedImage bufferedImage, x, y]
  (let [
        rgb (.getRGB bufferedImage x y),                    ; Get the rgb value of the pixel (TYPE_INT_ARGB)
        r (bit-and (bit-shift-right rgb 16) 0xFF),          ; Bitshift to get only red value
        g (bit-and (bit-shift-right rgb 8) 0xFF),           ; Bitshift to get only green value
        b (bit-and rgb 0xFF),                               ; Bitshift to get only blue value
        grayLevel (int (/ (+ r g b) 3)),                    ; We chose 3, as this gave a fair amount of contrast (not too dark, not too bright)
        gray (+ (bit-shift-left grayLevel 16) (bit-shift-left grayLevel 8) grayLevel) ; Calculate the new grayscaled RGB value
        ]
    ; We set the new gray value directly to the BufferedImage reference
    (.setRGB bufferedImage x y gray)
    )
  )

(defn read-buffered-image [fileName]
  "Helper function to read a BufferedImage"
  (javax.imageio.ImageIO/read (as-file (path fileName))))

(defn save-buffered-image [^BufferedImage img, name]
  "Helper function to write BufferedImage to disk"
  (javax.imageio.ImageIO/write
    img "jpg" (new File (path name))
    ))

(defn gray-serial [fileName, outputFileName]
  (let [
        img ^BufferedImage (read-buffered-image fileName)
        h (.getHeight img)
        w (.getWidth img)
        width-range (range w)                               ; Setup width range to iterate over
        height-range (range h)                              ; Setup height range to iterate over
        ]
    (doseq [x width-range y height-range]                   ; Iterate over the full height and width of the image
      ; We use the BufferedImage directly, because if we loaded each pixel in memory first to operate on
      ; it lead to a LOT of memory usage using
      ; a very large image
      (gray-pixel ^BufferedImage img x y)                   ; Make each pixel of the image gray
      )
    (save-buffered-image img outputFileName)                ; Save the grayscaled image
    )
  )

(defn gray-parallel [fileName, outputFileName]
  (let [
        img ^BufferedImage (read-buffered-image fileName)
        h (.getHeight img)
        w (.getWidth img)
        partition-size (Math/ceil (/ w n-threads))          ; Calculate chunk of the image width based on available cores (image width / threads )
        width-range (range w)                               ; Setup full width range to iterate over
        height-range (range h)                              ; Setup height range to iterate over
        partitions (partition-all partition-size width-range) ; Partition the full width of the image into equal parts, last part can be a tiny bit smaller, depending on image width is fully divisible by n-threads
        ]
    (doall                                                  ; Ensure the LazySeq is actually being done
      (pmap                                                 ; Map each partition to a separate process
        (fn [partition]
          (doseq [x partition y height-range]               ; Each process iterates over a partition of the full width, and over the full height
            (gray-pixel ^BufferedImage img x y)             ; Make each pixel of the image gray
            ))
        partitions)
      )
    (save-buffered-image img outputFileName)                ; Save the grayscaled image
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
