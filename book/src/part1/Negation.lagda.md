---
title     : "Negation: Negation, with intuitionistic and classical logic"
permalink : /Negation/
---

```agda
module plfa.part1.Negation where
```

This chapter introduces negation, and discusses intuitionistic
and classical logic.

## Imports

<details><summary>Agda</summary>

```agda
open import Relation.Binary.PropositionalEquality using (_≡_; refl)
open import Data.Nat using (ℕ; zero; suc)
open import Data.Empty using (⊥; ⊥-elim)
open import Data.Sum using (_⊎_; inj₁; inj₂)
open import Data.Product using (_×_)
open import plfa.part1.Isomorphism using (_≃_; extensionality)
```
</details>

```tex
\import Logic (Empty, absurd, ||, byLeft, byRight)
\import util.Logic (&&)
\import util.Equiv (=~)
\import part1.Isomorphism (extensionality)
```


## Negation

Given a proposition `A`, the negation `¬ A` holds if `A` cannot hold.
We formalise this idea by declaring negation to be the same
as implication of false:
<details><summary>Agda</summary>

```agda
¬_ : Set → Set
¬ A = A → ⊥
```
</details>

```tex
\func \fix 4 Not (A : \Type) : \Prop => A -> Empty
```
This is a form of _reductio ad absurdum_: if assuming `A` leads
to the conclusion `⊥` (an absurdity), then we must have `¬ A`.

Evidence that `¬ A` holds is of the form

    λ{ x → N }

where `N` is a term of type `⊥` containing as a free variable `x` of type `A`.
In other words, evidence that `¬ A` holds is a function that converts evidence
that `A` holds into evidence that `⊥` holds.

Given evidence that both `¬ A` and `A` hold, we can conclude that `⊥` holds.
In other words, if both `¬ A` and `A` hold, then we have a contradiction:
<details><summary>Agda</summary>

```agda
¬-elim : ∀ {A : Set}
  → ¬ A
  → A
    ---
  → ⊥
¬-elim ¬x x = ¬x x
```
</details>

```tex
\func Not-elim {A : \Type} (not-x : Not A) (x : A) : Empty => not-x x
```
Here we write `¬x` for evidence of `¬ A` and `x` for evidence of `A`.  This
means that `¬x` must be a function of type `A → ⊥`, and hence the application
`¬x x` must be of type `⊥`.  Note that this rule is just a special case of `→-elim`.

We set the precedence of negation so that it binds more tightly
than disjunction and conjunction, but less tightly than anything else:
```agda
infix 3 ¬_
```
Thus, `¬ A × ¬ B` parses as `(¬ A) × (¬ B)` and `¬ m ≡ n` as `¬ (m ≡ n)`.

In _classical_ logic, we have that `A` is equivalent to `¬ ¬ A`.
As we discuss below, in Agda we use _intuitionistic_ logic, where
we have only half of this equivalence, namely that `A` implies `¬ ¬ A`:
<details><summary>Agda</summary>

```agda
¬¬-intro : ∀ {A : Set}
  → A
    -----
  → ¬ ¬ A
¬¬-intro x  =  λ{¬x → ¬x x}
```
</details>

```tex
\func Not-Not-intro {A : \Type} (x : A) : Not (Not A) => \lam not-x => not-x x
```
Let `x` be evidence of `A`. We show that assuming
`¬ A` leads to a contradiction, and hence `¬ ¬ A` must hold.
Let `¬x` be evidence of `¬ A`.  Then from `A` and `¬ A`
we have a contradiction, evidenced by `¬x x`.  Hence, we have
shown `¬ ¬ A`.

An equivalent way to write the above is as follows:
<details><summary>Agda</summary>

```agda
¬¬-intro′ : ∀ {A : Set}
  → A
    -----
  → ¬ ¬ A
¬¬-intro′ x ¬x = ¬x x
```
</details>

```tex
\func Not-Not-intro' {A : \Type} (x : A) (not-x : Not A) : Empty => not-x x
```
Here we have simply converted the argument of the lambda term
to an additional argument of the function.  We will usually
use this latter style, as it is more compact.

