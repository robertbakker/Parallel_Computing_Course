(ns com.robertmark.image
  (import java.io.File)
  (import java.awt.Color)
  (import java.awt.image.BufferedImage)
  (import javax.imageio.ImageIO)
  (import org.apache.commons.lang3.time.StopWatch)
  )
(set! *warn-on-reflection* true)
(defn basePath []
  (.getAbsolutePath (new File ""))
  )

(defn path [p]
  (clojure.string/join "/../../../" [(basePath), p]))

(defn readFile
  [path]
  (new File path))

(defn grayPixel
  [bufferedImage, x, y]
  (let [
        rgb (.getRGB bufferedImage x y),
        r (bit-and (bit-shift-right rgb 16) 0xFF),
        g (bit-and (bit-shift-right rgb 8) 0xFF),
        b (bit-and rgb 0xFF),
        grayLevel (int (/ (+ r g b) 3)),
        gray (+ (bit-shift-left grayLevel 16) (bit-shift-left grayLevel 8) grayLevel)
        ]
    (.setRGB  bufferedImage x y gray)
    )
  )

(defn makeGray
  [bufferedImage]
  (let [
        ^int  width (.getWidth bufferedImage),
        ^int height (.getHeight bufferedImage)
        ]
    (doseq [x (range (- width 1)) y (range (- height 1))]
      (grayPixel bufferedImage x y))
    )
  )

(defn makeImageGray
  [input, output]
  (let [
        inputFile (readFile input),
        inputBuffer (ImageIO/read inputFile),
        outputFile (new File output)
        ]
    (makeGray inputBuffer)
    (javax.imageio.ImageIO/write
      inputBuffer "jpg" outputFile)
    )
  )

(defn benchmark []
  (let [
        stopWatch (new StopWatch)
        ]
    (.start stopWatch)
    (makeImageGray (path "image.jpg") (path "grayscaled_image_clojure.jpg"))
    (.stop stopWatch)
    (println (concat (.getTime stopWatch)))))


;(benchmark)
(time (makeImageGray (path "image.jpg") (path "grayscaled_image_clojure.jpg")))

