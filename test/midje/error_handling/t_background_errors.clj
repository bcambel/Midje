;; -*- indent-tabs-mode: nil -*-

(ns midje.error-handling.t-background-errors
  (:require [clojure.zip :as zip])
  (:use [midje sweet test-util]
        [midje.internal-ideas.wrapping :only [for-wrapping-target?]]
        [midje.util unify]
        [midje.error-handling monadic]
        [midje.error-handling.background-errors]))

(tabular
  (facts "before, after and around validation"
    (fact "valid, then return rest of form"
      (validate (cons ?wrapper `(:facts (do "something")))) => `(:facts (do "something")))
  
    (fact "wrapper's must use either :facts, :contents, or checks as their wrapping targets"
      (validate (cons ?wrapper `(:abc (do "something")))) => user-error-form?)
    
    (fact "correct form length" 
      (validate (cons ?wrapper `(:facts (do "something") (do "another thing")))) => user-error-form?
      (validate (list ?wrapper)) => user-error-form? ))

    ?wrapper
    'before 
    'after  
    'around)

(fact "before gets an optional :after param"
  (validate `(before :contents (do "something") :after (do "another thing"))) =not=> user-error-form?
  (validate `(before :contents (do "something") :around (do "another thing"))) => user-error-form?)

(fact "after and around don't get extra params - length should be 3"
  (validate `(after :contents (do "something") :after (do "another thing"))) => user-error-form?
  (validate `(around :contents (do "something") :after (do "another thing"))) => user-error-form?)

(facts "against-background validation"

  (fact "valid, then return rest of form"
    (validate `(against-background [(before :contents (do "something")) 
                                    (after :checks (do "something"))]
                 "body")) => `([(before :contents (do "something")) 
                                          (after :checks (do "something"))] "body")
  
    (validate `(against-background (before :contents (do "something")) 
                 "body")) 
    => 
    `( (before :contents (do "something")) 
         "body") )
    
  (fact "invalid if any state-description invalid"
    (validate `(against-background [(before :contents (do "something"))
                                    (after :BAD (do "something"))]
                 "body")) => user-error-form?
    (validate `(against-background (before :BAD (do "something"))
                 "body")) => user-error-form? ) 
  
  (fact "invalid when the second in form is not state-descriptions and/or bckground fakes" 
    (validate `(against-background :incorrect-type-here "body")) => user-error-form? )
  
  (fact "invalid when form has less than 3 elements" 
    (validate `(against-background [(before :contents (do "something"))
                                    (after :BAD (do "something"))])) => user-error-form? 
    (validate `(against-background (before :contents (do "something")))) => user-error-form? ))

(facts "background validation"

  (fact "valid, then return rest of form"
    (validate `(background (before :contents (do "something")) 
                           (after :checks (do "something")))) 
    
    => `( (before :contents (do "something")) 
          (after :checks (do "something")))
  
    (validate `(background (before :contents (do "something")))) 
    => 
    `( (before :contents (do "something"))))
    
  (fact "invalid if any state-description invalid"
    (validate `(background (before :contents (do "something"))
                           (after :BAD (do "something")))) => user-error-form?
    (validate `(background (before :BAD (do "something")))) => user-error-form? ) )  

 ;; Validation end-to-end facts


;;;;;;;;;;;;;;;;;;;;;;;; ** `against-background` end-to-end ** ;;;;;;;;;;;;;;;;;;;;;;;;;;;;

;; ~~ Vectory

;; invalid wrapping targets
(causes-validation-error
  (against-background [(before :invalid-wrapping-target (do "something"))] 
    "body"))

;; check for vectors w/ no state-descriptions or background fakes
(causes-validation-error
  (against-background [:not-a-state-description-or-fake]
    (fact nil => nil)))

(defn f [] )

;; check for vectors w/ one thing that isn't a state-description or background fake
(causes-validation-error
  (against-background [(before :contents (do "something")) (f) => 5 :other-odd-stuff]
    (fact nil => nil)))

;; invalid if missing background fakes or state descriptions 
(causes-validation-error
  (against-background []
    (fact nil => nil)))

;; ~~Sequency 

;; invalid wrapping targets
(causes-validation-error
  (against-background (before :invalid-wrapping-target (do "something")) 
    "body"))

;; invalid when list w/ no state-descriptions or background fakes
(after-silently
  (against-background (:not-a-state-description-or-fake)
    (fact nil => nil))

  (fact 
    @reported =future=> (one-of (contains {:type :user-error}))))

; check for one thing that isn't a state-description or background fake
(causes-validation-error
  (against-background :invalid-stuff-here
    (fact nil => nil)))

;; invalid if missing background fakes or state descriptions 
(causes-validation-error
  (against-background
    (fact nil => nil)))
           

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;; ** `background` end-to-end ** ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

;; invalid wrapping targets
(causes-validation-error
  (background (before :invalid-wrapping-target (do "something"))))

;; invalid when anything doesn't look like a state-description or background fake
(causes-validation-error
  (background (before :contents (do "something")) 
              (:not-a-state-description-or-fake)))

; invalid when one thing isn't a state-description or background fake
(causes-validation-error
  (background :invalid-stuff-here))

;; invalid if missing background fakes or state descriptions 
(causes-validation-error
  (background))