We cannot show that `¬ ¬ A` implies `A`, but we can show that
`¬ ¬ ¬ A` implies `¬ A`:
<details><summary>Agda</summary>

```agda
¬¬¬-elim : ∀ {A : Set}
  → ¬ ¬ ¬ A
    -------
  → ¬ A
¬¬¬-elim ¬¬¬x  =  λ x → ¬¬¬x (¬¬-intro x)
```
</details>

```tex
\func Not-Not-Not-elim {A : \Type} (not-not-not-x : Not (Not (Not A))) : Not A =>
  \lam x => not-not-not-x (Not-Not-intro x)
```
Let `¬¬¬x` be evidence of `¬ ¬ ¬ A`. We will show that assuming
`A` leads to a contradiction, and hence `¬ A` must hold.
Let `x` be evidence of `A`. Then by the previous result, we
can conclude `¬ ¬ A`, evidenced by `¬¬-intro x`.  Then from
`¬ ¬ ¬ A` and `¬ ¬ A` we have a contradiction, evidenced by
`¬¬¬x (¬¬-intro x)`.  Hence we have shown `¬ A`.

Another law of logic is _contraposition_,
stating that if `A` implies `B`, then `¬ B` implies `¬ A`:
<details><summary>Agda</summary>

```agda
contraposition : ∀ {A B : Set}
  → (A → B)
    -----------
  → (¬ B → ¬ A)
contraposition f ¬y x = ¬y (f x)
```
</details>

```tex
\func contraposition {A B : \Type} (f : A -> B) : Not B -> Not A =>
  \lam not-y x => not-y (f x)
```
Let `f` be evidence of `A → B` and let `¬y` be evidence of `¬ B`.  We
will show that assuming `A` leads to a contradiction, and hence `¬ A`
must hold. Let `x` be evidence of `A`.  Then from `A → B` and `A` we
may conclude `B`, evidenced by `f x`, and from `B` and `¬ B` we may
conclude `⊥`, evidenced by `¬y (f x)`.  Hence, we have shown `¬ A`.

Using negation, it is straightforward to define inequality:
<details><summary>Agda</summary>

```agda
_≢_ : ∀ {A : Set} → A → A → Set
x ≢ y  =  ¬ (x ≡ y)
```
</details>

```tex
\func \infix 1 /= {A : \Type} (x y : A) : \Prop => Not (x = y)
```
It is trivial to show distinct numbers are not equal:
<details><summary>Agda</summary>

```agda
_ : 1 ≢ 2
_ = λ()
```
</details>

```tex
\func [1/=2] : 1 /= 2 => \case __ \with {}
```
This is our first use of an absurd pattern in a lambda expression.
The type `M ≡ N` is occupied exactly when `M` and `N` simplify to
identical terms. Since `1` and `2` simplify to distinct normal forms,
Agda determines that there is no possible evidence that `1 ≡ 2`.
As a second example, it is also easy to validate
Peano's postulate that zero is not the successor of any number:
<details><summary>Agda</summary>

```agda
peano : ∀ {m : ℕ} → zero ≢ suc m
peano = λ()
```
</details>

```tex
\func peano {m : Nat} : 0 /= suc m => \case __ \with {}
```
The evidence is essentially the same, as the absurd pattern matches
all possible evidence of type `zero ≡ suc m`.

Given the correspondence of implication to exponentiation and
false to the type with no members, we can view negation as
raising to the zero power.  This indeed corresponds to what
we know for arithmetic, where

    0 ^ n  ≡  1,  if n ≡ 0
           ≡  0,  if n ≢ 0

Indeed, there is exactly one proof of `⊥ → ⊥`.  We can write
this proof two different ways:
<details><summary>Agda</summary>

```agda
id : ⊥ → ⊥
id x = x

id′ : ⊥ → ⊥
id′ ()
```
</details>

```tex
\func id (e : Empty) : Empty => e

\func id' (e : Empty) : Empty
```
But, using extensionality, we can prove these equal:
<details><summary>Agda</summary>

```agda
id≡id′ : id ≡ id′
id≡id′ = extensionality (λ())
```
</details>

