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

(defn basePath []
  (.getAbsolutePath (new File ""))
  )

(defn path [p]
  (clojure.string/join "/../../../" [(basePath), p]))

(defn readFile
  [path]
  (new File path))

(defn setpxl [^BufferedImage image data]
  (let [
        h (.getHeight image)
        w (.getWidth image)
        ]
    (.setRGB image 0 0 w h data 0 w)
    )
  )

(defn getrgb [rgb]
  (let [r (bit-shift-right (bit-and rgb (int 0x00FF0000)) 16)
        g (bit-shift-right (bit-and rgb (int 0x0000FF00)) 8)
        b (bit-and rgb (int 0x000000FF))]
    [r g b])
  )

(defn grayPixel
  [rgb]
  (let [
        r (bit-and (bit-shift-right rgb 16) 0xFF),
        g (bit-and (bit-shift-right rgb 8) 0xFF),
        b (bit-and rgb 0xFF),
        grayLevel (int (/ (+ r g b) 3)),
        ]
    (+ (bit-shift-left grayLevel 16) (bit-shift-left grayLevel 8) grayLevel)
    )
  )


(defn getpxl [^BufferedImage img]
  (.getRGB img 0 0 (.getWidth img) (.getHeight img) nil 0 (.getWidth img))
  )

(defn makeItGray [pixels]
  (time-print "making it gray" (doall (map grayPixel pixels)))
  )
(defn graycalc [[r g b]]
  (let [gray (int (/ (+ r g b) 3))
        r (bit-shift-left gray 16)
        g (bit-shift-left gray 8)
        b gray
        a (bit-shift-left 0x00 24)
        ]
    (int (bit-or a r g b))))

(defn grayThread [^BufferedImage, pixels]

  )

(defn getPixels [^BufferedImage img, startX]
  (.getRGB img startX 0 (.getWidth img) (.getHeight img) nil 0 (.getWidth img))
  )

(defn readImage [fileName]
  (javax.imageio.ImageIO/read (as-file (path fileName)))
  )


(defn grayThreaded [fileName, threads]
  (let [
        img (time-print "Reading image" (readImage fileName))
        h (.getHeight img)
        w (.getWidth img)
        widthChunk (* h (Math/ceil (/ w threads)))
        pixels (time-print "Get pixels" (getpxl img))
        partitionedPixels (time-print "Partitioning the data" (partition-all widthChunk pixels))
        gray (pmap makeItGray partitionedPixels)
        grayResult (time-print "Calculating result in parallel" (doall (apply concat gray)))
        ]
    (time-print "Setting pixel data back into buffered image" (setpxl img (int-array grayResult)))
    (javax.imageio.ImageIO/write
      img "jpg" (new File "test.jpg")
      )))

(defn testrgb []
  (let [img (time (javax.imageio.ImageIO/read (as-file (path "vissenkom.jpg"))))
        h (.getHeight img)
        w (.getWidth img)
        arr (time (int-array (getpxl img)))
        gray (time
               ;;why is amap so slow?

               ;;400ms
               ;(int-array (map #(graycalc (getrgb %1)) arr))


               ;;8000ms
               (amap ^ints arr idx ret ^int (graycalc (getrgb (aget ^ints arr idx))))
               )
        ]
    (time (setpxl img gray))
    (javax.imageio.ImageIO/write
      img "jpg" (new File "test.jpg")
      )

    ))

;(testrgb)
(time-print "Full process" (grayThreaded "image.jpg" 4))