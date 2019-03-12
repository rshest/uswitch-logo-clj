(ns uswitch-logo-clj.core
  (:require [clisk.live :refer :all]))

; uswitch logo color
(def ^:const uswitch-blue [0.007 0.01 0.61])

; shape boolean operations
(defn intersection ([& args] (clamp (apply vmin args) [0 0 0] [1 1 1])))
(defn union ([& args] (clamp (apply vmax args) [0 0 0] [1 1 1])))
(defn difference ([& args] (clamp (apply v- args) [0 0 0] [1 1 1])))

; circle equation
(defn circle ([r]
  (vif `(- (+ (* ~'y ~'y) (* ~'x ~'x)) (* ~r ~r)) 
        black
        white)))

; rectrangle equation, see http://mathforum.org/library/drmath/view/77216.html
(defn rect ([w h]
  (vif `(+ (Math/abs (+ (/ ~'x ~w) (/ ~'y ~h))) 
           (Math/abs (- (/ ~'x ~w) (/ ~'y ~h))) -2) 
        black
        white)))

; all pixels below y plane, used for cutting out
(defn lower-half-plane ([] (if (> y 0) black white)))

; triangle equation (right-angled, pointed upwards)
; made by rotating a rectangle and subtracting a rectangle from the bottom part
(defn triangle ([s]
  (difference
    (rotate (/ Math/PI 4) (rect s s))
    (offset [0 (- s)] (rect (* 2 s) s)))))

; a "letter u" equation
(defn letter-u ([w]
  (difference
    (union 
      (circle 0.3)
      (offset [0 0.12] (rect 0.3 0.15)))
    (circle (- 0.3 w))
    (offset [0 0.3] (rect (- 0.3 w) 0.3)))))

; combined shape of the logo
(defn uswitch-logo-shape ([]
  (union
    (difference
      (letter-u 0.15)
      (offset [-0.15 0.05] (letter-u 0.06)))
    (offset [-0.22 0.22] (triangle 0.09)))))

; coloured and centered logo
(defn uswitch-logo ([]
  (v+ uswitch-blue (offset [-0.5 -0.5] (uswitch-logo-shape)))))

; some random noise to offset pixel positions
(defn offset-noise ([]
  (v* (seamless 0.1 (compose plasma vsnoise)) [0.05 0.05 0.05])))

(defn -main [& args]
  (do
    (show (offset (offset-noise) (uswitch-logo)))
    ; uncomment to show the clean logo
    ;(show (uswitch-logo))
  ))