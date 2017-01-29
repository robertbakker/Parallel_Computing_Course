(ns com.robertmark.MyImage
  (:import (java.io File)
           (java.awt.image BufferedImage)
           (javax.imageio ImageIO)))

(import 'java.awt.image.BufferedImage)
(use 'clojure.java.io)

(defn setpxl [^BufferedImage image data]
  (let [h (.getHeight image)
        w (.getWidth image)]
    (.setRGB image 0 0 w h ^ints data 0 w)
    ) )

(defn getrgb [rgb]
  (let [r  (bit-shift-right (bit-and rgb (int 0x00FF0000)) 16)
        g  (bit-shift-right (bit-and rgb (int 0x0000FF00)) 8)
        b  (bit-and rgb (int 0x000000FF))]
    [r g b])
  )

(defn getpxl [^BufferedImage img]
  (.getRGB img 0 0  (.getWidth img) (.getHeight img) nil 0 (.getWidth img) )
  )

(defn graycalc [[r g b]]
  (let [gray (int (/ (+ r g b) 3))
        r  (bit-shift-left gray 16)
        g  (bit-shift-left gray 8)
        b  gray
        a  (bit-shift-left 0x00 24)
        ]
    (int (bit-or a r g b))))

(defn testrgb []
  (let [img (time (javax.imageio.ImageIO/read (as-file "../../../image.jpg")))
        h (.getHeight img)
        w (.getWidth img)
        arr (time (int-array (getpxl img)))
        gray (time
               ;;why is amap so slow?

               ;;400ms
               ; (int-array (map #(graycalc (getrgb %1)) arr))

               ;;8000ms
               (amap ^ints arr idx ret ^int (graycalc (getrgb (aget ^ints arr idx))))
               )
        ]
    (time (setpxl img gray))

    ))

(time (testrgb))