```tex
\func id=id' : id = id' => extensionality (\case __ \with {})
```
By extensionality, `id ≡ id′` holds if for every
`x` in their domain we have `id x ≡ id′ x`. But there
is no `x` in their domain, so the equality holds trivially.

Indeed, we can show any two proofs of a negation are equal:
<details><summary>Agda</summary>

```agda
assimilation : ∀ {A : Set} (¬x ¬x′ : ¬ A) → ¬x ≡ ¬x′
assimilation ¬x ¬x′ = extensionality (λ x → ⊥-elim (¬x x))
```
</details>

```tex
\func assimilation {A : \Type} (not-x not-x' : Not A) : not-x = not-x' =>
  extensionality (\lam x => absurd (not-x x))

-- In Arend, we use `\Prop` to encode logic. Types in `\Prop` have at most one element.
-- Standard library lemma `Logic.prop-pi` proves exactly that.

\func assimilation' {A : \Type} (not-x not-x' : Not A) : not-x = not-x' => Logic.prop-pi
```
Evidence for `¬ A` implies that any evidence of `A`
immediately leads to a contradiction.  But extensionality
quantifies over all `x` such that `A` holds, hence any
such `x` immediately leads to a contradiction,
again causing the equality to hold trivially.


#### Exercise `<-irreflexive` (recommended)

