#!/usr/bin/env runhaskell
import Control.Arrow ((&&&))
import Data.Bifunctor (second)
import Data.Char (toUpper)
import Data.Function (on)
import Data.List (nub, sortBy)

acronyms :: [(String, String)]
acronyms = [("P2P", "Peer-To-Peer"), ("PoW", "Proof of Work"), ("PoS", "Proof of Stake")]

main :: IO ()
main =
    writeFile "acros.tex" . concat $
    map ((\(a, s) -> concat ["\\acro{", a, "}{", s, "}\n"])) $
    sortBy (compare `on` (map toUpper . fst)) acronyms