Using negation, show that
[strict inequality](/Relations/#strict-inequality)
is irreflexive, that is, `n < n` holds for no `n`.

<details><summary>Agda</summary>

```agda
-- Your code goes here
```
</details>

```tex
\import part1.Relations (<, s<s, z<s)

\func <-irreflexive {n : Nat} (n<n : n < n) : Empty
  | {0}, ()
  | {suc n}, s<s n<n => <-irreflexive n<n
```


#### Exercise `trichotomy` (practice)

Show that strict inequality satisfies
[trichotomy](/Relations/#trichotomy),
that is, for any naturals `m` and `n` exactly one of the following holds:

* `m < n`
* `m ≡ n`
* `m > n`

Here "exactly one" means that not only one of the three must hold,
but that when one holds the negation of the other two must also hold.

<details><summary>Agda</summary>

```agda
-- Your code goes here
```
</details>

```tex
\import Arith.Nat (pred)
\import Paths (pmap, inv)

\func less=>not-eq {m n : Nat} (m<n : m < n) : m /= n
  | {0}, {suc n}, z<s => peano
  | {suc m}, {suc n}, s<s m<n => \lam sm=sn => less=>not-eq m<n (pmap pred sm=sn)

\func less=>not-greater {m n : Nat} (le : m < n) : Not (n < m)
  | {0}, {suc n}, z<s => \case __ \with {}
  | {suc m}, {suc n}, s<s m<n => \lam sn<sm => less=>not-greater m<n (m<n-pred sn<sm)

\func eq=>not-less {m n : Nat} (m=n : m = n) : Not (m < n)
  | {0}, {0}, m=n => \case __ \with {}
  | {suc m}, {suc n}, m=n => \lam sm<sn => eq=>not-less {m} {n} (pmap pred m=n) (m<n-pred sm<sn)

\func eq=>not-greater {m n : Nat} (m=n : m = n) : Not (n < m)
  | {0}, {0}, m=n => \case __ \with {}
  | {suc m}, {suc n}, m=n => \lam sn<sm => eq=>not-greater {m} {n} (pmap pred m=n) (m<n-pred sn<sm)

\func greater=>not-less {m n : Nat} (gt : n < m) : Not (m < n) => less=>not-greater gt

\func greater=>not-eq {m n : Nat} (gt : n < m) : m /= n => \lam m=n => less=>not-eq gt (inv m=n)

\func m<n-pred {m n : Nat} (sm<sn : suc m < suc n) : m < n
  | s<s m<n => m<n
```

#### Exercise `⊎-dual-×` (recommended)

Show that conjunction, disjunction, and negation are related by a
version of De Morgan's Law.

    ¬ (A ⊎ B) ≃ (¬ A) × (¬ B)

This result is an easy consequence of something we've proved previously.

<details><summary>Agda</summary>

```agda
-- Your code goes here
```
</details>

```tex
-- The statement we need to prove is a special case of `part1.Connectives.->-distrib-u`.
-- But we can easily prove equality without using it.

\import Logic (propExt)
\open && (prod, proj1, proj2)

\func ||-dual-&& {A B : \Prop} : Not (A || B) = Not A && Not B => propExt to from
  \where {
    \func to {A B : \Prop} (p : Not (A || B)) : Not A && Not B =>
      prod (\lam a => p (byLeft a)) (\lam b => p (byRight b))

    \func from {A B : \Prop} (p : Not A && Not B) : Not (A || B)
      | prod not-a not-b => \lam ab => \case ab \with {
        | byLeft a => not-a a
        | byRight b => not-b b
      }
  }

-- `Not (A && B) = Not A || Not B` is not provable in constructive mathematics.
-- But we can prove that the right side implies the left one.

\func &&-dual-||-from {A B : \Prop} (p : Not A || Not B) : Not (A && B)
  | byLeft not-a => \lam ab => not-a (proj1 ab)
  | byRight not-b => \lam ab => not-b (proj2 ab)
```


Do we also have the following?

    ¬ (A × B) ≃ (¬ A) ⊎ (¬ B)

If so, prove; if not, can you give a relation weaker than
isomorphism that relates the two sides?


## Intuitive and Classical logic

In Gilbert and Sullivan's _The Gondoliers_, Casilda is told that
as an infant she was married to the heir of the King of Batavia, but
that due to a mix-up no one knows which of two individuals, Marco or
Giuseppe, is the heir.  Alarmed, she wails "Then do you mean to say
that I am married to one of two gondoliers, but it is impossible to
say which?"  To which the response is "Without any doubt of any kind
whatever."

Logic comes in many varieties, and one distinction is between
_classical_ and _intuitionistic_. Intuitionists, concerned
by assumptions made by some logicians about the nature of
infinity, insist upon a constructionist notion of truth.  In
particular, they insist that a proof of `A ⊎ B` must show
_which_ of `A` or `B` holds, and hence they would reject the
claim that Casilda is married to Marco or Giuseppe until one of the
two was identified as her husband.  Perhaps Gilbert and Sullivan
anticipated intuitionism, for their story's outcome is that the heir
turns out to be a third individual, Luiz, with whom Casilda is,
conveniently, already in love.

Intuitionists also reject the law of the excluded middle, which
asserts `A ⊎ ¬ A` for every `A`, since the law gives no clue as to
_which_ of `A` or `¬ A` holds. Heyting formalised a variant of
Hilbert's classical logic that captures the intuitionistic notion of
provability. In particular, the law of the excluded middle is provable
in Hilbert's logic, but not in Heyting's.  Further, if the law of the
excluded middle is added as an axiom to Heyting's logic, then it
becomes equivalent to Hilbert's.  Kolmogorov showed the two logics
were closely related: he gave a double-negation translation, such that
a formula is provable in classical logic if and only if its
translation is provable in intuitionistic logic.

Propositions as Types was first formulated for intuitionistic logic.
It is a perfect fit, because in the intuitionist interpretation the
formula `A ⊎ B` is provable exactly when one exhibits either a proof
of `A` or a proof of `B`, so the type corresponding to disjunction is
a disjoint sum.

(Parts of the above are adopted from "Propositions as Types", Philip Wadler,
_Communications of the ACM_, December 2015.)

## Excluded middle is irrefutable

The law of the excluded middle can be formulated as follows:
```agda
postulate
  em : ∀ {A : Set} → A ⊎ ¬ A
```
As we noted, the law of the excluded middle does not hold in
intuitionistic logic.  However, we can show that it is _irrefutable_,
meaning that the negation of its negation is provable (and hence that
its negation is never provable):
<details><summary>Agda</summary>

```agda
em-irrefutable : ∀ {A : Set} → ¬ ¬ (A ⊎ ¬ A)
em-irrefutable = λ k → k (inj₂ (λ x → k (inj₁ x)))
```
</details>

```tex
\func em-irrefutable {A : \Prop} : Not (Not (A || Not A)) =>
  \lam k => k (byRight (\lam x => k (byLeft x)))
```
The best way to explain this code is to develop it interactively:

    em-irrefutable k = ?

Given evidence `k` that `¬ (A ⊎ ¬ A)`, that is, a function that given a
value of type `A ⊎ ¬ A` returns a value of the empty type, we must fill
in `?` with a term that returns a value of the empty type.  The only way
we can get a value of the empty type is by applying `k` itself, so let's
expand the hole accordingly:

    em-irrefutable k = k ?

We need to fill the new hole with a value of type `A ⊎ ¬ A`. We don't have
a value of type `A` to hand, so let's pick the second disjunct:

    em-irrefutable k = k (inj₂ λ{ x → ? })

The second disjunct accepts evidence of `¬ A`, that is, a function
that given a value of type `A` returns a value of the empty type.  We
bind `x` to the value of type `A`, and now we need to fill in the hole
with a value of the empty type.  Once again, the only way we can get a
value of the empty type is by applying `k` itself, so let's expand the
hole accordingly:

    em-irrefutable k = k (inj₂ λ{ x → k ? })

This time we do have a value of type `A` to hand, namely `x`, so we can
pick the first disjunct:

    em-irrefutable k = k (inj₂ λ{ x → k (inj₁ x) })

There are no holes left! This completes the proof.

The following story illustrates the behaviour of the term we have created.
(With apologies to Peter Selinger, who tells a similar story
about a king, a wizard, and the Philosopher's stone.)

Once upon a time, the devil approached a man and made an offer:
"Either (a) I will give you one billion dollars, or (b) I will grant
you any wish if you pay me one billion dollars.
Of course, I get to choose whether I offer (a) or (b)."

The man was wary.  Did he need to sign over his soul?
No, said the devil, all the man need do is accept the offer.

The man pondered.  If he was offered (b) it was unlikely that he would
ever be able to buy the wish, but what was the harm in having the
opportunity available?

"I accept," said the man at last.  "Do I get (a) or (b)?"

The devil paused.  "I choose (b)."

The man was disappointed but not surprised.  That was that, he thought.
But the offer gnawed at him.  Imagine what he could do with his wish!
Many years passed, and the man began to accumulate money.  To get the
money he sometimes did bad things, and dimly he realised that
this must be what the devil had in mind.
Eventually he had his billion dollars, and the devil appeared again.

"Here is a billion dollars," said the man, handing over a valise
containing the money.  "Grant me my wish!"

The devil took possession of the valise.  Then he said, "Oh, did I say
(b) before?  I'm so sorry.  I meant (a).  It is my great pleasure to
give you one billion dollars."

And the devil handed back to the man the same valise that the man had
just handed to him.

(Parts of the above are adopted from "Call-by-Value is Dual to Call-by-Name",
Philip Wadler, _International Conference on Functional Programming_, 2003.)


#### Exercise `Classical` (stretch)

Consider the following principles:

* Excluded Middle: `A ⊎ ¬ A`, for all `A`
* Double Negation Elimination: `¬ ¬ A → A`, for all `A`
* Peirce's Law: `((A → B) → A) → A`, for all `A` and `B`.
* Implication as disjunction: `(A → B) → ¬ A ⊎ B`, for all `A` and `B`.
* De Morgan: `¬ (¬ A × ¬ B) → A ⊎ B`, for all `A` and `B`.

Show that each of these implies all the others.

<details><summary>Agda</summary>

```agda
-- Your code goes here
```
</details>

```tex
\func em=dne : (\Pi (A : \Prop) -> A || Not A) = (\Pi (A : \Prop) -> Not (Not A) -> A) =>
  propExt [=>] [<=]
  \where {
    \func [=>] (em : \Pi (A : \Prop) -> A || Not A) : \Pi (A : \Prop) -> Not (Not A) -> A =>
      \lam A => \case em A \with {
        | byLeft a => \lam _ => a
        | byRight not-a => \lam not-not-a => absurd (not-not-a not-a)
      }

    \func [<=] (dne : \Pi (A : \Prop) -> Not (Not A) -> A) : \Pi (A : \Prop) -> A || Not A =>
      \lam A => dne (A || Not A) em-irrefutable
  }

\func em=pl : (\Pi (A : \Prop) -> A || Not A) = (\Pi (A B : \Prop) -> ((A -> B) -> A) -> A) =>
  propExt [=>] [<=]
  \where {
    \func [=>] (em : \Pi (A : \Prop) -> A || Not A) : \Pi (A B : \Prop) -> ((A -> B) -> A) -> A =>
      \lam A B a->b->a => \case em A \with {
        | byLeft a => a
        | byRight not-a => a->b->a (\lam a => absurd (not-a a))
      }

    \func [<=] (pl : \Pi (A B : \Prop) -> ((A -> B) -> A) -> A) : \Pi (A : \Prop) -> A || Not A =>
      \lam A => pl (A || Not A) Empty (\lam p => absurd (em-irrefutable p))
  }

\func em=iad : (\Pi (A : \Prop) -> A || Not A) = (\Pi (A B : \Prop) -> (A -> B) -> Not A || B) =>
  propExt [=>] [<=]
  \where {
    \func [=>] (em : \Pi (A : \Prop) -> A || Not A) : \Pi (A B : \Prop) -> (A -> B) -> Not A || B =>
      \lam A B a->b => \case em A \with {
        | byLeft a => byRight (a->b a)
        | byRight not-a => byLeft not-a
      }

    \func [<=] (iad : \Pi (A B : \Prop) -> (A -> B) -> Not A || B) : \Pi (A : \Prop) -> A || Not A =>
      \lam A => \let not-a||a => iad A A (\lam a => a) \in \case not-a||a \with {
        | byLeft not-a => byRight not-a
        | byRight a => byLeft a
      }
  }

\func em=dm : (\Pi (A : \Prop) -> A || Not A) = (\Pi (A B : \Prop) -> Not (Not A && Not B) -> A || B) =>
  propExt [=>] [<=]
  \where {
    \func [=>] (em : \Pi (A : \Prop) -> A || Not A) : \Pi (A B : \Prop) -> Not (Not A && Not B) -> A || B =>
      \lam A B not-a&&not-b->empty => \case em A, em B \with {
        | byLeft a, _ => byLeft a
        | _, byLeft b => byRight b
        | byRight not-a, byRight npt-b => absurd (not-a&&not-b->empty (prod not-a npt-b))
      }

    \func [<=] (dm : \Pi (A B : \Prop) -> Not (Not A && Not B) -> A || B) : \Pi (A : \Prop) -> A || Not A =>
      \lam A => dm A (Not A) (\lam (prod not-a not-not-a) => not-not-a not-a)
  }
```


#### Exercise `Stable` (stretch)

Say that a formula is _stable_ if double negation elimination holds for it:
```agda
Stable : Set → Set
Stable A = ¬ ¬ A → A
```
Show that any negated formula is stable, and that the conjunction
of two stable formulas is stable.

<details><summary>Agda</summary>

```agda
-- Your code goes here
```
</details>

```tex
\func Stable (A : \Prop) : \Prop => Not (Not A) -> A

\func neg-Stable (A : \Prop) : Stable (Not A) =>
  \lam not-not-not-a a => Not-Not-Not-elim not-not-not-a a

\func conj-Stable {A B : \Prop} (sa : Stable A) (sb : Stable B) : Stable (A && B) =>
  \lam not-not-ab => prod
      (sa (\lam not-a => not-not-ab (\lam ab => not-a (proj1 ab))))
      (sb (\lam not-b => not-not-ab (\lam ab => not-b (proj2 ab))))
```

## Standard Prelude

Definitions similar to those in this chapter can be found in the standard library:
<details><summary>Agda</summary>

```agda
import Relation.Nullary using (¬_)
import Relation.Nullary.Negation using (contraposition)
```
</details>

```tex
\import Logic (Not)
```

## Unicode

This chapter uses the following unicode:

    ¬  U+00AC  NOT SIGN (\neg)
    ≢  U+2262  NOT IDENTICAL TO (\==n)
